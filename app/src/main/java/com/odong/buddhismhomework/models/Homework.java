package com.odong.buddhismhomework.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by flamen on 15-2-12.
 */
public class Homework implements Serializable {
    public Homework() {
        this.incantations = new ArrayList<Integer>();
    }

    @Override
    public String toString() {
        return name;
    }

    private String id;
    private String type;
    private String name;
    private List<Integer> incantations;

    public void setId(String id) {
        this.id = id;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public String getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public List<Integer> getIncantations() {
        return incantations;
    }



}
