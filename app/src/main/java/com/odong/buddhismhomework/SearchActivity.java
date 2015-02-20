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
        getActionBar().setIcon(R.drawable.ic_dzj);
        setTitle(R.string.action_favorites);

        initList();
    }
}
