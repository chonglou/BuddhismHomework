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
        for (String ext : new String[]{"dict", "idx", "ifo"}) {
            files.add("foguangdacidian" + ext);
        }
        int i = 0;
        for (String f : files) {
            CacheFile cf = new CacheFile(this, f);
            try {
                cf.sync(redo);
            } catch (Exception e) {
                Log.e("下载", "地址错误", e);
                cf.remove();
            }
            i++;
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
