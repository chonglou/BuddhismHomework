package com.odong.buddhismhomework.utils;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.odong.buddhismhomework.models.Calendar;
import com.odong.buddhismhomework.receivers.AlarmReceiver;

/**
 * Created by flamen on 15-2-20.
 */
public class AlarmHelper {
    public AlarmHelper(Context context) {
        this.context = context;
    }

    public void resetAlarms() {
        AlarmManager alarm = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        KvHelper kv = new KvHelper(context);
        for (String k : new String[]{"homework.evening.cal", "homework.morning.cal"}) {
            Calendar ca = kv.get(k, Calendar.class, new Calendar());
            PendingIntent pi = getIntent(k);
            if (ca.isEnable()) {
                java.util.Calendar cal = java.util.Calendar.getInstance();
                cal.set(java.util.Calendar.HOUR_OF_DAY, ca.getHour());
                cal.set(java.util.Calendar.MINUTE, ca.getMinute());
                alarm.setInexactRepeating(AlarmManager.RTC_WAKEUP,
                        cal.getTimeInMillis(),
                        AlarmManager.INTERVAL_DAY,
                        pi);
            } else {
                alarm.cancel(pi);
            }
        }
    }

    private PendingIntent getIntent(String name) {
        Intent intent = new Intent(context, AlarmReceiver.class);
        intent.putExtra("name", name);
        return PendingIntent.getBroadcast(context, 0, intent, 0);
    }

    private Context context;
}
