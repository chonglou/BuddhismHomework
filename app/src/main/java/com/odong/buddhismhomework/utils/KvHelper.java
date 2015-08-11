package com.odong.buddhismhomework.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.odong.buddhismhomework.R;
import com.odong.buddhismhomework.models.Calendar;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Date;

/**
 * Created by flamen on 15-2-20.
 */
public class KvHelper {
    public KvHelper(Context context) {
        settings = context.getSharedPreferences(context.getString(R.string.app_name), 0);
    }

    public void set(String key, boolean val) {

        SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean(key, val);
        editor.apply();
    }

    public void set(String key, Calendar val) {
        setObject(key, val);
    }

    public Object getObject(String key, Object def) {
        if (settings.contains(key)) {
            String val = settings.getString(key, null);
            if (val != null) {
                try {

                    ByteArrayInputStream bais = new ByteArrayInputStream(val.getBytes());
                    ObjectInputStream ois = new ObjectInputStream(bais);
                    return ois.readObject();
                } catch (IOException | ClassNotFoundException e) {
                    Log.e("kv", "读取对象", e);
                }

            }
        }
        return def;
    }

    public void setObject(String key, Object val) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(val);
            oos.flush();

            SharedPreferences.Editor editor = settings.edit();
            editor.putString(key, baos.toString());
            editor.apply();

        } catch (IOException e) {
            Log.e("kv", "保存对象出错", e);
        }
    }

    public void set(String key, float val) {

        SharedPreferences.Editor editor = settings.edit();
        editor.putFloat(key, val);
        editor.apply();
    }

    public void set(String key, int val) {
        SharedPreferences.Editor editor = settings.edit();
        editor.putInt(key, val);
        editor.apply();
    }

    public void set(String key, long val) {
        SharedPreferences.Editor editor = settings.edit();
        editor.putLong(key, val);
        editor.apply();
    }

    public void set(String key, Date val) {
        set(key, val.getTime());
    }

    public Date getDate(String key, Date def) {
        long val = settings.getLong(key, -1);
        if (val == -1) {
            return def;
        }
        return new Date(val);
    }

    public SharedPreferences get() {
        return settings;
    }

//    public void set(String key, Object val) {
//        SharedPreferences.Editor editor = settings.edit();
//
//        editor.putString(key, new Gson().toJson(val));
//        editor.apply();
//    }


//    public <T> T get(String key, Class<T> clazz, T def) {
//        String val = settings.getString(key, null);
//        if (val != null) {
//            return new Gson().fromJson(val, clazz);
//        }
//
//        if (def != null) {
//            set(key, def);
//        }
//        return def;
//
//    }

    private SharedPreferences settings;

}
