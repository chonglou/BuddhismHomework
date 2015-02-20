package com.odong.buddhismhomework;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.odong.buddhismhomework.utils.DictHelper;

import java.util.Map;

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

        dictHelper = new DictHelper(this);

        findViewById(R.id.btn_dict_search).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String key = ((EditText) findViewById(R.id.et_dict_key)).getText().toString().trim();

                TextView tv = ((TextView) findViewById(R.id.tv_dict_content));


                if (key.isEmpty()) {
                    tv.setText(R.string.lbl_error_please_input);
                    return;
                }
                Map<String, String> map = dictHelper.search(key);
                if (map.isEmpty()) {
                    tv.setText(R.string.lbl_empty_results);
                    return;
                }
                StringBuilder sb = new StringBuilder();
                for (Map.Entry<String, String> e : map.entrySet()) {
                    sb.append("\n【");
                    sb.append(e.getKey());
                    sb.append("】\n");
                    sb.append(e.getValue());
                    sb.append("\n");
                }
                tv.setText(sb.toString());

            }
        });
    }

    private DictHelper dictHelper;

}
