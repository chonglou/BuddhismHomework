package com.odong.buddhismhomework.models;

import android.content.Context;
import android.util.Log;

import java.io.FileInputStream;
import java.io.IOException;

/**
 * Created by flamen on 15-2-8.
 */
public class CacheFile {


    public CacheFile(Context context, String name) {
        this.name = name;
        this.context = context;
    }


    public String read() throws IOException {
        FileInputStream fis = context.openFileInput(getRealName());
        Log.d("读取缓存", getRealName());
        StringBuilder sb = new StringBuilder();
        byte[] buf = new byte[1024];
        int n;
        while ((n = fis.read(buf)) > 0) {
            sb.append(new String(buf, 0, n));
        }
        return sb.toString();
    }

    public void remove() {
        context.deleteFile(getRealName());
        Log.d("删除文件", getRealName());
    }


    public String getRealName() {
        return "cache-" + name;
    }

    public boolean exists() {
        return context.getFileStreamPath(getRealName()).exists();
    }

    private String name;
    private Context context;


}
