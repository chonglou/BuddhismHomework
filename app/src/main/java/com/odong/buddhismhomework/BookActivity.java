package com.odong.buddhismhomework;

import android.app.Activity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.widget.TextView;

import com.odong.buddhismhomework.models.CacheFile;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * Created by flamen on 15-2-8.
 */
public class BookActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book);


        cFile = new CacheFile(this, "books", R.array.lv_books, getIntent().getIntExtra("id", 0), "txt");

        setTitle(cFile.getTitle());
        initTextView();
    }

    private void initTextView() {
        TextView tv = (TextView) findViewById(R.id.tv_book_content);
        try {
            tv.setText(cFile.read());
        } catch (FileNotFoundException e) {
            tv.setText(R.string.lbl_error_not_exists);
        } catch (IOException e) {
            tv.setText(R.string.lbl_error_io);
        }
        tv.setMovementMethod(new ScrollingMovementMethod());


    }

    private CacheFile cFile;
}
