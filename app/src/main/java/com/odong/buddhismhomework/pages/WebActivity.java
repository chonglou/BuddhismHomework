package com.odong.buddhismhomework.pages;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebView;
import android.webkit.WebViewClient;

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

        initWebView(getIntent().getBooleanExtra("js", true));

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_web, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_home:
                startActivity(new Intent(this, MainActivity.class));
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }

    private void initWebView(boolean js) {
        WebView wv = (WebView) findViewById(R.id.wv_content);
        wv.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });
        wv.getSettings().setJavaScriptEnabled(js);
        wv.getSettings().setDomStorageEnabled(true);
        wv.loadUrl(getIntent().getStringExtra("url"));

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        WebView wv = (WebView) findViewById(R.id.wv_content);
        if ((keyCode == KeyEvent.KEYCODE_BACK) && wv.canGoBack()) {
            wv.goBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
