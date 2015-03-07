package com.odong.buddhismhomework.services;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.RemoteViews;

import com.odong.buddhismhomework.Config;
import com.odong.buddhismhomework.R;
import com.odong.buddhismhomework.models.CacheFile;
import com.odong.buddhismhomework.pages.MainActivity;
import com.odong.buddhismhomework.utils.DwDbHelper;
import com.odong.buddhismhomework.utils.KvHelper;
import com.odong.buddhismhomework.utils.WidgetHelper;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Date;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * Created by flamen on 15-3-4.
 */
public class SyncService extends IntentService {
    public SyncService() {
        super("SyncService");
    }

    @Override
    public void onStart(Intent intent, int startId) {
        progress = 0;
        stringBuilder = new StringBuilder();
        dwDbHelper = new DwDbHelper(this);
        widgetHelper = new WidgetHelper(this);
        kvHelper = new KvHelper(this);
        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notification = new Notification();
        notification.icon = R.drawable.ic_launcher;
        updateIntent = new Intent(this, MainActivity.class);
        notification.contentView = new RemoteViews(getPackageName(), R.layout.notice_bar);
        notification.contentView.setProgressBar(R.id.pb_notice_bar, 100, 0, false);
        notification.contentView.setTextViewText(R.id.tv_notice_bar, getString(R.string.lbl_progressing));
        notification.contentIntent = PendingIntent.getActivity(this, 0, updateIntent, 0);

        super.onStart(intent, startId);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        increase(2);
        switch (intent.getStringExtra("type")) {
            case "cbeta.sql":
                downloadAndImport("cbeta");
                break;
            case "videos.sql":
                downloadAndImport("videos");
                break;
            case "ddc.zip":
                downloadAndUnzip("ddc");
                break;
            case "musics.zip":
                downloadAndUnzip("musics");
                break;
            case "dict.zip":
                downloadAndUnzip("dict");
                break;
            case "cbeta.zip":
                downloadAndUnzip("cbeta");
                break;
            default:
                downloadAndImport("cbeta");
                downloadAndImport("videos");
                downloadAndUnzip("dict");
                downloadAndUnzip("ddc");
                downloadAndUnzip("musics");
                downloadAndUnzip("cbeta");
                kvHelper.set("sync://all.zip", new Date());
                break;
        }
        success();
    }

    private void downloadAndImport(String name) {
        String sql = name + ".sql";
        download(fetchUrl(sql), sql);
        increase(5);
        dwDbHelper.loadSql(new CacheFile(this, sql).getRealFile());
        increase(5);
        kvHelper.set("sync://" + sql, new Date());
    }

    private void downloadAndUnzip(String name) {
        String zip = name + ".zip";
        download(fetchUrl(zip), zip);
        increase(5);
        unzip(zip, name);
        increase(5);
        kvHelper.set("sync://" + zip, new Date());
    }

    //------------------------------------------------------------------
    private String fetchUrl(String file) {
        return Config.getUrlMap().get(kvHelper.get("host.type", Integer.class, R.id.btn_setting_home_dropbox)).get(file);
    }

    private void fail(String msg) {
        Message m = new Message();
        m.what = FAIL;
        log(msg);
        handler.sendMessage(m);
    }

    private void success() {
        Message msg = new Message();
        msg.what = SUCCESS;
        handler.sendMessage(msg);
    }

    private void log(String msg) {
        stringBuilder.append(msg);
        stringBuilder.append('\n');
        widgetHelper.toast(msg, true);
    }


    private void increase(int i) {
        progress += i;
        Message msg = new Message();
        msg.what = INCREASE;
        msg.arg1 = progress;
        handler.sendMessage(msg);
    }


    private void unzip(String zip, String dir) {
        log(getString(R.string.lbl_begin_uncompress, zip));
        File zipF = new File(new CacheFile(this, zip).getRealFile().getAbsolutePath());
        File dirF = new File(new CacheFile(this, dir).getRealFile().getAbsolutePath());

        if (!zipF.exists()) {
            log(getString(R.string.lbl_file_not_exist, zip));
            return;
        }
        if (dirF.exists()) {
            log(getString(R.string.lbl_already_exist, dir));
            return;
        }
        try {
            ZipInputStream zis = new ZipInputStream(new BufferedInputStream(new FileInputStream(zipF)));
            ZipEntry ze;
            while ((ze = zis.getNextEntry()) != null) {
                File f = new File(dirF, ze.getName());
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
            log(getString(R.string.lbl_uncompress_complete, zip));
        } catch (Exception e) {
            dirF.delete();
            Log.d("解压缩失败", zip, e);
            fail(getString(R.string.lbl_error_unzip, zip));
        }

    }

    private void download(String url, String name) {

        //String name = URLDecoder.decode(url.substring(url.lastIndexOf("/") + 1, url.indexOf("?")), "UTF-8");
        CacheFile cf = new CacheFile(this, name);

        if (cf.exists()) {
            log(getString(R.string.lbl_already_exist, name));
            return;
        }

        if (url == null) {
            fail(getString(R.string.lbl_no_valid_host));
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
            log(getString(R.string.lbl_download_success, name));
            Log.d("下载完成", name);
        } catch (Exception e) {
            Log.e("下载", name, e);
            cf.remove();
            fail(getString(R.string.lbl_error_download, name));
        }

    }


    private DwDbHelper dwDbHelper;
    private WidgetHelper widgetHelper;
    private KvHelper kvHelper;
    private NotificationManager notificationManager;
    private Notification notification;
    private Intent updateIntent;
    private StringBuilder stringBuilder;
    private int progress;

    private final int SUCCESS = 1;
    private final int FAIL = 2;
    private final int INCREASE = 3;
    private Handler handler = new Handler(new Handler.Callback() {

        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case SUCCESS:
                    notification.defaults = Notification.DEFAULT_ALL;
                    notification.flags |= Notification.FLAG_AUTO_CANCEL;
                    notification.contentIntent = PendingIntent.getActivity(SyncService.this, 0, new Intent(SyncService.this, MainActivity.class), 0);
                    notification.contentView.setTextViewText(R.id.tv_notice_bar, getString(R.string.lbl_success));
                    notification.contentView.setProgressBar(R.id.pb_notice_bar, 100, 100, false);
                    notificationManager.notify(0, notification);
                    kvHelper.set("sync.log", stringBuilder.toString());
                    stopService(updateIntent);
                    break;
                case FAIL:
                    notification.defaults = Notification.DEFAULT_ALL;
                    notification.flags |= Notification.FLAG_AUTO_CANCEL;
                    notification.contentView.setTextViewText(R.id.tv_notice_bar, getString(R.string.lbl_fail));
                    notificationManager.notify(0, notification);
                    kvHelper.set("sync.log", stringBuilder.toString());
                    stopService(updateIntent);
                    break;
                case INCREASE:
                    notification.contentView.setProgressBar(R.id.pb_notice_bar, 100, msg.arg1, false);
                    notificationManager.notify(0, notification);
                    break;
            }
            return true;
        }
    });


}
