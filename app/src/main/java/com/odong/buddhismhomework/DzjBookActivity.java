package com.odong.buddhismhomework;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

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
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_dzj, menu);
        return true;
    }

    @Override
    public void onBackPressed() {
        TextView tv = (TextView) findViewById(R.id.tv_dzj_content);
        Point p = new Point();
        p.setX(tv.getScrollX());
        p.setY(tv.getScrollY());
        DwDbHelper ddh = new DwDbHelper(this);
        ddh.set("scroll://dzj/" + book.getName(), p);
        ddh.close();
        super.onBackPressed();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
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
                        Toast.makeText(DzjBookActivity.this, getString(R.string.lbl_success), Toast.LENGTH_SHORT).show();
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

        DwDbHelper ddh = new DwDbHelper(this);
        Point p = ddh.get("scroll://dzj/" + book.getName(), Point.class);
        ddh.close();
        if (p == null) {
            p = new Point();
        }
        tv.scrollTo(p.getX(), p.getY());
    }

    private Dzj book;
}
