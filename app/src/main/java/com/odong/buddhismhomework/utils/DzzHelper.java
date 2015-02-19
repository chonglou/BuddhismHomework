package com.odong.buddhismhomework.utils;

import android.content.Context;

import com.odong.buddhismhomework.models.CacheFile;

/**
 * Created by flamen on 15-2-18.
 */
public class DzzHelper {
    public DzzHelper(Context context) {
        this.context = context;


    }

    public void check() {
        CacheFile cf = new CacheFile(context, name + ".zip");
        if (cf.exists()) {
            new FileHelper(context).unzip(cf.getRealFile().getAbsolutePath());

        }
    }

    public boolean exist() {
        return new CacheFile(context, name).exists();
    }


    private Context context;
    private final String name = "大正藏";
}
