package com.odong.buddhismhomework.models;

import android.view.View;

import java.io.Serializable;

/**
 * Created by flamen on 15-2-12.
 */
public class NavIcon implements Serializable {
    public NavIcon(int title, int image, View.OnClickListener click) {
        this.title = title;
        this.image = image;
        this.click = click;
    }

    private int title;
    private int image;
    private View.OnClickListener click;

    public int getTitle() {
        return title;
    }

    public int getImage() {
        return image;
    }

    public View.OnClickListener getClick() {
        return click;
    }
}
