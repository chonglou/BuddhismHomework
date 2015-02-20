package com.odong.buddhismhomework.dict;

import android.util.Log;

import com.odong.buddhismhomework.models.CacheFile;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Serializable;

/**
 * Created by flamen on 15-2-19.
 */
public class IfoFile {
    public IfoFile(File file) throws IOException{
        String line;
            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(cf.getRealFile())));
            while ((line = br.readLine()) != null) {
                if (line.startsWith("bookname=")) {
                    name = line.split("=")[1];

                }
                else if(line.startsWith("version=")){
                    version = line.split("=")[1];
                }
                else if(line.startsWith("author=")){

                }
            }
            br.close();


    }
    private String bookName(String dict) {
        CacheFile cf = new CacheFile(context, NAME + "/" + dict + "/" + dict + ".ifo");


        return ""+name+"("+version+")";
    }
    private String name;
    private String author;
    private String description;
    private String version;
    private int count;
    private int size;

    public String getName() {
        return name;
    }

    public String getAuthor() {
        return author;
    }

    public String getDescription() {
        return description;
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
