package com.odong.buddhismhomework.back;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import com.odong.buddhismhomework.R;

/**
 * Created by flamen on 15-2-19.
 */
public abstract class NoticeService extends IntentService {

    public NoticeService(String name) {
        super(name);
    }

    protected void notification(Intent intent, String msg) {

        PendingIntent pd = PendingIntent.getActivity(this, 0, intent, 0);
        Notification.Builder nf = new Notification.Builder(this)
                .setContentText(msg)
                .setContentTitle(getString(R.string.app_name))
                .setSmallIcon(R.drawable.ic_launcher)
                .setContentIntent(pd)
                .setAutoCancel(true);


        NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        nm.notify(0, nf.build());

    }

    protected void toast(final String msg) {
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
