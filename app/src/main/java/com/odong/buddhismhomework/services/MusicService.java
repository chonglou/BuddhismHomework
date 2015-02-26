package com.odong.buddhismhomework.services;

import android.app.Service;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.os.PowerManager;
import android.util.Log;

import com.odong.buddhismhomework.R;
import com.odong.buddhismhomework.models.CacheFile;
import com.odong.buddhismhomework.utils.KvHelper;
import com.odong.buddhismhomework.utils.WidgetHelper;

import java.io.FileInputStream;
import java.io.IOException;

/**
 * Created by flamen on 15-2-25.
 */
public class MusicService extends Service implements MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener, MediaPlayer.OnCompletionListener {
    public class MusicBinder extends Binder {
        public MusicService getService() {
            return MusicService.this;
        }
    }

    public boolean isPlaying() {
        return player.isPlaying();
    }

    public int getCurrentPosition() {
        return player.getCurrentPosition();
    }

    public void pause() {
        player.pause();
    }

    public void play() {
        player.start();
    }

    public void play(String song) {
        player.reset();

        CacheFile cf = new CacheFile(this, song);
        WidgetHelper wh = new WidgetHelper(this);
        if (cf.exists()) {
            try {

                player.setDataSource(new FileInputStream(cf.getRealFile()).getFD());

            } catch (IOException e) {
                cf.remove();
                wh.toast(getString(R.string.lbl_error_io), true);
                Log.e("Music Service", "Data Source", e);
            }
            player.prepareAsync();
        } else {
            wh.toast(getString(R.string.lbl_error_not_exists), true);
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        player = new MediaPlayer();
        player.setAudioStreamType(AudioManager.STREAM_MUSIC);
        player.setWakeMode(getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);
        player.setOnPreparedListener(this);
        player.setOnErrorListener(this);
        player.setOnCompletionListener(this);
        if (new KvHelper(this).get("mp3.replay", Boolean.class, false)) {
            player.setLooping(true);
        }
    }


    @Override
    public void onPrepared(MediaPlayer mp) {
        duration = player.getDuration();
        player.start();
    }


    @Override
    public IBinder onBind(Intent intent) {
        return musicBinder;
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        return false;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        if (player.isPlaying()) {
            player.stop();
        }
        player.release();
        return false;
    }

    @Override
    public void onCompletion(MediaPlayer mp) {

    }

    private MediaPlayer player;
    private int duration;
    private final IBinder musicBinder = new MusicBinder();

    public int getDuration() {
        return duration;
    }

}
