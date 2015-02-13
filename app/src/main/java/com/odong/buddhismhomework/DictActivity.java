package com.odong.buddhismhomework;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.odong.buddhismhomework.dict.StarDict;

/**
 * Created by flamen on 15-2-12.
 */
public class DictActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dict);
        setTitle(R.string.title_dict);


        findViewById(R.id.btn_dict_search).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String key = ((EditText) findViewById(R.id.et_dict_key)).getText().toString();
                Log.d("字典", key);
                ((TextView) findViewById(R.id.tv_dict_content)).setText(search(key));
            }
        });
    }


    private String search(String key) {

        return null;
    }

    private StarDict dict;
}
