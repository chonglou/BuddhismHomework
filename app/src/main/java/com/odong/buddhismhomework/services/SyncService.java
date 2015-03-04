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
import com.odong.buddhismhomework.utils.KvHelper;
import com.odong.buddhismhomework.utils.WidgetHelper;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.net.URLDecoder;
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
        sb = new StringBuilder();
        wh = new WidgetHelper(this);
        kh = new KvHelper(this);
        nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
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
        download();
        uncompress();
        cbeta();
        youtube();
        success();
    }

    private void download() {
        int i = 1;
        increase(i);

        log(getString(R.string.lbl_begin_download));
        Integer type = kh.get("host.type", Integer.class, R.id.btn_setting_home_dropbox);

        switch (type) {
            case R.id.btn_setting_home_dropbox:
                onDropbox(i);
                break;
            case R.id.btn_setting_home_baiduyun:
                onBaiduyun();
                break;
            default:
                log(getString(R.string.lbl_unknown_host));
        }

    }

    private void uncompress() {
        int i = 30;
        increase(i);

        unzip(DICT_NAME+".zip", DICT_NAME);
        i += 10;
        increase(i);
        unzip(DZJ_NAME+".zip", DZJ_NAME);

    }

    private void cbeta() {
        int i = 70;
        increase(i);
        //log(getString(R.string.lbl_import_complete, books.size()))
    }

    private void youtube() {
        int i = 90;
        increase(i);
    }

    private void increase(int i) {
        Message msg = new Message();
        msg.what = INCREASE;
        msg.arg1 = i;
        handler.sendMessage(msg);
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
        sb.append(msg);
        sb.append('\n');
        wh.toast(msg, true);
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
        } catch (IOException e) {
            dirF.delete();
            Log.d("解压缩失败", zip, e);
            fail(getString(R.string.lbl_error_unzip, zip));
        }

    }


    private void onDropbox(int i) {
        try {
            Document doc = Jsoup.connect(Config.DROPBOX_URL).get();
            Elements links = doc.select("a.filename-link");
            for (Element el : links) {
                downloadFile(el.attr("href").replace("dl=0", "dl=1"));
                i += 2;
                increase(i);
            }
            log(getString(R.string.lbl_download_complete, links.size()));
        } catch (IOException e) {
            Log.e("下载", "IO", e);
            fail(getString(R.string.lbl_download_error, e.getMessage()));
        }

    }

    private void onBaiduyun() {
        fail(getString(R.string.lbl_no_valid_host));
    }


    private void downloadFile(String url) throws IOException {
        String name = URLDecoder.decode(url.substring(url.lastIndexOf("/") + 1, url.indexOf("?")), "UTF-8");
        CacheFile cf = new CacheFile(this, name);

        if (cf.exists()) {
            log(getString(R.string.lbl_already_exist, name));
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
        } catch (IOException | StringIndexOutOfBoundsException e) {
            Log.e("下载", name, e);
            cf.remove();
        }

    }


    public final static String DZJ_NAME = "dzj-f";
    public final static String DICT_NAME = "dict";

    private WidgetHelper wh;
    private KvHelper kh;
    private NotificationManager nm;
    private Notification notification;
    private Intent updateIntent;
    private StringBuilder sb;

    private final int SUCCESS = 1;
    private final int FAIL = 2;
    private final int INCREASE = 3;
    private Handler handler = new Handler(new Handler.Callback() {

        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case SUCCESS:
                    notification.defaults = Notification.DEFAULT_ALL;
                    notification.contentIntent = PendingIntent.getActivity(SyncService.this, 0, new Intent(SyncService.this, MainActivity.class), 0);
                    notification.contentView.setTextViewText(R.id.tv_notice_bar, getString(R.string.lbl_success));
                    notification.contentView.setProgressBar(R.id.pb_notice_bar, 100, 100, false);
                    nm.notify(0, notification);
                    kh.set("sync.last", new Date());
                    kh.set("sync.log", sb.toString());
                    stopService(updateIntent);
                    break;
                case FAIL:
                    notification.defaults = Notification.DEFAULT_ALL;
                    notification.contentView.setTextViewText(R.id.tv_notice_bar, getString(R.string.lbl_fail));
                    nm.notify(0, notification);
                    kh.set("sync.log", sb.toString());
                    stopService(updateIntent);
                    break;
                case INCREASE:
                    notification.contentView.setProgressBar(R.id.pb_notice_bar, 100, msg.arg1, false);
                    nm.notify(0, notification);
                    break;
            }
            return true;
        }
    });


