package com.odong.buddhismhomework;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.odong.buddhismhomework.models.Homework;
import com.odong.buddhismhomework.utils.XmlHelper;

import java.io.InputStream;
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
        String type = getIntent().getStringExtra("type");
        homeworkList = new XmlHelper(this).getHomeworkList(type);

        ((TextView) findViewById(R.id.tv_homework_content)).setMovementMethod(new ScrollingMovementMethod());
        initSpinner();
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

    private void initSpinner() {

        Spinner spinner = (Spinner) findViewById(R.id.sp_homework);
        ArrayAdapter<Homework> adapter = new ArrayAdapter<Homework>(this,
                android.R.layout.simple_spinner_item, homeworkList);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                initHomework(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                initHomework(0);
            }
        });

    }


    private void initHomework(int index) {

        Homework hw = homeworkList.get(index);

        setTitle(hw.getName());

        TextView tv = (TextView) findViewById(R.id.tv_homework_content);
        try {
            StringBuilder sb = new StringBuilder();
            for (Integer i : hw.getIncantations()) {


                InputStream is = getResources().openRawResource(i);
                byte[] buf = new byte[1024];
                int len;
                while ((len = is.read(buf)) > 0) {
                    sb.append(new String(buf, 0, len));
                }
                sb.append("\n\n");

            }
            tv.setText(sb.toString());
            tv.scrollTo(0, 0);
        } catch (Resources.NotFoundException e) {
            Log.e("读取文件", hw.getName(), e);
            tv.setText(R.string.lbl_error_io);
        } catch (Exception e) {
            Log.e("读取文件", hw.getName(), e);
            tv.setText(R.string.lbl_error_io);
        }

    }

    private List<Homework> homeworkList;

}
