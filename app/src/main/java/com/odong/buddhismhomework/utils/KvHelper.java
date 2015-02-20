package com.odong.buddhismhomework.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.odong.buddhismhomework.R;

/**
 * Created by flamen on 15-2-20.
 */
public class KvHelper {
    public KvHelper(Context context) {
        settings = context.getSharedPreferences(context.getString(R.string.app_name), 0);
    }

    public void set(String key, Object val) {
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(key, new Gson().toJson(val));
        editor.apply();
    }


    public <T> T get(String key, Class<T> clazz, T def) {
        String val = settings.getString(key, null);
        if (val != null) {
            return new Gson().fromJson(val, clazz);
        }

        if (def != null) {
            set(key, def);
        }
        return def;

    }

    private SharedPreferences settings;

}
