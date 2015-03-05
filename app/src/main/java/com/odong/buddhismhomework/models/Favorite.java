package com.odong.buddhismhomework.models;

import com.odong.buddhismhomework.R;

import java.io.Serializable;

/**
 * Created by flamen on 15-2-26.
 */
public class Favorite implements Serializable {
    public int getTypeId() {
        switch (type) {
            case "ddc":
                return R.string.title_ddc;
            case "dzj":
                return R.string.title_dzj;
        }
        return R.string.lbl_null;
    }

    private String type;
    private String title;
    private int id;
    private int tid;


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getTid() {
        return tid;
    }

    public void setTid(int tid) {
        this.tid = tid;
    }

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

}
