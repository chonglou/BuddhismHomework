package com.odong.buddhismhomework;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.Switch;

import com.odong.buddhismhomework.utils.DwDbHelper;


public class SettingsActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        initEvents();
        ((Switch)findViewById(R.id.btn_settings_replay)).setChecked(new DwDbHelper(this).get("mp3.replay", Boolean.class)==Boolean.TRUE);

    }

    private void initEvents(){
        findViewById(R.id.btn_setting_clear).setOnClickListener(new View.OnClickListener() {
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
                Log.d("wahaha", ""+((Switch)v).isChecked());
                new DwDbHelper(SettingsActivity.this).set("mp3.replay", ((Switch)v).isChecked());

            }
        });
    }

}
