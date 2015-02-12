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
    private String name;
    private List<Integer> incantations;


    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public List<Integer> getIncantations() {
        return incantations;
    }


}
