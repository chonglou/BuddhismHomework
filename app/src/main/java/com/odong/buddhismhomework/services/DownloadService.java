package com.odong.buddhismhomework.services;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.odong.buddhismhomework.Config;
import com.odong.buddhismhomework.R;
import com.odong.buddhismhomework.models.CacheFile;
import com.odong.buddhismhomework.utils.KvHelper;
import com.odong.buddhismhomework.utils.WidgetHelper;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.DataInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.Date;

/**
 * Created by flamen on 15-2-8.
 */
public class DownloadService extends IntentService {

    public DownloadService() {
        super("DownloadService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        WidgetHelper wh = new WidgetHelper(this);
        wh.toast(getString(R.string.lbl_begin_download), true);
        KvHelper kh = new KvHelper(this);
        Integer type = kh.get("host.type", Integer.class, R.id.btn_setting_home_dropbox);

        switch (type) {
            case R.id.btn_setting_home_dropbox:
                onDropbox(intent);
                break;
            case R.id.btn_setting_home_baiduyun:
                onBaiduyun();
                break;
            default:
                wh.toast(getString(R.string.lbl_unknown_host), true);
        }

        kh.set("sync.last", new Date());

    }

    private void download(String url) throws IOException {
        String name = URLDecoder.decode(url.substring(url.lastIndexOf("/") + 1, url.indexOf("?")), "UTF-8");
        CacheFile cf = new CacheFile(this, name);
        WidgetHelper wh = new WidgetHelper(this);
        if (cf.exists()) {
            wh.toast(getString(R.string.lbl_already_exist, name), true);
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
            wh.toast(getString(R.string.lbl_download_success, name), true);
            Log.d("下载完成", name);
        } catch (IOException | StringIndexOutOfBoundsException e) {
            Log.e("下载", name, e);
            cf.remove();
        }

    }


    private void onDropbox(Intent intent) {
        WidgetHelper wh = new WidgetHelper(this);
        try {
            Document doc = Jsoup.connect(Config.DROPBOX_URL).get();
            Elements links = doc.select("a.filename-link");
            for (Element el : links) {
                download(el.attr("href").replace("dl=0", "dl=1"));
            }
            wh.notification(intent, getString(R.string.lbl_download_complete, links.size()));
        } catch (IOException e) {
            Log.e("下载", "IO", e);
            wh.notification(intent, getString(R.string.lbl_download_error, e.getMessage()));
        }

    }

    private void onBaiduyun() {
        WidgetHelper wh = new WidgetHelper(this);
        wh.toast(getString(R.string.lbl_no_valid_host), true);
    }


}
