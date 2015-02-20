package com.odong.buddhismhomework;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.google.gson.Gson;
import com.odong.buddhismhomework.models.Dzj;
import com.odong.buddhismhomework.utils.DwDbHelper;

import java.util.ArrayList;
import java.util.HashMap;
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
        final List<Map<String, String>> items = new ArrayList<Map<String, String>>();
        DwDbHelper ddh = new DwDbHelper(this);
        final List<Dzj> books = ddh.getFavDzjList();
        ddh.close();
        for (Dzj d : books) {
            Map<String, String> map = new HashMap<String, String>();
            map.put("title", d.getTitle());
            map.put("details", d.getAuthor());
            items.add(map);
        }
        final SimpleAdapter adapter = new SimpleAdapter(this,
                items,
                android.R.layout.two_line_list_item,
                new String[]{"title", "details"},
                new int[]{android.R.id.text1, android.R.id.text2});


        ListView lv = (ListView) findViewById(R.id.lv_items);
        lv.setAdapter(adapter);


        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(FavoritesActivity.this, DzjBookActivity.class);
                intent.putExtra("book", new Gson().toJson(books.get(position)));
                startActivity(intent);
            }
        });
        lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
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
