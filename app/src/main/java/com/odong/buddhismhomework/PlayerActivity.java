package com.odong.buddhismhomework;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.DialogInterface;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.odong.buddhismhomework.models.CacheFile;

import java.io.IOException;

/**
 * Created by flamen on 15-2-8.
 */
public class PlayerActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);


        loadFiles();
        initTextView();
        initMp3View();
    }

    @Override
    public void onBackPressed() {
        if (((ToggleButton) findViewById(R.id.btn_player)).isChecked()) {
            AlertDialog.Builder adb = new AlertDialog.Builder(this);
            adb.setMessage(R.string.lbl_will_pause);
            adb.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    PlayerActivity.this.finish();
                }
            });
            adb.setNegativeButton(android.R.string.no, null);
            adb.setCancelable(false);
            adb.create().show();

        } else {
            super.onBackPressed();
        }

    }

    private void initMp3View() {
        if (mp3File == null) {
            findViewById(R.id.gl_player_mp3).setVisibility(View.GONE);
            return;
        }

        setTitle(mp3File.getTitle());

        mp3Player = MediaPlayer.create(this, Uri.parse(getFileStreamPath(mp3File.getRealName()).getAbsolutePath()));
        mp3Player.setAudioStreamType(AudioManager.STREAM_MUSIC);

        mp3Seeker = (SeekBar) findViewById(R.id.sb_player);

        mp3Seeker.setProgress(0);
        mp3Seeker.setMax(mp3Player.getDuration());
        mp3Seeker.setClickable(false);


        findViewById(R.id.btn_player).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ToggleButton tb = (ToggleButton) findViewById(R.id.btn_player);
                if (tb.isChecked()) {
                    mp3Player.start();
                    durationHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mp3Seeker.setProgress(mp3Player.getCurrentPosition());
                            durationHandler.postDelayed(this, 100);
                        }
                    }, 100);
                } else {
                    mp3Player.pause();
                }
            }
        });
    }

    private void loadFiles() {

        int id = getIntent().getIntExtra("id", 0);
        switch (getIntent().getIntExtra("type", 0)) {
            case R.array.lv_courses:
                txtFile = new CacheFile(this, "courses", R.array.lv_courses, id, "txt");
                mp3File = new CacheFile(this, "courses", R.array.lv_courses, id, "mp3");
                break;
            case R.array.lv_books:
                txtFile = new CacheFile(this, "books", R.array.lv_books, id, "txt");
                mp3File = null;
                break;
            case R.array.lv_musics:
                txtFile = null;
                mp3File = new CacheFile(this, "musics", R.array.lv_musics, id, "mp3");
                break;
        }
    }

    private void initTextView() {
        TextView tv = (TextView) findViewById(R.id.tv_player_content);
        if (txtFile == null) {
            tv.setText(R.string.lbl_empty);
            return;
        }


        setTitle(txtFile.getTitle());
        try {
            tv.setText(txtFile.read());
        } catch (IOException e) {
            Log.e("读取文件", txtFile.getRealName(), e);
            tv.setText(R.string.lbl_error_io);
        }


        tv.setMovementMethod(new ScrollingMovementMethod());

    }


    private CacheFile txtFile;
    private CacheFile mp3File;
    private SeekBar mp3Seeker;
    private MediaPlayer mp3Player;
    private BroadcastReceiver receiver;
    private Handler durationHandler = new Handler();
}
