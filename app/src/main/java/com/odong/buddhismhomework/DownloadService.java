package com.odong.buddhismhomework;

import android.app.IntentService;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import com.odong.buddhismhomework.utils.DwDbHelper;

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

    private void onDropbox(boolean redo) {

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
