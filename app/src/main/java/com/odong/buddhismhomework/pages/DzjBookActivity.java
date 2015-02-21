package com.odong.buddhismhomework.pages;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.gson.Gson;
import com.odong.buddhismhomework.R;
import com.odong.buddhismhomework.models.CacheFile;
import com.odong.buddhismhomework.models.Dzj;
import com.odong.buddhismhomework.models.Point;
import com.odong.buddhismhomework.utils.DwDbHelper;
import com.odong.buddhismhomework.utils.KvHelper;
import com.odong.buddhismhomework.utils.WidgetHelper;

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
        new WidgetHelper(this).initTextViewFont(R.id.tv_dzj_content);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_dzj_book, menu);
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
                        DwDbHelper ddh = new DwDbHelper(DzjBookActivity.this);
                        ddh.setDzjFav(book.getId(), true);
                        ddh.close();
                        new WidgetHelper(DzjBookActivity.this).toast(getString(R.string.lbl_success), false);
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
        TextView tv = (TextView) findViewById(R.id.tv_dzj_content);
        String content = new CacheFile(this, book.getName()).read();
        if (content == null) {
            tv.setText(R.string.lbl_empty);
        } else {
            tv.setText(content);
        }


        Point p = new KvHelper(this).get("scroll://dzj/" + book.getName(), Point.class, new Point());
        tv.scrollTo(p.getX(), p.getY());
    }

    private Dzj book;
}
