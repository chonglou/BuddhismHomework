package com.odong.buddhismhomework;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Switch;
import android.widget.TextView;

import com.odong.buddhismhomework.utils.DwDbHelper;

import java.util.Date;


public class SettingsActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        initEvents();
        setTexts();

    }

    private void setTexts() {
        DwDbHelper dh = new DwDbHelper(this);
        ((Switch) findViewById(R.id.btn_settings_replay)).setChecked(dh.get("mp3.replay", Boolean.class) == Boolean.TRUE);
        Date last_sync = dh.get("sync.last", Date.class);

        ((TextView) findViewById(R.id.tv_setting_sync)).setText(getString(R.string.tv_last_sync,
                last_sync == null ? getString(R.string.lbl_never) : last_sync.toString()));

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
                        Intent intent = new Intent(SettingsActivity.this, DownloadService.class);
                        intent.putExtra("action", "sync");
                        intent.putExtra("redo", true);
                        startService(intent);
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

}
