package com.odong.buddhismhomework;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.odong.buddhismhomework.models.CacheFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;


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
            case R.id.action_download:
                List<CacheFile> files = new ArrayList<CacheFile>();
                files.addAll(CacheFile.all(this, "books", R.array.lv_books, "txt"));
                files.addAll(CacheFile.all(this, "courses", R.array.lv_courses, "mp3", "txt"));
                files.addAll(CacheFile.all(this, "musics", R.array.lv_musics, "mp3"));

                initDownloadDialog();
                dlgDownload.show();
                new Downloader().execute(files.toArray(new CacheFile[files.size()]));

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
        //Map<Integer, View.OnClickListener> events = new HashMap<Integer, View.OnClickListener>();
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


    private void initDownloadDialog() {
        dlgDownload = new ProgressDialog(this);
        dlgDownload.setTitle(R.string.action_download);
        dlgDownload.setMessage(getString(R.string.lbl_download));
        dlgDownload.setCancelable(false);
        dlgDownload.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);

    }


    private class Downloader extends AsyncTask<CacheFile, String, String> {

        @Override
        protected String doInBackground(CacheFile... files) {
            dlgDownload.setMax(files.length);
            dlgDownload.setProgress(0);

            try {
                for (CacheFile cf : files) {
                    cf.sync();
                    dlgDownload.incrementProgressBy(1);
                }


            } catch (MalformedURLException e) {
                Log.e("下载", "地址错误", e);
            } catch (IOException e) {
                Log.e("下载", "IO错误", e);
            } catch (SecurityException e) {
                Log.e("下载", "安全错误", e);
            }

            dlgDownload.dismiss();
            return null;
        }
    }


    private ProgressDialog dlgDownload;
}
