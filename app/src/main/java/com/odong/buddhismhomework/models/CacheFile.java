package com.odong.buddhismhomework.models;

import android.content.Context;
import android.util.Log;

import com.odong.buddhismhomework.BuildConfig;
import com.odong.buddhismhomework.Config;
import com.odong.buddhismhomework.utils.DwDbHelper;
import com.odong.buddhismhomework.utils.XmlHelper;

import java.io.DataInputStream;
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

    public void sync(boolean redo) throws IOException {
        if (!redo && exists()) {
            return;
        }

        String url;
        if (BuildConfig.DEBUG) {
            url = "http://" + Config.DEVELOPMENT_HOST + "/tools/downloads/";
        } else {
            DwDbHelper ddh = new DwDbHelper(this.context);
            url = ddh.get("download.node", String.class);
            if (url == null) {
                url = new XmlHelper(this.context).getHostList().get(0).getUrl();
                ddh.set("download.node", url);
            }
        }
        url += name;
        Log.d("下载", url + " => " + getRealName());
        DataInputStream dis = new DataInputStream(new URL(url).openStream());

        byte[] buf = new byte[1024];
        int len;

        FileOutputStream fos = context.openFileOutput(getRealName(), Context.MODE_PRIVATE);
        while ((len = dis.read(buf)) > 0) {
            fos.write(buf, 0, len);
        }
        fos.flush();

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