//    private String appendF(String url, int i) {
//        String[] ss = url.split("/");
//        ss[i] += "-f";
//        return DZJ_NAME + "/" + Arrays.asList(ss).toString().replaceAll("(^\\[|\\]$)", "").replace(", ", "/");
//    }
//
//    private void importBooks() {
//        CacheFile cf = new CacheFile(this, DZJ_NAME + "/index.html");
//        if (!cf.exists()) {
//            log(getString(R.string.lbl_file_not_exist, DZJ_NAME));
//            return;
//        }
//
//        try {
//            Document doc = Jsoup.parse(cf.getRealFile(), "UTF-8");
//            List<Dzj> books = new ArrayList<Dzj>();
//            Elements links = doc.select("a");
//
//            for (Element el : links) {
//                String url = el.attr("href");
//                if (url.endsWith(".txt")) {
//                    Dzj d = new Dzj();
//
//                    d.setTitle(el.text());
//                    d.setAuthor(el.parent().nextElementSibling().text());
//
//                    if (url.startsWith("T/")) {
//                        d.setType("大正藏");
//                        d.setName(appendF(url, 2));
//                    } else if (url.startsWith("X/")) {
//                        d.setType("卐續藏");
//                        d.setName(appendF(url, 2));
//                    } else if (url.startsWith("J/J")) {
//                        d.setType("嘉興藏");
//                        d.setName(appendF(url, 1));
//                    } else if (url.startsWith("A/A")) {
//                        d.setType("趙城金藏");
//                        d.setName(appendF(url, 1));
//                    } else if (url.startsWith("C/C")) {
//                        d.setType("中華藏");
//                        d.setName(appendF(url, 1));
//                    } else if (url.startsWith("F/F")) {
//                        d.setType("房山石經");
//                        d.setName(appendF(url, 1));
//                    } else if (url.startsWith("G/G")) {
//                        d.setType("佛教大藏經");
//                        d.setName(appendF(url, 1));
//                    } else if (url.startsWith("K/K")) {
//                        d.setType("高麗藏");
//                        d.setName(appendF(url, 1));
//                    } else if (url.startsWith("L/L")) {
//                        d.setType("乾隆藏");
//                        d.setName(appendF(url, 1));
//                    } else if (url.startsWith("M/M")) {
//                        d.setType("卍正藏");
//                        d.setName(appendF(url, 1));
//                    } else if (url.startsWith("P/P")) {
//                        d.setType("永樂北藏");
//                        d.setName(appendF(url, 1));
//                    } else if (url.startsWith("S/S")) {
//                        d.setType("宋藏遺珍");
//                        d.setName(appendF(url, 1));
//                    } else if (url.startsWith("U/U")) {
//                        d.setType("洪武南藏");
//                        d.setName(appendF(url, 1));
//                    } else if (url.startsWith("N/N")) {
//                        d.setType("漢譯南傳大藏經（元亨寺版）");
//                        d.setName(appendF(url, 1));
//                    } else {
//                        d.setType("其它");
//                    }
//                    //d.setName(el.parent().parent().parent().previousElementSibling().previousElementSibling().child(0).text());
//                    Log.d("抓取", d.toString());
//                    books.add(d);
//                }
//            }
//
//            DwDbHelper ddh = new DwDbHelper(this);
//            ddh.resetDzj(books);
//            ddh.close();
//
//            Log.d("导入", "成功");
//            log(getString(R.string.lbl_import_complete, books.size()));
//
//        } catch (IOException e) {
//            fail(getString(R.string.lbl_error_import));
//            Log.e("导入数据", cf.getName(), e);
//        }
//    }

}
