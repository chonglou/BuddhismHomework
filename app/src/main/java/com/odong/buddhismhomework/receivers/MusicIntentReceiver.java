package com.odong.buddhismhomework.receivers;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.odong.buddhismhomework.utils.KvHelper;

/**
 * Created by flamen on 15-2-25.
 */
public class MusicIntentReceiver extends android.content.BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(
                android.media.AudioManager.ACTION_AUDIO_BECOMING_NOISY)) {
            if (new KvHelper(context).get("mp3.earphone", Boolean.class, false)) {

            }
            Log.d("MP3", "播放暂停");
        }
    }
}
