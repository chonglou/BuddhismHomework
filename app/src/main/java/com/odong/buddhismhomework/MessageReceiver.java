package com.odong.buddhismhomework;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by flamen on 15-2-8.
 */
public class MessageReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Toast.makeText(context, intent.getStringExtra("response"), Toast.LENGTH_SHORT).show();
        Log.d("aaa", "测试啊啊啊");
    }
}
