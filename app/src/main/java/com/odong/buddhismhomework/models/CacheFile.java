package com.odong.buddhismhomework.models;

import android.content.Context;
import android.util.Log;

import com.odong.buddhismhomework.BuildConfig;

import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by flamen on 15-2-8.
 */
public class CacheFile {
    public interface ItemCallback {
        void call(String name, String title, String details);
    }

    public static void each(Context context, int id, ItemCallback callback) {
        for (String item : context.getResources().getStringArray(id)) {
            String[] ss = item.split("\\|");
            callback.call(ss[0], ss[1], ss[2]);
        }
    }

    public static List<CacheFile> all(Context context, String type, int id, String... exts) {
        List<CacheFile> files = new ArrayList<CacheFile>();
        for (String item : context.getResources().getStringArray(id)) {
            for (String ext : exts) {
                files.add(new CacheFile(context, type, item, ext));
            }
        }
        return files;
    }

    public CacheFile(Context context, String type, String item, String ext) {
        String[] ss = item.split("\\|");
        this.name = ss[0];
        this.title = ss[1];
        this.details = ss[2];
        this.type = type;
        this.ext = ext;
        this.context = context;
    }

    public CacheFile(Context context, String type, int saId, int index, String ext) {
        this(context, type, context.getResources().getStringArray(saId)[index], ext);
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

    public void sync() throws IOException {
        if (!context.getFileStreamPath(getRealName()).exists()) {
            URL url = new URL(
                    (BuildConfig.DEBUG ?
                            "http://192.168.1.102/tools" :
                            "https://raw.githubusercontent.com/chonglou/BuddhismHomework/master/tools")

                            + getHttpName());
            Log.d("同步缓存", url.toString() + " " + getRealName());
            DataInputStream dis = new DataInputStream(url.openStream());

            byte[] buf = new byte[1024];
            int len;

            FileOutputStream fos = context.openFileOutput(getRealName(), Context.MODE_PRIVATE);
            while ((len = dis.read(buf)) > 0) {
                fos.write(buf, 0, len);
            }
            fos.flush();
        }
    }

    public String getRealName() {
        return "cache-" + type + "-" + name + "." + ext;
    }

    public String getHttpName() {
        return "/" + type + "/" + name + "." + ext;
    }

    private String name;
    private String type;
    private String title;
    private String details;
    private String ext;
    private Context context;

    public String getTitle() {
        return title;
    }

    public String getDetails() {
        return details;
    }
}
