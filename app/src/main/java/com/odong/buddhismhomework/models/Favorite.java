package com.odong.buddhismhomework.models;

import java.io.Serializable;

/**
 * Created by flamen on 15-2-26.
 */
public class Favorite implements Serializable {
    private String type;
    private String title;
    private Object obj;
    private String details;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Object getObj() {
        return obj;
    }

    public void setObj(Object obj) {
        this.obj = obj;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }
}
