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
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
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
        files = new HashMap<>();
        dwDbHelper = new DwDbHelper(this);
        widgetHelper = new WidgetHelper(this);
        kvHelper = new KvHelper(this);
        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notification = new Notification();
        notification.icon = R.drawable.ic_launcher;
        updateIntent = new Intent(this, MainActivity.class);
        notification.contentView = new RemoteViews(getPackageName(), R.layout.notice_bar);
        notification.contentView.setProgressBar(R.id.pb_notice_bar, 100, 0, false);
        notification.contentView.setTextViewText(R.id.tv_notice_bar, getString(R.string.lbl_progressing, 0));
        notification.contentIntent = PendingIntent.getActivity(this, 0, updateIntent, 0);

        super.onStart(intent, startId);
    }

    @Override
    protected synchronized void onHandleIntent(Intent intent) {
        increase(2);
        String type = intent.getStringExtra("type");
        dwDbHelper.addLog(SPACING + getString(R.string.lbl_begin_sync, type) + SPACING);


        try {

            initFiles();

            switch (type) {
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
            finish(true, getString(R.string.lbl_sync_complete, type));
        } catch (Exception e) {
            Log.d("服务", "出错", e);
            log(e.getMessage());
            finish(false, getString(R.string.lbl_error_sync, type));
        }


    }

    private void downloadAndImport(String name) throws Exception {
        String sql = name + ".sql";
        download(sql, null);
        increase(5);
        dwDbHelper.loadSql(new CacheFile(this, sql).getRealFile());
        increase(5);
        kvHelper.set("sync://" + sql, new Date());
    }

    private void downloadAndUnzip(String name) throws Exception {
        String zip = name + ".zip";
        download(zip, name);
        increase(5);
        unzip(zip, name);
        increase(5);
        kvHelper.set("sync://" + zip, new Date());
    }


    private void initFiles() throws Exception {
        String url;
        String type;
        switch (kvHelper.get().getInt("host.type", R.id.btn_setting_home_dropbox)) {
            case R.id.btn_setting_home_dropbox:
                type = "dropbox";
                url = Config.DROPBOX_FILE_LST;
                break;
            default:
                throw new IOException(getString(R.string.lbl_no_valid_host));
        }

//        String json = HttpClient.get(url);
//        Log.d("文件索引", json);

//        JsonArray ja = new JsonParser().parse(json).getAsJsonArray();
//        for (JsonElement je : ja) {
//            Map<String, String> map = new HashMap<>();
//            JsonObject jo = je.getAsJsonObject();
//            map.put("url", jo.get(type).getAsString());
//            map.put("md5", jo.get("md5").getAsString());
//            files.put(jo.get("name").getAsString(), map);
//        }
        Log.d("文件列表", files.toString());
    }

    private void finish(boolean ok, String msg) {
        dwDbHelper.addLog(SPACING + msg + SPACING);
        Message m = new Message();
        m.what = ok ? SUCCESS : FAIL;
        handler.sendMessage(m);
    }

    private void log(String msg) {
        dwDbHelper.addLog(msg);
        widgetHelper.toast(msg, true);
    }


    private void increase(int i) {
        progress += i;
        Message msg = new Message();
        msg.what = INCREASE;
        msg.arg1 = progress;
        handler.sendMessage(msg);
    }


    private void unzip(String zip, String dir) throws Exception {
        log(getString(R.string.lbl_begin_uncompress, zip));
        File zipF = new File(new CacheFile(this, zip).getRealFile().getAbsolutePath());
        CacheFile dirCF = new CacheFile(this, dir);
        File dirF = new File(dirCF.getRealFile().getAbsolutePath());

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
                    if (!f.getParentFile().exists()) {
                        f.getParentFile().mkdirs();
                    }
                    FileOutputStream fos = new FileOutputStream(f);
                    while ((count = zis.read(buf)) != -1) {
                        fos.write(buf, 0, count);
                    }
                    fos.flush();
                    fos.close();
                }
            }
            log(getString(R.string.lbl_uncompress_complete, zip));
        } catch (IOException e) {
            dirCF.delete();
            Log.d("解压缩失败", zip, e);
            throw new Exception(getString(R.string.lbl_error_unzip, zip));
        }

    }

    private String md5(File file) throws IOException, NoSuchAlgorithmException {
        FileInputStream fis = new FileInputStream(file);
        byte[] buf = new byte[1024];
        MessageDigest md = MessageDigest.getInstance("MD5");
        int read = 0;
        while ((read = fis.read(buf)) != -1) {
            md.update(buf, 0, read);
        }
        fis.close();
        byte[] mdb = md.digest();
        String val = "";
        for (byte b : mdb) {
            val += Integer.toString((b & 0xff) + 0x100, 16).substring(1);
        }
        return val;
    }

    private void download(String name, String dir) throws Exception {
        Map<String, String> map = files.get(name);
        String url = map.get("url");
        String md5 = map.get("md5");

        CacheFile cf = new CacheFile(this, name);

        if (cf.exists()) {

            String newMd5 = md5(cf.getRealFile());
            if (md5.equals(newMd5)) {
                log(getString(R.string.lbl_already_exist, name));
                return;
            } else {
                Log.d("MD5不匹配", name + ": " + md5 + " VS " + newMd5);
                log(getString(R.string.lbl_error_md5, name));
                cf.delete();
                if (dir != null) {
                    new CacheFile(this, dir).delete();
                }
            }
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
        } catch (IOException e) {
            Log.e("下载", name, e);
            cf.remove();
            throw new Exception(getString(R.string.lbl_error_download, name));
        }

    }


    private DwDbHelper dwDbHelper;
    private WidgetHelper widgetHelper;
    private KvHelper kvHelper;
    private NotificationManager notificationManager;
    private Notification notification;
    private Intent updateIntent;
    private Map<String, Map<String, String>> files;
    private int progress;

    private final String SPACING = "------";
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
                    stopService(updateIntent);
                    break;
                case FAIL:
                    notification.defaults = Notification.DEFAULT_ALL;
                    notification.flags |= Notification.FLAG_AUTO_CANCEL;
                    notification.contentView.setTextViewText(R.id.tv_notice_bar, getString(R.string.lbl_fail));
                    notificationManager.notify(0, notification);
                    stopService(updateIntent);
                    break;
                case INCREASE:
                    notification.contentView.setTextViewText(R.id.tv_notice_bar, getString(R.string.lbl_progressing, msg.arg1));
                    notification.contentView.setProgressBar(R.id.pb_notice_bar, 100, msg.arg1, false);
                    notificationManager.notify(0, notification);
                    break;
            }
            return true;
        }
    });


}
