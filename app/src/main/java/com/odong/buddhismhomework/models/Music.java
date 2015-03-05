package com.odong.buddhismhomework.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by flamen on 15-2-12.
 */
public class Music implements Serializable {
    public Music() {
        files = new ArrayList<Integer>();
    }

    public String getScrollId() {
        return "scroll://music/" + name;
    }

    @Override
    public String toString() {
        return name;
    }


    private String name;
    private String author;
    private String mp3;
    private List<Integer> files;

    public List<Integer> getFiles() {
        return files;
    }

    public String getMp3() {
        return mp3;
    }

    public void setMp3(String mp3) {
        this.mp3 = mp3;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }
}
