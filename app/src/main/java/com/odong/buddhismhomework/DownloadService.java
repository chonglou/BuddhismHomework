package com.odong.buddhismhomework;

import android.app.IntentService;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import com.odong.buddhismhomework.models.CacheFile;
import com.odong.buddhismhomework.utils.DwDbHelper;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

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
    }

    private void download(String url, boolean redo) throws IOException {
        String name = url.substring(url.lastIndexOf("/") + 1, url.indexOf("?"));
        Log.d("下载", url + " => " + name);
        new CacheFile(this, name).sync(url, redo);
    }

    private void onDropbox(boolean redo) {
        try {
            Document doc = Jsoup.connect(Config.DROPBOX_URL).get();
            Elements links = doc.select("a.filename-link");
            for (Element el : links) {
                download(el.attr("href"), redo);
            }
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
