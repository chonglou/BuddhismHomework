package com.odong.buddhismhomework.models;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import com.odong.buddhismhomework.R;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by flamen on 15-2-8.
 */
public class CacheFile {


    public CacheFile(Context context, String name) {
        this.name = name;
        this.context = context;
    }


    public String read() {
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(getRealFile())));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line);
                sb.append('\n');
            }
            return sb.toString();
        } catch (IOException e) {
            Log.e("读取文件", name, e);
        }
        return null;
    }


    public void remove() {
        File f = getRealFile();
        if (f != null) {
            f.delete();
            Log.d("删除文件", f.getAbsolutePath());
        }

    }

    public boolean exists() {
        File f = getRealFile();
        return f != null && f.exists();
    }

    public String getRootUrl() {
        return Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + context.getString(R.string.app_name);
    }

    public File getRealFile() {
        String state = Environment.getExternalStorageState();
        if (!Environment.MEDIA_MOUNTED.equals(state)) {
            Log.d("SD卡", "状态[" + state + "]");
            return null;
        }
        File dir = new File(getRootUrl());
        if (!dir.exists()) {
            dir.mkdirs();
        }
        return new File(dir, name);
    }

    private String name;
    private Context context;

    public String getName() {
        return name;
    }
}
