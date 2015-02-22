package com.odong.buddhismhomework.pages.reading;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.gson.Gson;
import com.odong.buddhismhomework.R;
import com.odong.buddhismhomework.models.Book;
import com.odong.buddhismhomework.models.CacheFile;
import com.odong.buddhismhomework.models.Dzj;
import com.odong.buddhismhomework.models.Point;
import com.odong.buddhismhomework.utils.DwDbHelper;
import com.odong.buddhismhomework.utils.KvHelper;
import com.odong.buddhismhomework.utils.WidgetHelper;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;

/**
 * Created by flamen on 15-2-19.
 */
public class ShowActivity extends Activity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book);
        ((TextView) findViewById(R.id.tv_book_content)).setMovementMethod(new ScrollingMovementMethod());


        type = getIntent().getStringExtra("type");
        String file = getIntent().getStringExtra("file");


        KvHelper kh = new KvHelper(this);
        WidgetHelper wh = new WidgetHelper(this);
        wh.initTextViewFont(R.id.tv_book_content);

        try {
            if ("book".equals(type)) {
                book = new Gson().fromJson(file, Book.class);
                getActionBar().setIcon(R.drawable.ic_books);
                setTitle(((Book) book).getName());
                point = kh.get(((Book) book).getScrollId(), Point.class, new Point());


            } else if ("dzj".equals(type)) {
                getActionBar().setIcon(R.drawable.ic_dzj);
                book = new Gson().fromJson(file, Dzj.class);
                setTitle(((Dzj) book).getTitle());
                point = kh.get(((Dzj) book).getScrollId(), Point.class, new Point());
            }

            read(point.getPage());
        } catch (IOException e) {
            ((TextView) findViewById(R.id.tv_book_content)).setText(R.string.lbl_empty);
        }


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_book, menu);
        if ("book".equals(type)) {
            menu.findItem(R.id.action_add_to_favorites).setVisible(false);
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        TextView tv = (TextView) findViewById(R.id.tv_book_content);
        Point p = new Point();
        p.setX(tv.getScrollX());
        p.setY(tv.getScrollY());

        KvHelper kv = new KvHelper(this);

        if (type.equals("book")) {
            kv.set(((Book) book).getScrollId(), p);
        } else if (type.equals("dzj")) {
            kv.set(((Dzj) book).getScrollId(), p);
        }
        super.onBackPressed();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_page_next:
                page(true);
                break;
            case R.id.action_page_previous:
                page(false);
                break;
            case R.id.action_zoom_in:
                new WidgetHelper(this).zoomTextView(R.id.tv_book_content, false);
                break;
            case R.id.action_zoom_out:
                new WidgetHelper(this).zoomTextView(R.id.tv_book_content, true);
                break;
            case R.id.action_add_to_favorites:
                AlertDialog.Builder adb = new AlertDialog.Builder(this);
                adb.setTitle(R.string.action_add_to_favorites);
                adb.setMessage(R.string.lbl_are_you_sure);
                adb.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        DwDbHelper ddh = new DwDbHelper(ShowActivity.this);
                        ddh.setDzjFav(((Dzj) book).getId(), true);
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

    private void page(boolean next) {
        WidgetHelper wh = new WidgetHelper(this);
        int p;
        if (next) {
            if (point.isLast()) {
                wh.toast(getString(R.string.lbl_error_last_page), false);
                return;
            }
            p = point.getPage() + 1;
        } else {
            if (point.getPage() == 0) {
                wh.toast(getString(R.string.lbl_error_first_page), false);
                return;
            }
            p = point.getPage() - 1;
        }
        try {
            read(p);
            point.setX(0);
            point.setY(0);
            point.setPage(p);
            Log.d("翻页", "" + point.getPage());
        } catch (IOException e) {
            Log.e("翻页", "previous", e);
        }
    }

    private void read(int page) throws IOException {
        final int LINES = 120;

        TextView tv = (TextView) findViewById(R.id.tv_book_content);

        StringBuilder sb = new StringBuilder();

        LineNumberReader lnr;
        switch (type) {
            case "book":
                lnr = new LineNumberReader(new InputStreamReader(getResources().openRawResource(((Book) book).getFiles().get(0))));
                break;
            case "dzj":
                lnr = new LineNumberReader(new InputStreamReader(new FileInputStream(new CacheFile(this, ((Dzj) book).getName()).getRealFile())));
                break;
            default:
                throw new IllegalArgumentException();
        }


        lnr.setLineNumber(page * LINES + 1);
        for (int i = 0, begin = page * LINES, end = (page + 1) * LINES; ; i++) {
            String line = lnr.readLine();
            if (i < begin) {
                continue;
            }
            if (i > end) {
                break;
            }

            if (line == null) {
                point.setLast(true);
                break;
            }
            sb.append(line);
            sb.append("\n");

        }
        lnr.close();

        tv.setText(sb.toString());
        tv.scrollTo(point.getX(), point.getY());
        point.setPage(page);
    }


    private String type;
    private Object book;
    private Point point;


}
