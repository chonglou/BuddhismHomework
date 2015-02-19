package com.odong.buddhismhomework;

import android.app.IntentService;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import com.odong.buddhismhomework.models.CacheFile;
import com.odong.buddhismhomework.utils.DictHelper;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * Created by flamen on 15-2-19.
 */
public class ImportService extends IntentService {

    public ImportService() {
        super("ImportService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        unzip(DictHelper.NAME);
        unzip(DZJ_NAME);
    }

    private void unzip(String name) {

        File file = new File(new CacheFile(this, name + ".zip").getRealFile().getAbsolutePath());
        File root = new File(new CacheFile(this, name).getRealFile().getAbsolutePath());

        if (!file.exists()) {
            response(getString(R.string.lbl_file_not_exist, name + ".zip"));
            return;
        }
        if (root.exists()) {
            response(getString(R.string.lbl_already_exist, name));
            return;
        }
        try {
            ZipInputStream zis = new ZipInputStream(new BufferedInputStream(new FileInputStream(file)));
            ZipEntry ze;
            while ((ze = zis.getNextEntry()) != null) {
                File f = new File(file.getParentFile(), ze.getName());
                if (ze.isDirectory()) {
                    f.mkdirs();
                    Log.d("创建目录", f.getAbsolutePath());
                } else {
                    byte[] buf = new byte[1024];
                    int count;
                    Log.d("解压缩文件", f.getAbsolutePath());
                    FileOutputStream fos = new FileOutputStream(f);
                    while ((count = zis.read(buf)) != -1) {
                        fos.write(buf, 0, count);
                    }
                    fos.flush();
                    fos.close();
                }
            }
            Log.d("解压缩完毕", name);
        } catch (IOException e) {
            root.delete();
            Log.d("解压缩", name, e);
            response(getString(R.string.lbl_error_unzip, name));
        }

    }

    private void response(final String msg) {
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
            }
        });
    }

    public final static String DZJ_NAME = "dzj-f";
    public final static String DICT_NAME = "dict";
}
