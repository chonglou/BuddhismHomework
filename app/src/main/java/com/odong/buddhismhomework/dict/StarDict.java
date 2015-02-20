package com.odong.buddhismhomework.dict;

import android.util.Log;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by flamen on 15-2-19.
 */
public class StarDict {
    public static List<StarDict> load(File root) throws IOException{
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
    public String search(String keyword){
        return keyword;
    }

    @Override
    public String toString() {
        return ifo.toString();
    }

    public StarDict(File root, String name) throws IOException{
        ifo = new IfoFile(new File(root, name+"/"+name+".ifo"));
        idx = new IdxFile(new File(root, name+"/"+name+".idx"));
        dict = new DictFile(new File(root, name+"/"+name+".dict"));
    }
    private IfoFile ifo;
    private IdxFile idx;
    private DictFile dict;


}
