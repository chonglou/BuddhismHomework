package com.odong.buddhismhomework.pages;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.GridView;

import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.odong.buddhismhomework.R;
import com.odong.buddhismhomework.models.CacheFile;
import com.odong.buddhismhomework.models.NavIcon;
import com.odong.buddhismhomework.pages.audio.SectionActivity;
import com.odong.buddhismhomework.pages.reading.CatalogActivity;
import com.odong.buddhismhomework.pages.reading.FavoritesActivity;
import com.odong.buddhismhomework.utils.HttpClient;
import com.odong.buddhismhomework.utils.KvHelper;
import com.odong.buddhismhomework.utils.WidgetHelper;
import com.odong.buddhismhomework.widgets.NavIconAdapter;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initGrid();
        checkVersion();
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
            case R.id.action_storage:
                try {
                    Intent storage = new Intent();
                    storage.setAction(Intent.ACTION_GET_CONTENT);
                    storage.setData(Uri.parse(new CacheFile(this, "").getRootUrl()));
                    storage.setType("file/*");
                    startActivity(storage);
                } catch (ActivityNotFoundException e) {
                    Log.e("文件浏览", "出错", e);
                }
                break;
            case R.id.action_favorites:
                startActivity(new Intent(MainActivity.this, FavoritesActivity.class));
                break;
            case R.id.action_sync:
                new WidgetHelper(MainActivity.this).showSyncDialog("all.zip");
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
                if (new KvHelper(MainActivity.this).get("sync://musics.zip", Date.class, null) == null) {
                    new WidgetHelper(MainActivity.this).showSyncDialog("musics.zip");
                } else {
                    Intent intent = new Intent(MainActivity.this, SectionActivity.class);
                    intent.putExtra("type", "courses");
                    startActivity(intent);
                }
            }
        }));
        icons.add(new NavIcon(R.string.title_books, R.drawable.ic_books, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (new KvHelper(MainActivity.this).get("sync://cbeta.sql", Date.class, null) == null) {
                    new WidgetHelper(MainActivity.this).showSyncDialog("cbeta.sql");
                } else {
                    Intent intent = new Intent(MainActivity.this, CatalogActivity.class);
                    intent.putExtra("type", "fav");
                    startActivity(intent);
                }
            }
        }));
        icons.add(new NavIcon(R.string.title_musics, R.drawable.ic_musics, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (new KvHelper(MainActivity.this).get("sync://musics.zip", Date.class, null) == null) {
                    new WidgetHelper(MainActivity.this).showSyncDialog("musics.zip");
                } else {
                    Intent intent = new Intent(MainActivity.this, SectionActivity.class);
                    intent.putExtra("type", "musics");
                    startActivity(intent);
                }
            }
        }));

        icons.add(new NavIcon(R.string.title_videos, R.drawable.ic_videos, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (new KvHelper(MainActivity.this).get("sync://videos.sql", Date.class, null) == null) {
                    new WidgetHelper(MainActivity.this).showSyncDialog("videos.sql");
                } else {
                    Intent intent = new Intent(MainActivity.this, VideoActivity.class);
                    intent.putExtra("type", "channel");
                    startActivity(intent);
                }
            }
        }));

        icons.add(new NavIcon(R.string.title_dict, R.drawable.ic_dict, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (new KvHelper(MainActivity.this).get("sync://dict.zip", Date.class, null) == null) {
                    new WidgetHelper(MainActivity.this).showSyncDialog("dict.zip");
                } else {
                    startActivity(new Intent(MainActivity.this, DictActivity.class));
                }
            }
        }));

        icons.add(new NavIcon(R.string.title_dzj, R.drawable.ic_dzj, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (new KvHelper(MainActivity.this).get("sync://cbeta.sql", Date.class, null) == null) {
                    new WidgetHelper(MainActivity.this).showSyncDialog("cbeta.sql");
                } else {
                    Intent intent = new Intent(MainActivity.this, CatalogActivity.class);
                    intent.putExtra("type", "dzj");
                    startActivity(intent);
                }

            }
        }));

        icons.add(new NavIcon(R.string.title_ddc, R.drawable.ic_ddc, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (new KvHelper(MainActivity.this).get("sync://ddc.zip", Date.class, null) == null) {
                    new WidgetHelper(MainActivity.this).showSyncDialog("ddc.zip");
                } else {
                    new WidgetHelper(MainActivity.this).showDdc(
                            "file://" + new CacheFile(MainActivity.this, "/ddc" + "/mobile/index.php").getRealFile().getAbsolutePath(),
                            getString(R.string.title_ddc)
                    );
                }
            }
        }));

        icons.add(new NavIcon(R.string.title_help, R.drawable.ic_help, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder adbAboutMe = new AlertDialog.Builder(MainActivity.this);
                adbAboutMe.setMessage(R.string.lbl_help).setTitle(R.string.action_help);
                adbAboutMe.setPositiveButton(android.R.string.ok, null);
                adbAboutMe.create().show();
            }
        }));


        ((GridView) findViewById(R.id.gv_main_icons)).setAdapter(new NavIconAdapter(this, icons));

    }

    private void checkVersion() {
        Date lc = new KvHelper(this).get("version.last_check", Date.class, null);
        if (lc != null && (lc.getTime() - new Date().getTime() <= 1000 * 60 * 60 * 24)) {
            return;
        }
        new KvHelper(MainActivity.this).set("version.last_check", new Date());
        new AsyncTask<String, Void, Void>() {

            @Override
            protected Void doInBackground(String... params) {
                String url = "https://api.github.com/repos/chonglou/BuddhismHomework/tags";
                try {
                    String pl = HttpClient.get(url);

                    PackageInfo pi = getPackageManager().getPackageInfo(getPackageName(), 0);
                    Log.d("当前版本号", pi.versionName);
                    Log.d("版本列表", pl);
                    String version = new JsonParser().parse(pl).getAsJsonArray().get(0).getAsJsonObject().get("name").toString();

                    if (version.compareTo(pi.versionName) > 0) {
                        Message msg = new Message();
                        msg.what = UPGRADE;
                        versionHandler.sendMessage(msg);
                    }
                } catch (JsonParseException | IOException | URISyntaxException | PackageManager.NameNotFoundException e) {
                    new WidgetHelper(MainActivity.this).toast(getString(R.string.lbl_error_check_version), true);
                }

                return null;
            }
        }.execute();


    }


    final int UPGRADE = 1;
    final int ERROR = 2;
    private Handler versionHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case UPGRADE:
                    AlertDialog.Builder adb = new AlertDialog.Builder(MainActivity.this);
                    adb.setTitle(R.string.dlg_title_check_version);
                    adb.setMessage(R.string.dlg_check_version);
                    adb.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            String name = getPackageName();
                            try {
                                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + name)));
                            } catch (ActivityNotFoundException e) {
                                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + name)));
                            }
                        }
                    });
                    adb.setNegativeButton(android.R.string.no, null);
                    adb.create().show();
                    break;
                case ERROR:
                    break;

            }
            return true;
        }
    });


}
