package com.odong.buddhismhomework;

import android.app.Activity;
import android.app.AlertDialog;
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

import com.google.gson.Gson;
import com.odong.buddhismhomework.models.Book;
import com.odong.buddhismhomework.models.CacheFile;
import com.odong.buddhismhomework.models.Point;
import com.odong.buddhismhomework.utils.DwDbHelper;
import com.odong.buddhismhomework.utils.StringHelper;

import java.io.IOException;

/**
 * Created by flamen on 15-2-8.
 */
public class PlayerActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);


        String type = getIntent().getStringExtra("type");

        getActionBar().setIcon(getResources().getIdentifier("ic_" + type, "drawable", getPackageName()));

        book = new Gson().fromJson(getIntent().getStringExtra("book"), Book.class);
        setTitle(book.getName());


        ((TextView) findViewById(R.id.tv_player_content)).setMovementMethod(new ScrollingMovementMethod());
        initTextView();
        initMp3View();
    }

    @Override
    public void onBackPressed() {
        if (book.getMp3() == null) {
            TextView tv = (TextView) findViewById(R.id.tv_player_content);
            Point p = new Point();
            p.setX(tv.getScrollX());
            p.setY(tv.getScrollY());
            new DwDbHelper(this).set("scroll://" + book.getName(), p);
        }
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
        boolean ok = false;
        if (book.getMp3() != null) {
            try {
                CacheFile cf = new CacheFile(this, book.getMp3());
                if (!cf.exists()) {
                    throw new IOException(getString(R.string.lbl_file_not_exist, book.getMp3()));
                }

                mp3Player = MediaPlayer.create(this,
                        Uri.parse(cf.getRealFile().getAbsolutePath()));

                mp3Player.setAudioStreamType(AudioManager.STREAM_MUSIC);

                mp3Seeker = (SeekBar) findViewById(R.id.sb_player);

                mp3Seeker.setProgress(0);
                mp3Seeker.setMax(mp3Player.getDuration());
                mp3Seeker.setClickable(false);


                findViewById(R.id.btn_player).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ToggleButton tb = (ToggleButton) v;
                        if (tb.isChecked()) {
                            mp3Player.start();
                            findViewById(R.id.tv_player_content).scrollTo(0, 0);
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
                ok = true;
            } catch (IOException e) {
                Log.e("播放", "MP3", e);
            }
        }


        if (!ok) {
            findViewById(R.id.gl_player_mp3).setVisibility(View.GONE);
        }

    }


    private void initTextView() {
        TextView tv = (TextView) findViewById(R.id.tv_player_content);
        if (book.getFiles().isEmpty()) {
            tv.setText(R.string.lbl_empty);
            return;
        }

        try {
            tv.setText(new StringHelper(this).readFile(book.getFiles().toArray(new Integer[1])));
        } catch (IOException e) {
            Log.e("读取文件", book.getName(), e);
            tv.setText(R.string.lbl_error_io);
        }

        Point p = new DwDbHelper(this).get("scroll://" + book.getName(), Point.class);
        if (p == null) {
            p = new Point();
        }
        tv.scrollTo(p.getX(), p.getY());

    }


    private SeekBar mp3Seeker;
    private MediaPlayer mp3Player;
    private Book book;
    private Handler durationHandler = new Handler();
}
