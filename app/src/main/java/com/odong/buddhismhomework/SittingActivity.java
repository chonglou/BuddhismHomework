package com.odong.buddhismhomework;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

/**
 * Created by flamen on 15-2-8.
 */
public class SittingActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sitting);

        setClock(getIntent().getIntExtra("clock", 30*60));
        findViewById(R.id.btn_sitting_play).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    private void setClock(int seconds){
        TextView tv=(TextView)findViewById(R.id.tv_sitting_clock);
        tv.setText(String.format("%02d:%02d:%02d", seconds/3600, (seconds%3600)/60, (seconds%3600)%60));
    }

    private boolean isPlaying = false;
}
