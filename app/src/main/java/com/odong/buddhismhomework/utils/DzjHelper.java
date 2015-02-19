package com.odong.buddhismhomework.utils;

import android.content.Context;

import com.google.gson.internal.LinkedHashTreeMap;
import com.odong.buddhismhomework.models.CacheFile;

import java.util.Map;

/**
 * Created by flamen on 15-2-18.
 */
public class DzjHelper {
    public DzjHelper(Context context) {
        this.context = context;


    }

    public void load() {
        Map<String, String> map = new LinkedHashTreeMap<String, String>();

    }


    public boolean exist() {
        return new CacheFile(context, NAME).exists();
    }


    private Context context;
    public final static String NAME = "dzj-f";
}
