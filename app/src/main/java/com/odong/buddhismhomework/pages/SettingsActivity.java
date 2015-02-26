package com.odong.buddhismhomework.pages;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;

import com.odong.buddhismhomework.R;
import com.odong.buddhismhomework.models.CacheFile;
import com.odong.buddhismhomework.models.Calendar;
import com.odong.buddhismhomework.services.DownloadService;
import com.odong.buddhismhomework.services.ImportService;
import com.odong.buddhismhomework.utils.AlarmHelper;
import com.odong.buddhismhomework.utils.KvHelper;

import java.text.DateFormat;
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
        KvHelper kv = new KvHelper(this);
        ((Switch) findViewById(R.id.btn_settings_replay)).setChecked(kv.get("mp3.replay", Boolean.class, false));
        ((Switch) findViewById(R.id.btn_settings_earphone)).setChecked(kv.get("mp3.earphone", Boolean.class, false));

        ((TextView) findViewById(R.id.tv_setting_store)).setText(getString(R.string.tv_store_path, new CacheFile(this, "/").getRealFile()));
        ((TextView) findViewById(R.id.tv_setting_sync)).setText(date2string(R.string.tv_last_sync, kv.get("sync.last", Date.class, null)));
        ((TextView) findViewById(R.id.tv_setting_import)).setText(date2string(R.string.tv_last_import, kv.get("import.last", Date.class, null)));

        initCalendar(R.id.tv_setting_morning,
                R.string.tv_setting_morning,
                R.id.btn_setting_morning,
                "homework.morning.cal");

        initCalendar(R.id.tv_setting_evening,
                R.string.tv_setting_evening,
                R.id.btn_setting_evening,
                "homework.evening.cal");
    }


    private void initCalendar(final int tv, final int tvs, int btn, final String key) {
        Calendar cal = new KvHelper(this).get(key, Calendar.class, new Calendar());
        ((TextView) findViewById(tv)).setText(getString(tvs, cal.getHour(), cal.getMinute()));
        Switch sw = (Switch) findViewById(btn);
        sw.setChecked(cal.isEnable());

        sw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Switch sw = (Switch) v;
                KvHelper kv = new KvHelper(SettingsActivity.this);
                Calendar cal = kv.get(key, Calendar.class, new Calendar());

                if (sw.isChecked()) {
                    AlertDialog.Builder adb = new AlertDialog.Builder(SettingsActivity.this);
                    final TimePicker picker = new TimePicker(SettingsActivity.this);
                    picker.setIs24HourView(true);
                    picker.setCurrentHour(cal.getHour());
                    picker.setCurrentMinute(cal.getMinute());
                    adb.setView(picker);
                    adb.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            KvHelper kv = new KvHelper(SettingsActivity.this);
                            Calendar cal = kv.get(key, Calendar.class, new Calendar());
                            cal.setHour(picker.getCurrentHour());
                            cal.setMinute(picker.getCurrentMinute());
                            cal.setEnable(true);
                            kv.set(key, cal);

                            ((TextView) findViewById(tv)).setText(getString(tvs, cal.getHour(), cal.getMinute()));
                            new AlarmHelper(SettingsActivity.this).resetAlarms();
                            Log.d("设置闹钟", cal.toString());
                        }
                    });
                    adb.setNegativeButton(android.R.string.cancel, null);
                    adb.create().show();
//                    new TimePickerDialog(SettingsActivity.this, new TimePickerDialog.OnTimeSetListener() {
//                        @Override
//                        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
//                            KvHelper kv = new KvHelper(SettingsActivity.this);
//                            Calendar cal = kv.get(key, Calendar.class, new Calendar());
//                            cal.setHour(hourOfDay);
//                            cal.setMinute(minute);
//                            cal.setEnable(true);
//                            kv.set(key, cal);
//
//                            ((TextView) findViewById(tv)).setText(getString(tvs, cal.getHour(), cal.getMinute()));
//                            new WidgetHelper(SettingsActivity.this).resetAlarms();
//                            Log.d("设置闹钟", cal.toString());
//                        }
//                    }, cal.getHour(), cal.getMinute(), true).show();

                } else {
                    cal.setEnable(false);
                    kv.set(key, cal);
                    Log.d("禁止闹钟", cal.toString());
                    ((TextView) findViewById(tv)).setText(getString(tvs, cal.getHour(), cal.getMinute()));
                    new AlarmHelper(SettingsActivity.this).resetAlarms();
                }

            }
        });

    }


    private String date2string(int rid, Date date) {
        return getString(rid, date == null ? getString(R.string.lbl_never) : DateFormat.getDateTimeInstance().format(date));
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

        findViewById(R.id.btn_settings_earphone).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new KvHelper(SettingsActivity.this).set("mp3.earphone", ((Switch) v).isChecked());
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
