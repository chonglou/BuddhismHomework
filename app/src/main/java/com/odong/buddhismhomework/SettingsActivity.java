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
        DwDbHelper dh = new DwDbHelper(this);
        ((Switch) findViewById(R.id.btn_settings_replay)).setChecked(dh.get("mp3.replay", Boolean.class) == Boolean.TRUE);

        ((TextView) findViewById(R.id.tv_setting_store)).setText(getString(R.string.tv_store_path, new CacheFile(this, "/").getRealFile()));
        Date last_sync = dh.get("sync.last", Date.class);
        ((TextView) findViewById(R.id.tv_setting_sync)).setText(getString(R.string.tv_last_sync,
                last_sync == null ? getString(R.string.lbl_never) : new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(last_sync)));

    }

    private void initEvents() {
        findViewById(R.id.btn_setting_sync).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder adb = new AlertDialog.Builder(SettingsActivity.this);
                adb.setTitle(R.string.action_refresh);
                adb.setMessage(R.string.lbl_download);
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
                new DwDbHelper(SettingsActivity.this).set("mp3.replay", ((Switch) v).isChecked());

            }
        });
    }

    private void initHosts() {
        RadioGroup rg = (RadioGroup) findViewById(R.id.rg_setting_hosts);
        Integer type = new DwDbHelper(this).get("host.type", Integer.class);
        if (type == null) {
            type = R.id.btn_setting_home_dropbox;
        }
        rg.check(type);

        rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                new DwDbHelper(SettingsActivity.this).set("host.type", checkedId);
            }
        });

    }

}
