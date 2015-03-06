package com.odong.buddhismhomework.models;

import android.content.Context;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.Serializable;

import nl.siegmann.epublib.epub.EpubReader;

/**
 * Created by flamen on 15-2-19.
 */
public class Book implements Serializable {
    public nl.siegmann.epublib.domain.Book toEpub(Context context) throws IOException {
        return new EpubReader().readEpub(new FileInputStream(new CacheFile(context, "/cbeta/" + name).getRealFile()));
    }

    public String getScrollId() {
        return "scroll://book/" + id;
    }

    @Override
    public String toString() {
        return title;
    }

    private String author;
    private String name;
    private String title;
    private boolean fav;
    private int id;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isFav() {
        return fav;
    }

    public void setFav(boolean fav) {
        this.fav = fav;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
