package com.odong.buddhismhomework;

import android.app.Activity;
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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
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
        getActionBar().setIcon(R.drawable.ic_dict);

        initDict();

        findViewById(R.id.btn_dict_search).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String key = ((EditText) findViewById(R.id.et_dict_key)).getText().toString();

                TextView tv = ((TextView) findViewById(R.id.tv_dict_content));
                try {
                    if (starDict == null) {
                        tv.setText(R.string.lbl_empty);
                    } else {

                        String result = starDict.lookupWord(key);
                        if (result == null) {
                            result = getString(R.string.lbl_empty_results);
                        }
                        tv.setText(result);
                    }


                } catch (IOException e) {
                    Log.e("字典", "查询", e);
                    tv.setText(R.string.lbl_empty);
                }


            }
        });
    }


    private void initDict() {
        try {


            File ifo = getDictName("ifo");
            IfoFile ifoF = new IfoFile(
                    new FileInputStream(ifo),
                    new FileOutputStream(ifo)
            );
            File idx = getDictName("idx");
            IdxFile idxF = new IdxFile(
                    new FileInputStream(idx),
                    new FileOutputStream(idx),
                    ifoF.getWordCount(),
                    ifoF.getIdxFileSize());
            File dict = getDictName("dict");
            DictFile dictF = new DictFile(
                    new FileInputStream(dict),
                    new FileOutputStream(dict)
            );
            starDict = new StarDict(ifoF, idxF, dictF);
        } catch (IOException e) {
            Log.e("字典", "加载", e);
        }
    }

    private File getDictName(String ext) throws IOException {
        return new CacheFile(this, "foguangdacidian." + ext).getRealFile();
    }

    private StarDict starDict;
}
