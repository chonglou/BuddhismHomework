package com.odong.buddhismhomework;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.TextView;

import com.odong.buddhismhomework.models.CacheFile;
import com.odong.buddhismhomework.utils.DwDbHelper;

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
        DwDbHelper ddh = new DwDbHelper(this);
        ((Switch) findViewById(R.id.btn_settings_replay)).setChecked(ddh.get("mp3.replay", Boolean.class) == Boolean.TRUE);

        ((TextView) findViewById(R.id.tv_setting_store)).setText(getString(R.string.tv_store_path, new CacheFile(this, "/").getRealFile()));
        ((TextView) findViewById(R.id.tv_setting_sync)).setText(date2string(R.string.tv_last_sync, ddh.get("sync.last", Date.class)));
        ((TextView) findViewById(R.id.tv_setting_import)).setText(date2string(R.string.tv_last_import, ddh.get("import.last", Date.class)));

        ddh.close();
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
                DwDbHelper ddh = new DwDbHelper(SettingsActivity.this);
                ddh.set("mp3.replay", ((Switch) v).isChecked());
                ddh.close();

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
        DwDbHelper ddh = new DwDbHelper(this);
        Integer type = ddh.get("host.type", Integer.class);
        ddh.close();
        if (type == null) {
            type = R.id.btn_setting_home_dropbox;
        }
        rg.check(type);

        rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                DwDbHelper ddh = new DwDbHelper(SettingsActivity.this);
                ddh.set("host.type", checkedId);
                ddh.close();
            }
        });

    }

}
