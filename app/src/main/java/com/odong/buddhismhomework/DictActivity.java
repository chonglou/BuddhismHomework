package com.odong.buddhismhomework;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.odong.buddhismhomework.dict.DictFile;
import com.odong.buddhismhomework.dict.IdxFile;
import com.odong.buddhismhomework.dict.IfoFile;
import com.odong.buddhismhomework.dict.StarDict;
import com.odong.buddhismhomework.models.CacheFile;

import java.io.IOException;

/**
 * Created by flamen on 15-2-12.
 */
public class DictActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dict);
        setTitle(R.string.title_dict);

        initDict();

        findViewById(R.id.btn_dict_search).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String key = ((EditText) findViewById(R.id.et_dict_key)).getText().toString();

                TextView tv = ((TextView) findViewById(R.id.tv_dict_content));
                try {
                    tv.setText(starDict.lookupWord(key));
                } catch (IOException e) {
                    Log.e("字典", "查询", e);
                    tv.setText(R.string.lbl_empty);
                }

            }
        });
    }


    private void initDict() {
        try {


            String ifo = getDictName("ifo");
            IfoFile ifoF = new IfoFile(openFileInput(ifo), openFileOutput(ifo, Context.MODE_PRIVATE));
            String idx = getDictName("idx");
            IdxFile idxF = new IdxFile(
                    openFileInput(idx),
                    openFileOutput(idx, Context.MODE_PRIVATE),
                    ifoF.getWordCount(), ifoF.getIdxFileSize());
            String dict = getDictName("dict");
            DictFile dictF = new DictFile(openFileInput(dict), openFileOutput(dict, Context.MODE_PRIVATE));
            starDict = new StarDict(ifoF, idxF, dictF);
        } catch (IOException e) {
            Log.e("字典", "加载", e);
        }
    }

    private String getDictName(String ext) {
        return new CacheFile(this, "foguangdacidian").getRealName() + "." + ext;
    }

    private StarDict starDict;
}
