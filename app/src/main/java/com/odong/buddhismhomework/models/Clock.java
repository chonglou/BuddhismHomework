package com.odong.buddhismhomework.models;

import java.io.Serializable;

/**
 * Created by flamen on 15-2-12.
 */
public class Clock implements Serializable {
    private int minutes;
    private String name;

    @Override
    public String toString() {
        return name;
    }

    public int getMinutes() {
        return minutes;
    }

    public void setMinutes(int minutes) {
        this.minutes = minutes;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
