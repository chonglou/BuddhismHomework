package com.odong.buddhismhomework;

import android.app.Activity;
import android.os.Bundle;

/**
 * Created by flamen on 15-2-20.
 */
public class SearchActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_items);
        getActionBar().setIcon(android.R.drawable.ic_menu_search);
        String keyword = getIntent().getStringExtra("keyword");
        String type = getIntent().getStringExtra("type");
        setTitle(getString(R.string.lbl_search_result, keyword));

        if ("dzj".equals(type)) {
            initDzjList();
        }

    }

    private void initDzjList() {

    }
}
