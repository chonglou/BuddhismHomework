package com.odong.buddhismhomework.services;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.odong.buddhismhomework.Config;
import com.odong.buddhismhomework.R;
import com.odong.buddhismhomework.models.CacheFile;
import com.odong.buddhismhomework.models.Dzj;
import com.odong.buddhismhomework.utils.DwDbHelper;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
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
    protected void onHandleIntent(Intent intent) {
        WidgetHelper wh = new WidgetHelper(this);
        wh.toast(getString(R.string.lbl_begin_download), true);
        KvHelper kh = new KvHelper(this);
        Integer type = kh.get("host.type", Integer.class, R.id.btn_setting_home_dropbox);

        switch (type) {
            case R.id.btn_setting_home_dropbox:
                onDropbox(intent);
                break;
            case R.id.btn_setting_home_baiduyun:
                onBaiduyun();
                break;
            default:
                wh.toast(getString(R.string.lbl_unknown_host), true);
        }

        kh.set("sync.last", new Date());

        new WidgetHelper(this).toast(getString(R.string.lbl_begin_import), true);
        unzip(DICT_NAME);
        unzip(DZJ_NAME);

        importBooks(intent);
    }


    private String appendF(String url, int i) {
        String[] ss = url.split("/");
        ss[i] += "-f";
        return DZJ_NAME + "/" + Arrays.asList(ss).toString().replaceAll("(^\\[|\\]$)", "").replace(", ", "/");
    }

    private void importBooks(Intent intent) {
        WidgetHelper wh = new WidgetHelper(this);
        CacheFile cf = new CacheFile(this, DZJ_NAME + "/index.html");
        if (!cf.exists()) {
            wh.notification(intent, getString(R.string.lbl_file_not_exist, DZJ_NAME));
            return;
        }

        try {
            Document doc = Jsoup.parse(cf.getRealFile(), "UTF-8");
            List<Dzj> books = new ArrayList<Dzj>();
            Elements links = doc.select("a");

            for (Element el : links) {
                String url = el.attr("href");
                if (url.endsWith(".txt")) {
                    Dzj d = new Dzj();

                    d.setTitle(el.text());
                    d.setAuthor(el.parent().nextElementSibling().text());

                    if (url.startsWith("T/")) {
                        d.setType("大正藏");
                        d.setName(appendF(url, 2));
                    } else if (url.startsWith("X/")) {
                        d.setType("卐續藏");
                        d.setName(appendF(url, 2));
                    } else if (url.startsWith("J/J")) {
                        d.setType("嘉興藏");
                        d.setName(appendF(url, 1));
                    } else if (url.startsWith("A/A")) {
                        d.setType("趙城金藏");
                        d.setName(appendF(url, 1));
                    } else if (url.startsWith("C/C")) {
                        d.setType("中華藏");
                        d.setName(appendF(url, 1));
                    } else if (url.startsWith("F/F")) {
                        d.setType("房山石經");
                        d.setName(appendF(url, 1));
                    } else if (url.startsWith("G/G")) {
                        d.setType("佛教大藏經");
                        d.setName(appendF(url, 1));
                    } else if (url.startsWith("K/K")) {
                        d.setType("高麗藏");
                        d.setName(appendF(url, 1));
                    } else if (url.startsWith("L/L")) {
                        d.setType("乾隆藏");
                        d.setName(appendF(url, 1));
                    } else if (url.startsWith("M/M")) {
                        d.setType("卍正藏");
                        d.setName(appendF(url, 1));
                    } else if (url.startsWith("P/P")) {
                        d.setType("永樂北藏");
                        d.setName(appendF(url, 1));
                    } else if (url.startsWith("S/S")) {
                        d.setType("宋藏遺珍");
                        d.setName(appendF(url, 1));
                    } else if (url.startsWith("U/U")) {
                        d.setType("洪武南藏");
                        d.setName(appendF(url, 1));
                    } else if (url.startsWith("N/N")) {
                        d.setType("漢譯南傳大藏經（元亨寺版）");
                        d.setName(appendF(url, 1));
                    } else {
                        d.setType("其它");
                    }
                    //d.setName(el.parent().parent().parent().previousElementSibling().previousElementSibling().child(0).text());
                    Log.d("抓取", d.toString());
                    books.add(d);
                }
            }

            DwDbHelper ddh = new DwDbHelper(this);
            ddh.resetDzj(books);
            ddh.close();
            new KvHelper(this).set("import.last", new Date());

            Log.d("导入", "成功");
            wh.notification(intent, getString(R.string.lbl_import_complete, books.size()));

        } catch (IOException e) {
            wh.notification(intent, getString(R.string.lbl_error_import));
            Log.e("导入数据", cf.getName(), e);
        }
    }

    private void unzip(String name) {
        WidgetHelper wh = new WidgetHelper(this);
        File file = new File(new CacheFile(this, name + ".zip").getRealFile().getAbsolutePath());
        File root = new File(new CacheFile(this, name).getRealFile().getAbsolutePath());

        if (!file.exists()) {
            wh.toast(getString(R.string.lbl_file_not_exist, name + ".zip"), true);
            return;
        }
        if (root.exists()) {
            wh.toast(getString(R.string.lbl_already_exist, name), true);
            return;
        }
        try {
            ZipInputStream zis = new ZipInputStream(new BufferedInputStream(new FileInputStream(file)));
            ZipEntry ze;
            while ((ze = zis.getNextEntry()) != null) {
                File f = new File(file.getParentFile(), ze.getName());
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
            Log.d("解压缩完毕", name);
        } catch (IOException e) {
            root.delete();
            Log.d("解压缩", name, e);
            wh.toast(getString(R.string.lbl_error_unzip, name), true);
        }

    }


    private void onDropbox(Intent intent) {
        WidgetHelper wh = new WidgetHelper(this);
        try {
            Document doc = Jsoup.connect(Config.DROPBOX_URL).get();
            Elements links = doc.select("a.filename-link");
            for (Element el : links) {
                download(el.attr("href").replace("dl=0", "dl=1"));
            }
            wh.notification(intent, getString(R.string.lbl_download_complete, links.size()));
        } catch (IOException e) {
            Log.e("下载", "IO", e);
            wh.notification(intent, getString(R.string.lbl_download_error, e.getMessage()));
        }

    }

    private void onBaiduyun() {
        WidgetHelper wh = new WidgetHelper(this);
        wh.toast(getString(R.string.lbl_no_valid_host), true);
    }


    private void download(String url) throws IOException {
        String name = URLDecoder.decode(url.substring(url.lastIndexOf("/") + 1, url.indexOf("?")), "UTF-8");
        CacheFile cf = new CacheFile(this, name);
        WidgetHelper wh = new WidgetHelper(this);
        if (cf.exists()) {
            wh.toast(getString(R.string.lbl_already_exist, name), true);
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
            wh.toast(getString(R.string.lbl_download_success, name), true);
            Log.d("下载完成", name);
        } catch (IOException | StringIndexOutOfBoundsException e) {
            Log.e("下载", name, e);
            cf.remove();
        }

    }


    public final static String DZJ_NAME = "dzj-f";
    public final static String DICT_NAME = "dict";
}
