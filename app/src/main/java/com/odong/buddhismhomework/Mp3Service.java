package com.odong.buddhismhomework;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.PowerManager;

/**
 * Created by flamen on 15-2-8.
 */
public class Mp3Service extends IntentService implements MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener {
    MediaPlayer player = null;

    public Mp3Service() {
        super("Mp3Service");
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        player = MediaPlayer.create(this, Uri.parse(getFileStreamPath(intent.getStringExtra("name")).getAbsolutePath()));
        player.setAudioStreamType(AudioManager.STREAM_MUSIC);
        player.setOnPreparedListener(this);
        player.setOnErrorListener(this);
        player.setWakeMode(getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);
        player.prepareAsync();
//        WifiManager.WifiLock wifiLock = ((WifiManager) getSystemService(Context.WIFI_SERVICE))
//                .createWifiLock(WifiManager.WIFI_MODE_FULL, "mylock");
//
//        wifiLock.acquire();
//        wifiLock.release();
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        player.start();
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        //todo
        return false;
    }
}
