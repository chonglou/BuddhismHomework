package com.odong.buddhismhomework.back;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.odong.buddhismhomework.R;
import com.odong.buddhismhomework.utils.WidgetHelper;

/**
 * Created by flamen on 15-2-20.
 */
public class AlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        new WidgetHelper(context).notification(intent, context.getString(R.string.lbl_alarm_homework));
    }
}
