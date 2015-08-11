package com.odong.buddhismhomework.receivers;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;

import com.odong.buddhismhomework.R;
import com.odong.buddhismhomework.utils.WidgetHelper;

/**
 * Created by flamen on 15-8-11.
 */
public class ProgressReceiver extends ResultReceiver {
    public ProgressReceiver(Context context) {
        super(new Handler());
        this.context = context;
    }

    @Override
    protected void onReceiveResult(int resultCode, Bundle resultData) {
        switch (resultCode) {
            case 1:
                new WidgetHelper(context).setRotation(false);
                dlg = new ProgressDialog(context);
                dlg.setTitle(R.string.dlg_title_create_index);
                dlg.setMessage(context.getString(R.string.dlg_create_index, 0));
                break;
            case 0:
                int progress = resultData.getInt("progress");
                dlg.setProgress(progress);
                dlg.setMessage(context.getString(R.string.dlg_create_index, progress));
                dlg.show();
                break;
            case -1:
                dlg.dismiss();
                new WidgetHelper(context).setRotation(true);
                break;


        }
    }
    private Context context;
    private ProgressDialog dlg;


    public interface Callback {
        void run(int progress);
    }
}
