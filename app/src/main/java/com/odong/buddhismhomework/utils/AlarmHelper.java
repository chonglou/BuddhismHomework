package com.odong.buddhismhomework.utils;

import android.content.Context;

/**
 * Created by flamen on 15-2-20.
 */
public class AlarmHelper {
    public AlarmHelper(Context context) {
        this.context = context;
    }

    public void resetAlarms() {
        KvHelper kv = new KvHelper(context);
        //Calendar cm = kv.get("")
    }

    private Context context;
}
