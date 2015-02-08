package com.odong.buddhismhomework;

import android.app.Activity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.widget.TextView;

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

        ss = getResources().getStringArray(R.array.lv_books)[getIntent().getIntExtra("id", 0)].split("\\|");


        setTitle(ss[1]);
        initTextView();
    }

    private void initTextView() {
        TextView tv = (TextView) findViewById(R.id.tv_book_content);
        try {
            FileInputStream fis = openFileInput("books-" + ss[0] + ".txt");
            StringBuilder sb = new StringBuilder();
            byte[] buf = new byte[1024];
            int n;
            while ((n = fis.read(buf)) > 0) {
                sb.append(new String(buf, 0, n));
            }
            tv.setText(sb.toString());
        } catch (FileNotFoundException e) {
            tv.setText(R.string.lbl_error_not_exists);
        } catch (IOException e) {
            tv.setText(R.string.lbl_error_io);
        }
        tv.setMovementMethod(new ScrollingMovementMethod());


    }

    private String[] ss;
}
