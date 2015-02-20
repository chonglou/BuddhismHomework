package com.odong.buddhismhomework.utils;

import android.content.Context;
import android.util.Log;

import com.google.gson.internal.LinkedHashTreeMap;
import com.odong.buddhismhomework.models.CacheFile;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by flamen on 15-2-18.
 */
public class DictHelper {
    public DictHelper(Context context) {

        this.context = context;

        initDictMap();

    }

    public Map<String, String> search(String keyword) {
        Map<String, String> map = new LinkedHashTreeMap<>();
        for (Map.Entry<String, String> e : dictMap.entrySet()) {
            map.put(e.getValue(), search(keyword, e.getKey()));
        }
        return map;
    }

    private String search(String keyword, String dict) {
        return keyword;
    }

    private void initDictMap() {
        this.dictMap = new HashMap<>();
        CacheFile root = new CacheFile(context, NAME);
        if (root.exists()) {
            String[] dirs = root.getRealFile().list(new FilenameFilter() {
                @Override
                public boolean accept(File dir, String filename) {
                    return new File(dir, filename).isDirectory();
                }
            });
            for (String d : dirs) {
                String n = bookName(d);
                if (n != null) {
                    dictMap.put(d, n);
                }

            }
            //Log.d("加载字典", Arrays.asList(dirs).toString());
            Log.d("加载字典", dictMap.toString());
        }
    }

    private String bookName(String dict) {
        CacheFile cf = new CacheFile(context, NAME + "/" + dict + "/" + dict + ".ifo");

        String line;
        String name = null;
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(cf.getRealFile())));
            while ((line = br.readLine()) != null) {
                if (line.startsWith("bookname=")) {
                    name = line.split("=")[1];
                    break;
                }
            }
            br.close();

        } catch (IOException e) {
            Log.e("加载字典", dict, e);
        }
        return name;
    }


    private Context context;
    private Map<String, String> dictMap;
    public final static String NAME = "dict";
}
