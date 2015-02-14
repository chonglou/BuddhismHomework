package com.odong.buddhismhomework.models;

import java.io.Serializable;

/**
 * Created by flamen on 15-2-13.
 */
public class Host implements Serializable {
    private String url;
    private String name;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
