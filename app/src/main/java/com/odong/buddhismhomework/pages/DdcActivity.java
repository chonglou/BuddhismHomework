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

import com.google.gson.Gson;
import com.odong.buddhismhomework.R;
import com.odong.buddhismhomework.models.Ddc;
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

        setTitle(getIntent().getIntExtra("title", R.string.title_ddc));
        getActionBar().setIcon(getIntent().getIntExtra("icon", R.drawable.ic_ddc));

        Ddc ddc = new Gson().fromJson(getIntent().getStringExtra("ddc"), Ddc.class);
        setTitle(ddc.getTitle());
        initWebView(ddc);

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
            case R.id.action_add_to_favorites:
                AlertDialog.Builder adbF = new AlertDialog.Builder(this);
                adbF.setTitle(R.string.action_add_to_favorites);
                adbF.setMessage(R.string.lbl_are_you_sure);
                adbF.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        WebView wv = (WebView) findViewById(R.id.wv_content);

                        DwDbHelper ddh = new DwDbHelper(DdcActivity.this);
                        Ddc ddc = ddh.getDdc(wv.getUrl());
                        ddh.setFavorite("ddc", ddc.getId(), ddc.getTitle(), true);
                        ddh.close();
                        new WidgetHelper(DdcActivity.this).toast(getString(R.string.lbl_success), false);
                    }
                });
                adbF.setNegativeButton(android.R.string.no, null);
                adbF.create().show();
                break;
            case R.id.action_home:
                startActivity(new Intent(this, MainActivity.class));
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }

    private void initWebView(Ddc ddc) {
        WebView wv = (WebView) findViewById(R.id.wv_content);
        wv.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {

                Log.d("链接", url);
                Ddc ddc = new DwDbHelper(DdcActivity.this).getDdc(url);
                view.loadDataWithBaseURL(null, ddc.getContent(), "text/html", "utf-8", null);
                setTitle(ddc.getTitle());
                return true;
            }
        });
        wv.getSettings().setDomStorageEnabled(true);
        wv.loadDataWithBaseURL(null, ddc.getContent(), "text/html", "utf-8", null);
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
