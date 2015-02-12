package com.odong.buddhismhomework.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by flamen on 15-2-12.
 */
public class Book implements Serializable {
    public Book() {
        files = new ArrayList<Integer>();
    }

    @Override
    public String toString() {
        return name;
    }

    private String name;
    private String author;
    private List<Integer> files;

    public List<Integer> getFiles() {
        return files;
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
