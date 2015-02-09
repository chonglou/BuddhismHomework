package com.odong.buddhismhomework;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.odong.buddhismhomework.models.CacheFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by flamen on 15-2-8.
 */
public class BackgroundService extends IntentService {
    public BackgroundService() {
        super("BackgroundService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        String action = intent.getStringExtra("action");
        if (action.equals("sync")) {
            onSync();
        }
    }

    private void onSync() {
        List<CacheFile> files = new ArrayList<CacheFile>();
        files.addAll(CacheFile.all(this, "books", R.array.lv_books, "txt"));
        files.addAll(CacheFile.all(this, "courses", R.array.lv_courses, "mp3", "txt"));
        files.addAll(CacheFile.all(this, "musics", R.array.lv_musics, "mp3"));

        int i = 0;
        try {
            for (CacheFile cf : files) {
                cf.sync();
                i++;
            }
        } catch (MalformedURLException e) {
            Log.e("下载", "地址错误", e);
        } catch (IOException e) {
            Log.e("下载", "IO错误", e);
        } catch (SecurityException e) {
            Log.e("下载", "安全错误", e);
        }
        Log.d("同步完成", "" + i + "/" + files.size());

    }
}
