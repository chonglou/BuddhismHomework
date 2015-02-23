package com.odong.buddhismhomework.models;

/**
 * Created by flamen on 15-2-12.
 */
public class Pager {

    public boolean isInit() {
        return len > 0;
    }

    public int getSize() {
        return (len / LINES) - 1;
    }

    public boolean isFirst() {
        return cur <= 0;
    }

    public boolean isLast() {
        return cur >= getSize();
    }

    private int x;
    private int y;
    private int cur;
    private int len;
    public static final int LINES = 120;

    public int getLen() {
        return len;
    }

    public void setLen(int len) {
        this.len = len;
    }


    public int getCur() {
        return cur;
    }

    public void setCur(int cur) {
        this.cur = cur;
    }

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
