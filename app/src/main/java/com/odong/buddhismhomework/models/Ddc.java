package com.odong.buddhismhomework.models;

import java.io.Serializable;

/**
 * Created by flamen on 15-3-5.
 */
public class Ddc implements Serializable {
    private String content;
    private String url;
    private int id;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
