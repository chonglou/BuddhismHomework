package com.odong.buddhismhomework;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.GridView;

import com.odong.buddhismhomework.models.NavIcon;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initGrid();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_favorites:
                startActivity(new Intent(MainActivity.this, FavoritesActivity.class));
                break;
            case R.id.action_refresh:
                AlertDialog.Builder adb = new AlertDialog.Builder(this);
                adb.setTitle(R.string.action_refresh);
                adb.setMessage(R.string.dlg_download);
                adb.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startService(new Intent(MainActivity.this, DownloadService.class));
                    }
                });
                adb.setNegativeButton(android.R.string.no, null);
                adb.create().show();
                break;
            case R.id.action_settings:
                startActivity(new Intent(MainActivity.this, SettingsActivity.class));
                break;
            case R.id.action_about_me:
                AlertDialog.Builder adbAboutMe = new AlertDialog.Builder(this);
                adbAboutMe.setMessage(R.string.lbl_about_me).setTitle(R.string.action_about_me);
                adbAboutMe.setPositiveButton(android.R.string.ok, null);
                adbAboutMe.create().show();
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;

    }

    private void initGrid() {
        List<NavIcon> icons = new ArrayList<NavIcon>();
        icons.add(new NavIcon(R.string.title_morning, R.drawable.ic_morning, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, HomeworkActivity.class);
                intent.putExtra("type", "morning");
                startActivity(intent);
            }
        }));
        icons.add(new NavIcon(R.string.title_evening, R.drawable.ic_evening, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, HomeworkActivity.class);
                intent.putExtra("type", "evening");
                startActivity(intent);
            }
        }));
        icons.add(new NavIcon(R.string.title_sitting, R.drawable.ic_sitting, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, SittingActivity.class));
            }
        }));
        icons.add(new NavIcon(R.string.title_courses, R.drawable.ic_courses, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, BooksActivity.class);
                intent.putExtra("type", "courses");
                startActivity(intent);
            }
        }));
        icons.add(new NavIcon(R.string.title_books, R.drawable.ic_books, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, BooksActivity.class);
                intent.putExtra("type", "books");
                startActivity(intent);
            }
        }));
        icons.add(new NavIcon(R.string.title_musics, R.drawable.ic_musics, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, BooksActivity.class);
                intent.putExtra("type", "musics");
                startActivity(intent);
            }
        }));

        icons.add(new NavIcon(R.string.title_videos, R.drawable.ic_videos, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, VideoBooksActivity.class));
            }
        }));
        icons.add(new NavIcon(R.string.title_dict, R.drawable.ic_dict, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, DictActivity.class));
            }
        }));
        icons.add(new NavIcon(R.string.title_dzj, R.drawable.ic_dzj, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, DzjListActivity.class));
            }
        }));


        ((GridView) findViewById(R.id.gv_main_icons)).setAdapter(new NavIconAdapter(this, icons));

    }

}
