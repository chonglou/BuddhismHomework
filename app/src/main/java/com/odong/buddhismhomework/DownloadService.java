package com.odong.buddhismhomework;

import android.app.IntentService;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import com.odong.buddhismhomework.models.CacheFile;
import com.odong.buddhismhomework.utils.DwDbHelper;
import com.odong.buddhismhomework.utils.XmlHelper;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Date;
import java.util.List;

/**
 * Created by flamen on 15-2-8.
 */
public class DownloadService extends IntentService {

    public DownloadService() {
        super("DownloadService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        String action = intent.getStringExtra("action");


        if (action.equals("sync")) {
            boolean redo = intent.getBooleanExtra("redo", false);
            onSync(redo);
        }

    }


    private void onSync(boolean redo) {
        List<String> files = new XmlHelper(this).getDownloadFileList();
        files.add("dicts.tar.bz2");
        int i = 0;
        try {
            for (String f : files) {
                new CacheFile(this, f).sync(redo);
                i++;
            }
        } catch (MalformedURLException e) {
            Log.e("下载", "地址错误", e);
        } catch (IOException e) {
            Log.e("下载", "IO错误", e);
        } catch (SecurityException e) {
            Log.e("下载", "安全错误", e);
        }

        String msg = getString(R.string.lbl_refresh_result, i, files.size());
        new DwDbHelper(this).set("sync.last", new Date());
        Log.d("后台", msg);
        response(msg);

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
