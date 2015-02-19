package com.odong.buddhismhomework.utils;

import android.content.Context;

import com.odong.buddhismhomework.models.CacheFile;

/**
 * Created by flamen on 15-2-18.
 */
public class DictHelper {
    public DictHelper(Context context) {
        this.context = context;
    }

    public boolean exist() {
        return new CacheFile(context, NAME).exists();
    }


    private Context context;
    public final static String NAME = "dict";
}
