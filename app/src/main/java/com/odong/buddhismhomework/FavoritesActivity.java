package com.odong.buddhismhomework;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.SimpleAdapter;

import com.odong.buddhismhomework.models.Dzj;
import com.odong.buddhismhomework.utils.DwDbHelper;
import com.odong.buddhismhomework.utils.WidgetHelper;

import java.util.List;
import java.util.Map;

/**
 * Created by flamen on 15-2-19.
 */
public class FavoritesActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_items);
        getActionBar().setIcon(R.drawable.ic_dzj);
        setTitle(R.string.action_favorites);

        initList();
    }

    private void initList() {

        DwDbHelper ddh = new DwDbHelper(this);
        final List<Dzj> books = ddh.getFavDzjList();
        ddh.close();

        new WidgetHelper(this).initDzjBookList(books, new WidgetHelper.BookListCallback() {
            @Override
            public boolean run(final SimpleAdapter adapter, final int position, final List<Map<String, String>> items) {
                AlertDialog.Builder adb = new AlertDialog.Builder(FavoritesActivity.this);
                adb.setMessage(R.string.lbl_remove_favorites_item);
                adb.setTitle(R.string.lbl_are_you_sure);

                adb.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        DwDbHelper ddh = new DwDbHelper(FavoritesActivity.this);
                        ddh.setDzjFav(books.get(position).getId(), false);
                        ddh.close();
                        books.remove(position);
                        items.remove(position);
                        adapter.notifyDataSetChanged();
                    }
                });
                adb.setNegativeButton(android.R.string.no, null);

                adb.create().show();

                return true;
            }
        });


    }
}
