package com.odong.buddhismhomework.services;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.util.Log;

import com.odong.buddhismhomework.R;
import com.odong.buddhismhomework.receivers.ProgressReceiver;
import com.odong.buddhismhomework.utils.DbHelper;
import com.odong.buddhismhomework.utils.KvHelper;
import com.odong.buddhismhomework.utils.WidgetHelper;

import org.json.JSONException;

import java.io.IOException;

/**
 * Created by flamen on 15-8-11.
 */
public class IndexService extends IntentService {

    public IndexService() {
        super("IndexService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        try {
            KvHelper kh = new KvHelper(this);
            String frk = "first.run." + intent.getStringExtra("version");
            if (kh.get().getBoolean(frk, true)) {
                final ResultReceiver receiver = intent.getParcelableExtra("receiver");

                Bundle msg = new Bundle();
                receiver.send(1, msg);

                new WidgetHelper(this).toast(getString(R.string.lbl_wait_for_index), true);
                new DbHelper(this).index(new ProgressReceiver.Callback() {
                    @Override
                    public void run(int progress) {
                        Bundle msg = new Bundle();
                        msg.putInt("progress", progress);
                        receiver.send(0, msg);
                    }
                });
                kh.set(frk, false);
                receiver.send(-1, msg);
            }
            kh.set(frk, false);
        } catch (IOException | JSONException e) {
            Log.e("main", "创建索引出错", e);
            new WidgetHelper(this).toast(getString(R.string.lbl_error_create_index), true);
        }
    }
}
