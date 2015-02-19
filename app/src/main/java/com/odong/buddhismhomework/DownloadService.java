package com.odong.buddhismhomework;

import android.app.IntentService;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import com.odong.buddhismhomework.models.CacheFile;
import com.odong.buddhismhomework.utils.DictHelper;
import com.odong.buddhismhomework.utils.DwDbHelper;
import com.odong.buddhismhomework.utils.DzjHelper;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.Date;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * Created by flamen on 15-2-8.
 */
public class DownloadService extends IntentService {

    public DownloadService() {
        super("DownloadService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        boolean redo = intent.getBooleanExtra("redo", false);
        Integer type = new DwDbHelper(this).get("host.type", Integer.class);
        if (type == null) {
            type = R.id.btn_setting_home_dropbox;
        }
        switch (type) {
            case R.id.btn_setting_home_dropbox:
                onDropbox(redo);
                break;
            case R.id.btn_setting_home_baiduyun:
                onBaiduyun(redo);
                break;
            default:
                response(getString(R.string.lbl_unknown_host));
        }

        unzip(DictHelper.NAME, redo);
        unzip(DzjHelper.NAME, redo);
        new DwDbHelper(this).set("sync.last", new Date());
    }

    private void download(String url, boolean redo) throws IOException {
        String name = URLDecoder.decode(url.substring(url.lastIndexOf("/") + 1, url.indexOf("?")), "UTF-8");
        CacheFile cf = new CacheFile(this, name);

        if (!redo && cf.exists()) {
            response(getString(R.string.lbl_already_download, name));
            return;
        }

        try {
            Log.d("下载", url + " => " + name);
            DataInputStream dis = new DataInputStream(new URL(url).openStream());

            byte[] buf = new byte[1024];
            int len;

            FileOutputStream fos = new FileOutputStream(cf.getRealFile());
            while ((len = dis.read(buf)) != -1) {
                fos.write(buf, 0, len);
            }
            fos.flush();
            response(getString(R.string.lbl_download_success, name));
            Log.e("下载完成", name);
        } catch (IOException e) {
            Log.e("下载", name, e);
            cf.remove();
        }

    }

    public void unzip(String name, boolean redo) {

        File file = new File(new CacheFile(this, name + ".zip").getRealFile().getAbsolutePath());
        File root = new File(new CacheFile(this, name).getRealFile().getAbsolutePath());

        if (!file.exists() || (!redo && root.exists())) {
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


    private void onDropbox(boolean redo) {
        try {
            Document doc = Jsoup.connect(Config.DROPBOX_URL).get();
            Elements links = doc.select("a.filename-link");
            for (Element el : links) {
                download(Jsoup.connect(el.attr("href")).get().select("a#default_content_download_button").get(0).attr("href"), redo);
            }
            response(getString(R.string.lbl_download_complete, links.size()));
        } catch (IOException e) {
            Log.e("下载", "IO", e);
            response(getString(R.string.lbl_download_error, e.getMessage()));
        }

    }

    private void onBaiduyun(boolean redo) {
        response(getString(R.string.lbl_no_valid_host));
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


}
