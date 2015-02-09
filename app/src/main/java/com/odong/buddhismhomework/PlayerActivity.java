package com.odong.buddhismhomework;

import android.app.Activity;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;

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

    private void initMp3View() {
        if (mp3File == null) {
            findViewById(R.id.gl_player_mp3).setVisibility(View.GONE);
            return;
        }

        setTitle(mp3File.getTitle());
        mp3Player = MediaPlayer.create(this, Uri.parse(getFileStreamPath(mp3File.getRealName()).getAbsolutePath()));
        mp3Player.setAudioStreamType(AudioManager.STREAM_MUSIC);

        mp3Seeker = (SeekBar) findViewById(R.id.sb_player);
        finalTime = mp3Player.getDuration();
        mp3Seeker.setMax(finalTime);
        mp3Seeker.setClickable(false);


        SparseArray<View.OnClickListener> events = new SparseArray<View.OnClickListener>();
        events.put(R.id.btn_player_play, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mp3Player.start();
                timeElapsed = mp3Player.getCurrentPosition();
                mp3Seeker.setProgress(timeElapsed);
                durationHandler.postDelayed(updateSeekerTime, 100);
            }
        });
        events.put(R.id.btn_player_forward, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ((timeElapsed + forwardTime) <= finalTime) {
                    timeElapsed = timeElapsed + forwardTime;
                    mp3Player.seekTo(timeElapsed);
                }
            }
        });
        events.put(R.id.btn_player_pause, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mp3Player.pause();
            }
        });
        events.put(R.id.btn_player_rewind, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ((timeElapsed - forwardTime) >= 0) {
                    timeElapsed = timeElapsed - backwardTime;
                    mp3Player.seekTo(timeElapsed);
                }
            }
        });

        for (int i = 0; i < events.size(); i++) {
            findViewById(events.keyAt(i)).setOnClickListener(events.valueAt(i));
        }
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
        if (txtFile == null) {
            return;
        }

        TextView tv = (TextView) findViewById(R.id.tv_player_content);

        setTitle(txtFile.getTitle());
        try {
            tv.setText(txtFile.read());
        } catch (IOException e) {
            Log.e("读取文件", txtFile.getRealName(), e);
            tv.setText(R.string.lbl_empty);
        }


        tv.setMovementMethod(new ScrollingMovementMethod());

    }

    private Runnable updateSeekerTime = new Runnable() {
        @Override
        public void run() {
            timeElapsed = mp3Player.getCurrentPosition();
            mp3Seeker.setProgress(timeElapsed);
            durationHandler.postDelayed(this, 100);
        }
    };

    private int timeElapsed = 0, finalTime = 0, forwardTime = 2000, backwardTime = 2000;

    private CacheFile txtFile;
    private CacheFile mp3File;
    private MediaPlayer mp3Player;
    private SeekBar mp3Seeker;
    private Handler durationHandler = new Handler();
}
