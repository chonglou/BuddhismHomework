package com.odong.buddhismhomework;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.TextView;
import android.widget.ToggleButton;

/**
 * Created by flamen on 15-2-8.
 */
public class SittingActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sitting);

        setTitle(R.string.title_sitting);

        int clock = getIntent().getIntExtra("clock", 30*60);
        setClock(clock);

        final CountDownTimer timer = new CountDownTimer(clock*1000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                setClock(millisUntilFinished/1000);
            }

            @Override
            public void onFinish() {
                setClock(0);
            }
        };
        findViewById(R.id.btn_sitting_play).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(((ToggleButton)findViewById(R.id.btn_sitting_play)).isChecked()){
                    timer.start();
                }
                else {
                    timer.cancel();
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (((ToggleButton)findViewById(R.id.btn_sitting_play)).isChecked()) {
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

        }
        else {
            super.onBackPressed();
        }

    }
    private void setClock(long seconds){
        TextView tv=(TextView)findViewById(R.id.tv_sitting_clock);
        tv.setText(String.format("%02d:%02d:%02d", seconds/3600, (seconds%3600)/60, (seconds%3600)%60));
    }

    private boolean isPlaying = false;
}
