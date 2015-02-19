package com.odong.buddhismhomework.models;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import com.odong.buddhismhomework.R;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;

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

    public void sync(String url, boolean redo) throws IOException {
        if (!redo && exists()) {
            return;
        }

        url += name;
        Log.d("下载", url + " => " + name);
        DataInputStream dis = new DataInputStream(new URL(url).openStream());

        byte[] buf = new byte[1024];
        int len;

        FileOutputStream fos = new FileOutputStream(getRealFile());
        while ((len = dis.read(buf)) > 0) {
            fos.write(buf, 0, len);
        }
        fos.flush();

    }

    public void remove() throws IOException {
        getRealFile().deleteOnExit();
        Log.d("删除文件", getRealFile().getAbsolutePath());
    }

    public boolean exists() throws IOException {
        return getRealFile().exists();
    }


    public File getRealFile() throws IOException {
        String state = Environment.getExternalStorageState();
        if (!Environment.MEDIA_MOUNTED.equals(state)) {
            throw new IOException(context.getString(R.string.lbl_sd_card_not_exist)+state);
        }
        File dir = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + context.getString(R.string.app_name));
        if (!dir.exists()) {
            dir.mkdirs();
        }
        return new File(dir, name);
    }

    private String name;
    private Context context;


}
