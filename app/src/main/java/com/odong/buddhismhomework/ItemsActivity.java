package com.odong.buddhismhomework;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.odong.buddhismhomework.models.CacheFile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by flamen on 15-2-8.
 */
public class ItemsActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_items);

        lvId = getIntent().getIntExtra("lvId", R.array.lv_courses);
        setInfo();
        initListView();
    }

    private void setInfo() {
        switch (lvId) {
            case R.array.lv_books:
                setTitle(R.string.title_books);
                getActionBar().setIcon(R.drawable.ic_books);
                break;
            case R.array.lv_courses:
                setTitle(R.string.title_courses);
                getActionBar().setIcon(R.drawable.ic_courses);
                break;
            case R.array.lv_musics:
                setTitle(R.string.title_musics);
                getActionBar().setIcon(R.drawable.ic_musics);
                break;
        }


        //getActionBar().setDisplayHomeAsUpEnabled(true);
    }


    private void initListView() {

        final List<Map<String, String>> items = new ArrayList<Map<String, String>>();
        CacheFile.each(this, lvId, new CacheFile.ItemCallback() {
            @Override
            public void call(String name, String title, String details) {
                Map<String, String> map = new HashMap<String, String>();
                map.put("title", title);
                map.put("details", details);
                items.add(map);
            }
        });

        ListAdapter adapter = new SimpleAdapter(this,
                items,
                android.R.layout.two_line_list_item,
                new String[]{"title", "details"},
                new int[]{android.R.id.text1, android.R.id.text2});

        ListView lv = (ListView) findViewById(R.id.lv_items);
        lv.setAdapter(adapter);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Intent intent = null;
                switch (lvId) {
                    case R.array.lv_courses:
                        intent = new Intent(ItemsActivity.this, PlayerActivity.class);
                        intent.putExtra("type", R.array.lv_courses);
                        break;
                    case R.array.lv_musics:
                        intent = new Intent(ItemsActivity.this, PlayerActivity.class);
                        intent.putExtra("type", R.array.lv_musics);
                        break;
                    case R.array.lv_books:
                        intent = new Intent(ItemsActivity.this, PlayerActivity.class);
                        intent.putExtra("type", R.array.lv_books);
                        break;
                }

                if (intent != null) {
                    intent.putExtra("id", position);
                    startActivity(intent);
                }
            }
        });

        lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                AlertDialog.Builder adb = new AlertDialog.Builder(ItemsActivity.this);
                adb.setMessage(R.string.lbl_remove_item);
                adb.setTitle(R.string.lbl_are_you_sure);

                adb.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        switch (lvId) {
                            case R.array.lv_courses:
                                new CacheFile(ItemsActivity.this, "courses", R.array.lv_courses, position, "mp3").remove();
                                new CacheFile(ItemsActivity.this, "courses", R.array.lv_courses, position, "txt").remove();
                                break;
                            case R.array.lv_musics:
                                new CacheFile(ItemsActivity.this, "musics", R.array.lv_musics, position, "mp3").remove();
                                break;
                            case R.array.lv_books:
                                new CacheFile(ItemsActivity.this, "books", R.array.lv_books, position, "txt").remove();
                                break;
                        }


                    }
                });
                adb.setNegativeButton(android.R.string.no, null);

                adb.create().show();
                return true;
            }
        });
    }


    private int lvId;

}
