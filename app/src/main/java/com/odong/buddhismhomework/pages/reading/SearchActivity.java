package com.odong.buddhismhomework.pages.reading;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import com.odong.buddhismhomework.R;
import com.odong.buddhismhomework.models.Dzj;
import com.odong.buddhismhomework.utils.ChineseConverter;
import com.odong.buddhismhomework.utils.DwDbHelper;
import com.odong.buddhismhomework.utils.WidgetHelper;

import java.io.IOException;
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

        initChineseConverter();
        if (!keyword.isEmpty()) {
            if ("dzj".equals(type)) {
                if (chineseConverter != null) {
                    keyword = chineseConverter.s2t(keyword);
                }
                initDzjList(keyword);
            }
        }
    }

    private void initDzjList(String keyword) {
        DwDbHelper ddh = new DwDbHelper(this);
        List<Dzj> books = ddh.searchDzj(keyword);
        new WidgetHelper(this).initDzjBookList(R.id.lv_items, books, null);
    }

    private void initChineseConverter() {
        try {
            chineseConverter = new ChineseConverter(getResources().openRawResource(R.raw.ts), "UTF-8");
        } catch (IOException e) {
            Log.d("加载", "繁简转换表", e);
        }

    }

    private ChineseConverter chineseConverter;
}
