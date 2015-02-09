package com.odong.buddhismhomework;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.SparseArray;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;


public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initButtonEvent();
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
            case R.id.action_refresh:
                AlertDialog.Builder adb = new AlertDialog.Builder(this);
                adb.setTitle(R.string.action_refresh);
                adb.setMessage(R.string.lbl_download);
                adb.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(MainActivity.this, BackgroundService.class);
                        intent.putExtra("action", "sync");
                        startService(intent);
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

    private void initButtonEvent() {
        SparseArray<View.OnClickListener> events = new SparseArray<View.OnClickListener>();
        events.put(R.id.btn_main_morning, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, HomeworkActivity.class);
                intent.putExtra("type", "morning");
                startActivity(intent);
            }
        });

        events.put(R.id.btn_main_night, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, HomeworkActivity.class);
                intent.putExtra("type", "night");
                startActivity(intent);
            }
        });

        events.put(R.id.btn_main_sitting, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, SittingActivity.class));
            }
        });

        events.put(R.id.btn_main_courses, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ItemsActivity.class);
                intent.putExtra("lvId", R.array.lv_courses);
                startActivity(intent);
            }
        });
        events.put(R.id.btn_main_books, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ItemsActivity.class);
                intent.putExtra("lvId", R.array.lv_books);
                startActivity(intent);
            }
        });
        events.put(R.id.btn_main_music, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ItemsActivity.class);
                intent.putExtra("lvId", R.array.lv_musics);
                startActivity(intent);
            }
        });

        for (int i = 0; i < events.size(); i++) {
            findViewById(events.keyAt(i)).setOnClickListener(events.valueAt(i));
        }

    }


}
