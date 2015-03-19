package com.odong.buddhismhomework.models;

import java.io.Serializable;

/**
 * Created by flamen on 15-3-19.
 */
public class Point implements Serializable {
    private int x;
    private int y;

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }
}
