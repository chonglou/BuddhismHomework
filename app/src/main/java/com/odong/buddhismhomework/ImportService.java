package com.odong.buddhismhomework;

import android.app.IntentService;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import com.odong.buddhismhomework.models.CacheFile;
import com.odong.buddhismhomework.models.Dzj;
import com.odong.buddhismhomework.utils.DictHelper;
import com.odong.buddhismhomework.utils.DwDbHelper;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * Created by flamen on 15-2-19.
 */
public class ImportService extends IntentService {

    public ImportService() {
        super("ImportService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        unzip(DictHelper.NAME);
        unzip(DZJ_NAME);

        importBooks();
    }

    private String appendF(String url, int i) {
        String[] ss = url.split("/");
        ss[i] += "-f";
        return DZJ_NAME + "/" + Arrays.asList(ss).toString().replaceAll("(^\\[|\\]$)", "").replace(", ", "/");
    }

    private void importBooks() {
        CacheFile cf = new CacheFile(this, DZJ_NAME + "/index.html");
        if (!cf.exists()) {
            response(getString(R.string.lbl_file_not_exist, DZJ_NAME));
            return;
        }
        response(getString(R.string.lbl_begin_import));
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
            ddh.set("import.last", new Date());

            Log.d("导入", "成功");
            response(getString(R.string.lbl_import_complete, books.size()));

        } catch (IOException e) {
            response(getString(R.string.lbl_error_import));
            Log.e("导入数据", cf.getName(), e);
        }
    }

    private void unzip(String name) {

        File file = new File(new CacheFile(this, name + ".zip").getRealFile().getAbsolutePath());
        File root = new File(new CacheFile(this, name).getRealFile().getAbsolutePath());

        if (!file.exists()) {
            response(getString(R.string.lbl_file_not_exist, name + ".zip"));
            return;
        }
        if (root.exists()) {
            response(getString(R.string.lbl_already_exist, name));
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
            response(getString(R.string.lbl_error_unzip, name));
        }

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

    public final static String DZJ_NAME = "dzj-f";
    public final static String DICT_NAME = "dict";
}
