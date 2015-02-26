package com.odong.buddhismhomework.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.odong.buddhismhomework.utils.AlarmHelper;

/**
 * Created by flamen on 15-2-20.
 */
public class BootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
            new AlarmHelper(context).resetAlarms();
        }
    }
}
