package com.odong.buddhismhomework;

import android.app.Activity;
import android.os.Bundle;

import com.odong.buddhismhomework.models.Dzj;
import com.odong.buddhismhomework.utils.DwDbHelper;
import com.odong.buddhismhomework.utils.WidgetHelper;

import java.util.List;

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

        if (!keyword.isEmpty()) {
            if ("dzj".equals(type)) {
                initDzjList(keyword);
            }
        }
    }

    private void initDzjList(String keyword) {
        DwDbHelper ddh = new DwDbHelper(this);
        List<Dzj> books = ddh.searchDzj(keyword);
        new WidgetHelper(this).initDzjBookList(books, null);
    }
}
