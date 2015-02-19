package com.odong.buddhismhomework.models;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import com.odong.buddhismhomework.R;

import java.io.File;
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
        FileInputStream fis = new FileInputStream(getRealFile());
        Log.d("读取缓存", getRealFile().getAbsolutePath());
        StringBuilder sb = new StringBuilder();
        byte[] buf = new byte[1024];
        int n;
        while ((n = fis.read(buf)) > 0) {
            sb.append(new String(buf, 0, n));
        }
        return sb.toString();
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


    public File getRealFile() {
        String state = Environment.getExternalStorageState();
        if (!Environment.MEDIA_MOUNTED.equals(state)) {
            Log.d("SD卡", "状态[" + state + "]");
            return null;
        }
        File dir = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + context.getString(R.string.app_name));
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
