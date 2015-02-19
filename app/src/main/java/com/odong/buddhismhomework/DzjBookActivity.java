package com.odong.buddhismhomework;

import android.app.Activity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.widget.TextView;

import com.google.gson.Gson;
import com.odong.buddhismhomework.models.CacheFile;
import com.odong.buddhismhomework.models.Dzj;
import com.odong.buddhismhomework.models.Point;
import com.odong.buddhismhomework.utils.DwDbHelper;

/**
 * Created by flamen on 15-2-19.
 */
public class DzjBookActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dzj);
        getActionBar().setIcon(R.drawable.ic_dzj);

        book = new Gson().fromJson(getIntent().getStringExtra("book"), Dzj.class);
        setTitle(book.getTitle());

        ((TextView) findViewById(R.id.tv_dzj_content)).setMovementMethod(new ScrollingMovementMethod());
        initTextView();
    }

    @Override
    public void onBackPressed() {
        TextView tv = (TextView) findViewById(R.id.tv_dzj_content);
        Point p = new Point();
        p.setX(tv.getScrollX());
        p.setY(tv.getScrollY());
        new DwDbHelper(this).set("scroll://dzj/" + book.getName(), p);
        super.onBackPressed();
    }

    private void initTextView() {
        TextView tv = (TextView) findViewById(R.id.tv_dzj_content);
        String content = new CacheFile(this, book.getName()).read();
        if (content == null) {
            tv.setText(R.string.lbl_empty);
        } else {
            tv.setText(content);
        }

        Point p = new DwDbHelper(this).get("scroll://dzj/" + book.getName(), Point.class);
        if (p == null) {
            p = new Point();
        }
        tv.scrollTo(p.getX(), p.getY());
    }

    private Dzj book;
}
