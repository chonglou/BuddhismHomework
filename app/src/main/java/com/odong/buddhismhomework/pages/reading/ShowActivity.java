package com.odong.buddhismhomework.pages.reading;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.gson.Gson;
import com.odong.buddhismhomework.R;
import com.odong.buddhismhomework.back.IndexService;
import com.odong.buddhismhomework.models.CacheFile;
import com.odong.buddhismhomework.models.Dzj;
import com.odong.buddhismhomework.models.Index;
import com.odong.buddhismhomework.models.Point;
import com.odong.buddhismhomework.utils.DwDbHelper;
import com.odong.buddhismhomework.utils.KvHelper;
import com.odong.buddhismhomework.utils.WidgetHelper;

import java.io.FileInputStream;
import java.io.IOException;

/**
 * Created by flamen on 15-2-19.
 */
public class ShowActivity extends Activity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book);
        getActionBar().setIcon(R.drawable.ic_dzj);

        book = new Gson().fromJson(getIntent().getStringExtra("book"), Dzj.class);
        setTitle(book.getTitle());

        initTextView();
        initIndex();

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_book, menu);
        return true;
    }

    @Override
    public void onBackPressed() {
        TextView tv = (TextView) findViewById(R.id.tv_dzj_content);
        Point p = new Point();
        p.setX(tv.getScrollX());
        p.setY(tv.getScrollY());
        new KvHelper(this).set("scroll://dzj/" + book.getName(), p);
        super.onBackPressed();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_zoom_in:
                new WidgetHelper(this).zoomTextView(R.id.tv_dzj_content, false);
                break;
            case R.id.action_zoom_out:
                new WidgetHelper(this).zoomTextView(R.id.tv_dzj_content, true);
                break;
            case R.id.action_add_to_favorites:
                AlertDialog.Builder adb = new AlertDialog.Builder(this);
                adb.setTitle(R.string.action_add_to_favorites);
                adb.setMessage(R.string.lbl_are_you_sure);
                adb.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        DwDbHelper ddh = new DwDbHelper(ShowActivity.this);
                        ddh.setDzjFav(book.getId(), true);
                        ddh.close();
                        new WidgetHelper(ShowActivity.this).toast(getString(R.string.lbl_success), false);
                    }
                });
                adb.setNegativeButton(android.R.string.no, null);
                adb.create().show();
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }

    private void initTextView() {
        KvHelper kh = new KvHelper(this);
        WidgetHelper wh = new WidgetHelper(this);

        Point p = kh.get("scroll://dzj/" + book.getName(), Point.class, new Point());
        TextView tv = (TextView) findViewById(R.id.tv_dzj_content);
        tv.setMovementMethod(new ScrollingMovementMethod());


        try {
            tv.setText(wh.readFile(new FileInputStream(new CacheFile(this, book.getName()).getRealFile()), p.getOffset(), IndexService.LINES));
        } catch (IOException e) {
            tv.setText(R.string.lbl_empty);
        }

        tv.scrollTo(p.getX(), p.getY());
        wh.initTextViewFont(R.id.tv_dzj_content);


    }

    private void initIndex() {
        Index index = new KvHelper(this).get(IndexService.type2name("dzj", book.getName()), Index.class, null);
        if (index == null) {
            Intent intent = new Intent(ShowActivity.this, IndexService.class);
            intent.putExtra("type", "dzj");
            intent.putExtra("file", book.getName());
            startService(intent);
        }
    }

    private Dzj book;
}
