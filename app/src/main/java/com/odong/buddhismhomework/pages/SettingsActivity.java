package com.odong.buddhismhomework.pages;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.TextView;

import com.odong.buddhismhomework.R;
import com.odong.buddhismhomework.models.CacheFile;
import com.odong.buddhismhomework.back.DownloadService;
import com.odong.buddhismhomework.back.ImportService;
import com.odong.buddhismhomework.utils.KvHelper;

import java.text.SimpleDateFormat;
import java.util.Date;


public class SettingsActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        setTexts();
        initEvents();
        initHosts();
    }

    private void setTexts() {
        KvHelper ddh = new KvHelper(this);
        ((Switch) findViewById(R.id.btn_settings_replay)).setChecked(ddh.get("mp3.replay", Boolean.class, false));

        ((TextView) findViewById(R.id.tv_setting_store)).setText(getString(R.string.tv_store_path, new CacheFile(this, "/").getRealFile()));
        ((TextView) findViewById(R.id.tv_setting_sync)).setText(date2string(R.string.tv_last_sync, ddh.get("sync.last", Date.class, null)));
        ((TextView) findViewById(R.id.tv_setting_import)).setText(date2string(R.string.tv_last_import, ddh.get("import.last", Date.class, null)));

    }

    private String date2string(int rid, Date date) {
        return getString(rid, date == null ? getString(R.string.lbl_never) : new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(date));
    }

    private void initEvents() {
        findViewById(R.id.btn_setting_sync).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder adb = new AlertDialog.Builder(SettingsActivity.this);
                adb.setTitle(R.string.action_refresh);
                adb.setMessage(R.string.dlg_download);
                adb.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startService(new Intent(SettingsActivity.this, DownloadService.class));
                    }
                });
                adb.setNegativeButton(android.R.string.no, null);
                adb.create().show();
            }
        });
        findViewById(R.id.btn_settings_replay).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new KvHelper(SettingsActivity.this).set("mp3.replay", ((Switch) v).isChecked());
            }
        });

        findViewById(R.id.btn_setting_import).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder adb = new AlertDialog.Builder(SettingsActivity.this);
                adb.setTitle(R.string.action_import);
                adb.setMessage(R.string.dlg_import);
                adb.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startService(new Intent(SettingsActivity.this, ImportService.class));
                    }
                });
                adb.setNegativeButton(android.R.string.no, null);
                adb.create().show();

            }
        });
    }

    private void initHosts() {
        RadioGroup rg = (RadioGroup) findViewById(R.id.rg_setting_hosts);

        int type = new KvHelper(this).get("host.type", Integer.class, R.id.btn_setting_home_dropbox);

        rg.check(type);

        rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                new KvHelper(SettingsActivity.this).set("host.type", checkedId);
            }
        });

    }

}
