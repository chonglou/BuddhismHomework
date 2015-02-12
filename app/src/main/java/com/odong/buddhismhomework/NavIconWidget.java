package com.odong.buddhismhomework;

import android.content.Context;
import android.media.Image;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;

/**
 * Created by flamen on 15-2-11.
 */
public class NavIconWidget extends FrameLayout {
    public NavIconWidget(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.nav_icon, this);
    }

    public void setContent(int titleId, int imageId, OnClickListener listener){
        ((ImageButton)findViewById(R.id.btn_nav_icon)).setImageResource(imageId);
        ((TextView)findViewById(R.id.tv_nav_icon)).setText(titleId);
        findViewById(R.id.nav_icon).setOnClickListener(listener);
    }

}
