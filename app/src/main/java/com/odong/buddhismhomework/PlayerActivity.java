package com.odong.buddhismhomework;

import android.app.Activity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.widget.TextView;

import com.odong.buddhismhomework.models.CacheFile;

import java.io.IOException;

/**
 * Created by flamen on 15-2-8.
 */
public class PlayerActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);


        loadFiles();

        initTextView();
        if (txtFile != null) {

        } else if (mp3File != null) {
            setTitle(mp3File.getTitle());
        }


    }

    private void loadFiles() {

        int id = getIntent().getIntExtra("id", 0);
        switch (getIntent().getIntExtra("type", 0)) {
            case R.array.lv_courses:
                txtFile = new CacheFile(this, "courses", R.array.lv_courses, id, "txt");
                mp3File = new CacheFile(this, "courses", R.array.lv_courses, id, "mp3");
                break;
            case R.array.lv_books:
                txtFile = new CacheFile(this, "books", R.array.lv_books, id, "txt");
                mp3File = null;
                break;
            case R.array.lv_musics:
                txtFile = null;
                mp3File = new CacheFile(this, "musics", R.array.lv_musics, id, "mp3");
                break;
        }
    }

    private void initTextView() {
        TextView tv = (TextView) findViewById(R.id.tv_player_content);
        tv.setText(R.string.lbl_empty);
        if (txtFile != null) {
            setTitle(txtFile.getTitle());
            try {
                tv.setText(txtFile.read());
            } catch (IOException e) {
                Log.e("读取文件", txtFile.getRealName(), e);
            }
        }

        tv.setMovementMethod(new ScrollingMovementMethod());

    }

    private CacheFile txtFile;
    private CacheFile mp3File;
}
