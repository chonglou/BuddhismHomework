package com.odong.buddhismhomework.utils;

import android.content.Context;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * Created by flamen on 15-2-18.
 */
public class FileHelper {
    public FileHelper(Context context) {
        this.context = context;
    }

    public void unzip(String zip) {
        File path = new File(zip.substring(0, zip.lastIndexOf(".") - 1));
        if (path.exists()) {
            try {
                ZipInputStream zis = new ZipInputStream(new BufferedInputStream(new FileInputStream(zip)));
                ZipEntry ze;
                while ((ze = zis.getNextEntry()) != null) {
                    byte[] buf = new byte[1024];
                    int count;
                    File f = new File(path, ze.getName());
                    if (!f.getParentFile().exists()) {
                        f.getParentFile().mkdirs();
                    }
                    Log.d("正在解压缩文件", f.getAbsolutePath());
                    FileOutputStream fos = new FileOutputStream(f);
                    while ((count = zis.read(buf)) != -1) {
                        fos.write(buf, 0, count);
                    }
                    fos.flush();
                }
            } catch (IOException e) {
                path.delete();
                Log.d("解压缩", zip, e);
            }
        }
    }

    public String readFile(Integer... files) throws IOException {
        StringBuilder sb = new StringBuilder();
        for (int i : files) {
            if (i > 0) {
                BufferedReader br = new BufferedReader(new InputStreamReader(context.getResources().openRawResource(i)));
                String line;
                while ((line = br.readLine()) != null) {
                    sb.append(line);
                    sb.append('\n');
                }
            }

            sb.append("\n\n");
        }
        return sb.toString();
    }

    private Context context;
}
