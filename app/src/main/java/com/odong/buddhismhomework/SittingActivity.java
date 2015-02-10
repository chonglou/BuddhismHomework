package com.odong.buddhismhomework;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.ToggleButton;

import java.util.Arrays;

/**
 * Created by flamen on 15-2-8.
 */
public class SittingActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sitting);

        setTitle(R.string.title_sitting);


        int clock = getIntent().getIntExtra("clock", 30);
        initSpinner(clock);
        setClock(clock);

    }

    @Override
    public void onBackPressed() {
        if (((ToggleButton) findViewById(R.id.btn_sitting_play)).isChecked()) {
            AlertDialog.Builder adb = new AlertDialog.Builder(this);
            adb.setMessage(R.string.lbl_will_pause);
            adb.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    SittingActivity.this.finish();
                }
            });
            adb.setNegativeButton(android.R.string.no, null);
            adb.setCancelable(false);
            adb.create().show();

        } else {
            super.onBackPressed();
        }

    }

    private void initSpinner(int clock) {
        Spinner spinner = (Spinner) findViewById(R.id.sp_sitting_clocks);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.sitting_clocks_titles,
                android.R.layout.simple_spinner_item);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                setClock(getResources().getIntArray(R.array.sitting_clocks_items)[position]);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                setClock(30);
            }
        });

        
        int[] vals = getResources().getIntArray(R.array.sitting_clocks_items);
        for(int i=0; i<vals.length; i++){
            if(vals[i] == clock){
                spinner.setSelection(i);
                break;
            }
        }

    }

    private void setClock(int clock) {

        clock = clock * 60;
        setClockText(clock);

        final CountDownTimer timer = new CountDownTimer(clock * 1000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                setClockText(millisUntilFinished / 1000);
            }

            @Override
            public void onFinish() {
                setClockText(0);
                MediaPlayer.create(SittingActivity.this, R.raw.yinqing).start();
            }
        };
        findViewById(R.id.btn_sitting_play).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Spinner spinner = (Spinner)findViewById(R.id.sp_sitting_clocks);
                if (((ToggleButton) findViewById(R.id.btn_sitting_play)).isChecked()) {
                    timer.start();
                    spinner.setEnabled(false);
                } else {
                    timer.cancel();
                    spinner.setEnabled(true);
                }
            }
        });
    }

    private void setClockText(long seconds) {
        TextView tv = (TextView) findViewById(R.id.tv_sitting_clock);
        tv.setText(String.format("%02d:%02d:%02d", seconds / 3600, (seconds % 3600) / 60, (seconds % 3600) % 60));
    }

}
