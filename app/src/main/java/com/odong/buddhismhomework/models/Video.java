package com.odong.buddhismhomework.models;

import com.google.gson.internal.LinkedHashTreeMap;

import java.io.Serializable;
import java.util.Map;

/**
 * Created by flamen on 15-2-12.
 */
public class Video implements Serializable {
    public Video() {
        this.items = new LinkedHashTreeMap<String, String>();
    }

    private String name;
    private String author;
    private Map<String, String> items;

    public Map<String, String> getItems() {
        return items;
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
