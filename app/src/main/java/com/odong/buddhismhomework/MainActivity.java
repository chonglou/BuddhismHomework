package com.odong.buddhismhomework;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initButtonEvent();
        initDownloadDialog();
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
                List<String> names = new ArrayList<String>();
                for (String s : getResources().getStringArray(R.array.lv_books)) {
                    names.add("books/" + s + ".txt");
                }
                for (String s : getResources().getStringArray(R.array.lv_musics)) {
                    names.add("musics/" + s.split("\\|")[0] + ".mp3");
                }
                for (String s : getResources().getStringArray(R.array.lv_courses)) {
                    names.add("courses/" + s + ".txt");
                    names.add("courses/" + s + ".mp3");
                }
                new Downloader().execute(names.toArray(new String[names.size()]));
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
                Intent intent = new Intent(MainActivity.this, CourseActivity.class);
                intent.putExtra("name", "morning");
                startActivity(intent);
            }
        });

        events.put(R.id.btn_main_night, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, CourseActivity.class);
                intent.putExtra("name", "night");
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
                intent.putExtra("name", "courses");
                startActivity(intent);
            }
        });
        events.put(R.id.btn_main_books, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ItemsActivity.class);
                intent.putExtra("name", "books");
                startActivity(intent);
            }
        });
        events.put(R.id.btn_main_music, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ItemsActivity.class);
                intent.putExtra("name", "musics");
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
        dlgDownload.setCancelable(true);
        dlgDownload.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);

    }


    private class Downloader extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dlgDownload.setProgress(0);
            dlgDownload.show();
        }

        @Override
        protected String doInBackground(String... names) {
            dlgDownload.setMax(names.length);

            try {
                for (String name : names) {

                    String fn = name.replace('/', '-');
                    if (!new File(fn).exists()) {
                        DataInputStream dis = new DataInputStream(new URL("https://raw.githubusercontent.com/chonglou/BuddhismHomework/master/tools/" + name).openStream());

                        byte[] buf = new byte[1024];
                        int len;

                        FileOutputStream fos = openFileOutput(fn, Context.MODE_PRIVATE);
                        while ((len = dis.read(buf)) > 0) {
                            fos.write(buf, 0, len);
                        }
                        fos.flush();
                    }
                    dlgDownload.incrementProgressBy(1);
                }

            } catch (MalformedURLException e) {
                Log.e("下载", "地址错误", e);
            } catch (IOException e) {
                Log.e("下载", "IO错误", e);
            } catch (SecurityException e) {
                Log.e("下载", "安全错误", e);
            }

            boolean success = dlgDownload.getProgress() == dlgDownload.getMax();
            dlgDownload.dismiss();
            if (!success) {
                new AlertDialog.Builder(
                        MainActivity.this).setMessage(
                        R.string.lbl_error_download).setPositiveButton(
                        android.R.string.ok, null).create().show();
            }
            return null;
        }
    }


    private ProgressDialog dlgDownload;
}
