package com.odong.buddhismhomework;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.odong.buddhismhomework.models.CacheFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by flamen on 15-2-8.
 */
public class HomeworkActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homework);


        String homework = getIntent().getStringExtra("type");

        initSpinner(homework);
        initHomework(homework, 0);
    }

    @Override
    public void onBackPressed() {
        if (((ToggleButton) findViewById(R.id.btn_homework_play)).isChecked()) {
            AlertDialog.Builder adb = new AlertDialog.Builder(this);
            adb.setMessage(R.string.lbl_will_pause);
            adb.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    HomeworkActivity.this.finish();
                }
            });
            adb.setNegativeButton(android.R.string.no, null);
            adb.setCancelable(false);
            adb.create().show();

        } else {
            super.onBackPressed();
        }

    }

    private void initSpinner(final String type) {
        List<String> items = new ArrayList<String>();
        for (String s : getResources().getStringArray(titles(type))) {
            items.add(new CacheFile(this, "homework", s, "txt").getTitle());
        }
        Spinner spinner = (Spinner) findViewById(R.id.sp_homework);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, items);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                initHomework(type, position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                initHomework(type, 0);
            }
        });

    }

    private int titles(String type) {
        int homework;
        if (type.equals("morning")) {
            homework = R.array.homework_morning_titles;
        } else if (type.equals("night")) {
            homework = R.array.homework_night_titles;
        } else {
            throw new IllegalArgumentException();
        }
        return homework;
    }

    private void initHomework(String type, int index) {
        CacheFile cf = new CacheFile(this, "homework", getResources().getStringArray(titles(type))[index], "txt");
        setTitle(cf.getTitle());

        TextView tv = (TextView) findViewById(R.id.tv_homework_content);
        if (!cf.exists()) {
            tv.setText(R.string.lbl_empty);
            return;
        }

        try {
            tv.setText(cf.read());
        } catch (IOException e) {
            Log.e("读取文件", cf.getRealName(), e);
            tv.setText(R.string.lbl_error_io);
        }

        tv.setMovementMethod(new ScrollingMovementMethod());
    }


}
