package com.odong.buddhismhomework.back;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.odong.buddhismhomework.models.CacheFile;
import com.odong.buddhismhomework.models.Index;
import com.odong.buddhismhomework.utils.KvHelper;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by flamen on 15-2-21.
 */
public class IndexService extends IntentService {

    public IndexService() {
        super("IndexService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        String type = intent.getStringExtra("type");
        String file = intent.getStringExtra("file");
        final String name = type2name(type, file);


        try {
            if ("dzj".equals(type)) {
                index(name, new FileInputStream(new CacheFile(this, file).getRealFile()));
            } else if ("book".equals(type)) {
                index(name, (FileInputStream) getResources().openRawResource(Integer.parseInt(file)));
            }
            Log.d("创建索引完成", name);
        } catch (IOException e) {
            Log.e("创建索引", name, e);
        }

    }

    private void index(String name, FileInputStream fis) throws IOException {

        Index idx = new Index();
        BufferedReader br = new BufferedReader(new InputStreamReader(fis));
        int i = 0;
        while (br.readLine() != null) {
            i++;
            if (i == LINES) {
                idx.getPositions().add(fis.getChannel().position());
            }
        }
        br.close();
        new KvHelper(this).set(name, idx);
    }

    public final static int LINES = 120;

    public static String type2name(String type, String file) {
        return type + "-" + file + ".inx";
    }
}
