package com.odong.buddhismhomework.models;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by flamen on 15-2-7.
 */
public class Calendar implements Serializable {
    private int hour;
    private int minute;
    private int length;
    private boolean mon;
    private boolean tues;
    private boolean wed;
    private boolean thur;
    private boolean fri;
    private boolean sat;
    private boolean sun;

    public int getHour() {
        return hour;
    }

    public void setHour(int hour) {
        this.hour = hour;
    }

    public int getMinute() {
        return minute;
    }

    public void setMinute(int minute) {
        this.minute = minute;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public boolean isMon() {
        return mon;
    }

    public void setMon(boolean mon) {
        this.mon = mon;
    }

    public boolean isTues() {
        return tues;
    }

    public void setTues(boolean tues) {
        this.tues = tues;
    }

    public boolean isWed() {
        return wed;
    }

    public void setWed(boolean wed) {
        this.wed = wed;
    }

    public boolean isThur() {
        return thur;
    }

    public void setThur(boolean thur) {
        this.thur = thur;
    }

    public boolean isFri() {
        return fri;
    }

    public void setFri(boolean fri) {
        this.fri = fri;
    }

    public boolean isSat() {
        return sat;
    }

    public void setSat(boolean sat) {
        this.sat = sat;
    }

    public boolean isSun() {
        return sun;
    }

    public void setSun(boolean sun) {
        this.sun = sun;
    }
}
