package com.odong.buddhismhomework.pages;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.odong.buddhismhomework.R;
import com.odong.buddhismhomework.models.CacheFile;
import com.odong.buddhismhomework.utils.DwDbHelper;
import com.odong.buddhismhomework.utils.WidgetHelper;

/**
 * Created by flamen on 15-3-5.
 */
public class DdcActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web);

        getActionBar().setIcon(getIntent().getIntExtra("icon", R.drawable.ic_ddc));

        String title = getIntent().getStringExtra("title");
        index = "file://" + new CacheFile(DdcActivity.this, "/ddc/HTML/INDEX.HTM").getRealFile().getAbsolutePath();

        if (title == null) {
            getString(R.string.title_ddc);
        }
        String url = getIntent().getStringExtra("url");
        if (url == null) {
            url = index;
        }

        setTitle(title);
        initWebView(url);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_ddc, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        WidgetHelper wh = new WidgetHelper(this);
        switch (id) {
            case R.id.action_add_to_favorites:
                AlertDialog.Builder adbF = new AlertDialog.Builder(this);
                adbF.setTitle(R.string.action_add_to_favorites);
                adbF.setMessage(R.string.lbl_are_you_sure);
                adbF.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        WebView wv = (WebView) findViewById(R.id.wv_content);

                        DwDbHelper ddh = new DwDbHelper(DdcActivity.this);
                        ddh.setFavorite("ddc", 0, wv.getTitle(), wv.getUrl(), true);
                        ddh.close();
                        new WidgetHelper(DdcActivity.this).toast(getString(R.string.lbl_success), false);
                    }
                });
                adbF.setNegativeButton(android.R.string.no, null);
                adbF.create().show();
                break;
            case R.id.action_zoom_in:
                wh.zoomWebView(R.id.wv_content, false);
                break;
            case R.id.action_zoom_out:
                wh.zoomWebView(R.id.wv_content, true);
                break;
            case R.id.action_home:
                startActivity(new Intent(this, MainActivity.class));
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }

    private void initWebView(String url) {
        WebView wv = (WebView) findViewById(R.id.wv_content);
        wv.setWebViewClient(new WebViewClient() {

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                Log.d("打开", url);
                view.loadUrl(url);
                setTitle(view.getTitle());
                return true;
            }
        });

        wv.getSettings().setJavaScriptEnabled(true);
        wv.getSettings().setDomStorageEnabled(true);

        Log.d("打开", url);
        wv.loadUrl(url);

        new WidgetHelper(this).setWebViewFont(R.id.wv_content);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        WebView wv = (WebView) findViewById(R.id.wv_content);
        if ((keyCode == KeyEvent.KEYCODE_BACK) && wv.canGoBack()) {
            // wv.goBack();
            Log.d("返回", "首页");
            wv.loadUrl(index);
            setTitle(wv.getTitle());
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }


    private String index;


}
