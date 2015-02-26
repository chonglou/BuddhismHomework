package com.odong.buddhismhomework.pages.audio;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.google.gson.Gson;
import com.odong.buddhismhomework.R;
import com.odong.buddhismhomework.models.Book;
import com.odong.buddhismhomework.models.CacheFile;
import com.odong.buddhismhomework.models.Pager;
import com.odong.buddhismhomework.utils.KvHelper;
import com.odong.buddhismhomework.utils.WidgetHelper;

import java.io.FileInputStream;
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
            Pager p = new Pager();
            p.setX(tv.getScrollX());
            p.setY(tv.getScrollY());

            new KvHelper(this).set("scroll://book/" + book.getName(), p);

        }
        if (mp3Player.isPlaying()) {
            AlertDialog.Builder adb = new AlertDialog.Builder(this);
            adb.setMessage(R.string.dlg_will_pause);
            adb.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    mp3Player.stop();
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

            final CacheFile cf = new CacheFile(this, book.getMp3());
            if (cf.exists()) {

                try {

                    mp3Player = new MediaPlayer();
                    mp3Player.setDataSource(new FileInputStream(cf.getRealFile()).getFD());
                    mp3Player.setAudioStreamType(AudioManager.STREAM_MUSIC);
                    mp3Player.prepare();


                    boolean loop = new KvHelper(this).get("mp3.replay", Boolean.class, false);

                    if (loop) {
                        mp3Player.setLooping(true);
                    } else {
                        mp3Player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                            @Override
                            public void onCompletion(MediaPlayer mp) {
                                ((ToggleButton) findViewById(R.id.btn_player)).setChecked(false);
                            }
                        });
                    }


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
                    Log.e("MP3", "播放", e);
                    AlertDialog.Builder adb = new AlertDialog.Builder(this);
                    adb.setMessage(getString(R.string.lbl_file_broken, cf.getName()));
                    adb.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            cf.remove();
                            PlayerActivity.this.finish();
                        }
                    });
                    adb.setNegativeButton(android.R.string.no, null);
                    adb.setCancelable(false);
                    adb.create().show();
                }
            } else {
                new WidgetHelper(PlayerActivity.this).toast(getString(R.string.lbl_file_not_exist, book.getMp3()), false);

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
            tv.setText(new WidgetHelper(this).readFile(book.getFiles().toArray(new Integer[1])));

        } catch (IOException e) {
            Log.e("读取文件", book.getName(), e);
            tv.setText(R.string.lbl_error_io);
        }


        Pager p = new KvHelper(this).get("scroll://book/" + book.getName(), Pager.class, new Pager());
        tv.scrollTo(p.getX(), p.getY());
    }


    private SeekBar mp3Seeker;
    private MediaPlayer mp3Player;
    private Book book;
    private Handler durationHandler = new Handler();
}
