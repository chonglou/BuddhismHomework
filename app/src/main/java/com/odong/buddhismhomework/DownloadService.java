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
            String type = intent.getStringExtra("type");


            if (type == null) {
                type = "dropbox";
            }
            String name = new XmlHelper(this).getHostMap().get(type);

            if (type.equals("dropbox")) {
                onDropbox(redo);
            } else {
                response(getString(R.string.lbl_refresh_no_valid_host, name));
            }
        }

    }

    private void onDropbox(boolean redo) {

    }

//    private void onSync(boolean redo) {
//        List<String> files = new XmlHelper(this).getDownloadFileList();
//        for (String ext : new String[]{"dict", "idx", "ifo"}) {
//            files.add("foguangdacidian." + ext);
//        }
//        int success = 0;
//        int size = files.size();
//
//        for (int i = 1; i <= size; i++) {
//            String f = files.get(i - 1);
//            CacheFile cf = new CacheFile(this, f);
//            try {
//                cf.sync(redo);
//                response(getString(R.string.lbl_refresh_success, i, size, f));
//                success++;
//            } catch (Exception e) {
//                Log.e("下载", "地址错误", e);
//                response(getString(R.string.lbl_refresh_fail, i, size, f));
//                cf.remove();
//            }
//        }
//
//        String msg = getString(R.string.lbl_refresh_result, success, files.size());
//
//        new DwDbHelper(this).set("sync.last", new Date());
//        Log.d("后台", msg);
//        response(msg);
//
//    }

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
