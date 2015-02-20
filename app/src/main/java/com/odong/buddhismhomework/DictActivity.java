package com.odong.buddhismhomework;

import android.app.Activity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.odong.buddhismhomework.models.CacheFile;
import com.odong.buddhismhomework.utils.StarDict;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by flamen on 15-2-12.
 */
public class DictActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dict);
        setTitle(R.string.title_dict);
        getActionBar().setIcon(R.drawable.ic_dict);
        ((TextView) findViewById(R.id.tv_dict_content)).setMovementMethod(new ScrollingMovementMethod());
        initDictList();


        findViewById(R.id.btn_dict_search).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String key = ((EditText) findViewById(R.id.et_dict_key)).getText().toString().trim();

                TextView tv = ((TextView) findViewById(R.id.tv_dict_content));

                if (key.isEmpty()) {
                    tv.setText(R.string.lbl_error_please_input);
                    return;
                }

                if (dictList.isEmpty()) {
                    tv.setText(R.string.lbl_empty);
                    return;
                }
                StringBuilder sb = new StringBuilder();
                sb.append(getString(R.string.lbl_search_result, key));
                sb.append("\n");

                for (StarDict sd : dictList) {
                    String val;
                    try {
                        val = sd.search(key);
                        if (val == null) {
                            val = getString(R.string.lbl_empty_results);
                        }

                    } catch (IOException e) {
                        val = e.getMessage();
                        Log.d("搜索", key, e);
                    }
                    sb.append("\n【");
                    sb.append(sd.toString());
                    sb.append("】\n");
                    sb.append(val);
                    sb.append("\n");
                }
                tv.setText(sb.toString());
                tv.scrollTo(0, 0);

            }
        });
    }

    private void initDictList() {
        try {
            dictList = StarDict.load(new CacheFile(this, ImportService.DICT_NAME).getRealFile());
        } catch (IOException e) {
            dictList = new ArrayList<>();
            Log.e("加载字典", "出错", e);
        }
    }

    private List<StarDict> dictList;

}
