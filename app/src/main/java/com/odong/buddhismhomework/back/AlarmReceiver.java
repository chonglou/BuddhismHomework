package com.odong.buddhismhomework.back;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.odong.buddhismhomework.R;

/**
 * Created by flamen on 15-2-20.
 */
public class AlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        PendingIntent pd = PendingIntent.getActivity(context, 0, intent, 0);
        Notification.Builder nf = new Notification.Builder(context)
                .setContentText("闹钟")
                .setContentTitle(context.getString(R.string.app_name))
                .setSmallIcon(R.drawable.ic_launcher)
                .setContentIntent(pd)
                .setAutoCancel(true);

        NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        nm.notify(0, nf.build());
    }
}
