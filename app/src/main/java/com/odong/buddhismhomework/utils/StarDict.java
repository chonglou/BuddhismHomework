package com.odong.buddhismhomework.utils;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by flamen on 15-2-19.
 */
public class StarDict {
    public static List<StarDict> load(File root) throws IOException {
        List<StarDict> dictList = new ArrayList<>();
        if (root.exists()) {
            String[] dirs = root.list(new FilenameFilter() {
                @Override
                public boolean accept(File dir, String filename) {
                    return new File(dir, filename).isDirectory();
                }
            });
            for (String d : dirs) {
                dictList.add(new StarDict(root, d));
            }
        }
        return dictList;
    }

    public String search(String keyword) throws IOException {
        Entry entry = searchInIndex(keyword);
        if (entry != null) {
            return searchInDict(entry);
        }
        return null;
    }

    @Override
    public String toString() {
        return info.name + "(" + info.version + ")";
    }

    public StarDict(File root, String name) throws IOException {
        info = new Info();
        loadInfo(new File(root, name + "/" + name + ".ifo"));
        idx = new File(root, name + "/" + name + ".idx");
        dict = new File(root, name + "/" + name + ".dict");
    }

    private String searchInDict(Entry entry) throws IOException {
        DataInputStream dis = new DataInputStream(new BufferedInputStream(new FileInputStream(dict)));
        dis.skip(entry.offset);
        byte[] bt = new byte[entry.size];
        dis.close();
        return new String(bt, "UTF-8");
    }

    private Entry searchInIndex(String keyword) throws IOException {
        Entry entry = null;
        DataInputStream dis = new DataInputStream(new BufferedInputStream(new FileInputStream(idx)));
        byte[] bt = new byte[info.size];
        dis.read(bt);
        dis.close();

        int start;
        int end = 0;
        for (int i = 0; i < info.count; i++) {
            start = end;
            while (bt[end] != '\0') {
                end++;
            }
            String word = new String(bt, start, end - start, "UTF-8");
            ++end;

            if (word.toLowerCase().equals(keyword.toLowerCase())) {
                entry = new Entry();
                entry.word = word;
                entry.offset = readInt32(bt, end);
                end += 4;
                entry.size = readInt32(bt, end);
                end += 4;
            } else {
                end += 8;
            }
        }
        return entry;
    }

    private void loadInfo(File file) throws IOException {

        String line;
        BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
        while ((line = br.readLine()) != null) {
            if (line.startsWith("bookname=")) {
                info.name = stringValue(line);

            } else if (line.startsWith("version=")) {
                info.version = stringValue(line);
            } else if (line.startsWith("idxfilesize=")) {
                info.size = intValue(line);
            } else if (line.startsWith("wordcount=")) {
                info.count = intValue(line);
            }
        }
        br.close();
    }

    private String stringValue(String line) {
        return line.split("=")[1];
    }

    private long longValue(String line) {
        return Long.parseLong(stringValue(line));
    }

    private int intValue(String line) {
        return Integer.parseInt(stringValue(line));
    }

    private int readInt32(byte[] str, int beginPos) {

        int firstByte = (0x000000FF & ((int) str[beginPos]));
        int secondByte = (0x000000FF & ((int) str[beginPos + 1]));
        int thirdByte = (0x000000FF & ((int) str[beginPos + 2]));
        int fourthByte = (0x000000FF & ((int) str[beginPos + 3]));

        return firstByte << 24 | secondByte << 16 | thirdByte << 8 | fourthByte;
    }

    private class Info {
        private String name;
        private String version;
        private int count;
        private int size;
    }

    private class Word {
        private String word;
        private Integer index;
    }

    private class Entry {
        private String word;
        private int size;
        private int offset;
    }

    private final File idx;
    private final File dict;
    private final Info info;


}
