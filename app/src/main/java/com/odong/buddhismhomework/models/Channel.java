package com.odong.buddhismhomework.models;

import java.io.Serializable;

/**
 * Created by flamen on 15-3-4.
 */
public class Channel implements Serializable {
    private String cid;
    private int id;
    private String title;
    private String description;

    public String getCid() {
        return cid;
    }

    public void setCid(String cid) {
        this.cid = cid;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
