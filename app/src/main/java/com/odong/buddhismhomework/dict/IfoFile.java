package com.odong.buddhismhomework.dict;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by flamen on 15-2-19.
 */
public class IfoFile {
    @Override
    public String toString() {
        return name+"("+version+")";
    }

    public IfoFile(File file) throws IOException {
        String line;
        BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
        while ((line = br.readLine()) != null) {
            if (line.startsWith("bookname=")) {
                name = stringValue(line);

            } else if (line.startsWith("version=")) {
                version = stringValue(line);
            } else if (line.startsWith("idxfilesize=")) {
                size = intValue(line);
            } else if (line.startsWith("wordcount=")) {
                count = intValue(line);
            }
        }
        br.close();
    }

    private String stringValue(String line) {
        return line.split("=")[1];
    }

    private int intValue(String line) {
        return Integer.parseInt(stringValue(line));
    }

    private String name;
    private String version;
    private int count;
    private int size;

    public String getName() {
        return name;
    }

    public String getVersion() {
        return version;
    }

    public int getCount() {
        return count;
    }

    public int getSize() {
        return size;
    }
}
