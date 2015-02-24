package com.odong.buddhismhomework.pages;

import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebView;

import com.odong.buddhismhomework.R;

/**
 * Created by flamen on 15-2-24.
 */
public class WebActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web);
        setTitle(R.string.title_ddc);
        getActionBar().setIcon(R.drawable.ic_ddc);

        ((WebView) findViewById(R.id.wv_content)).loadUrl(getIntent().getStringExtra("url"));
    }
}
