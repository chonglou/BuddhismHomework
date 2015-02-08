package com.odong.buddhismhomework;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

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
                setTitle(R.string.title_music);
                getActionBar().setIcon(R.drawable.ic_musics);
                break;
        }


        //getActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private List<Map<String, String>> loadListItems() {

        List<Map<String, String>> items = new ArrayList<Map<String, String>>();

        for (String s : getResources().getStringArray(lvId)) {
            String[] ss = s.split("\\|");
            Map<String, String> map = new HashMap<String, String>();
            map.put("title", ss[1]);
            map.put("details", ss[2]);
            items.add(map);
        }
        return items;
    }

    private void initListView() {

        ListAdapter adapter = new SimpleAdapter(this,
                loadListItems(),
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
                        intent = new Intent(ItemsActivity.this, CourseActivity.class);
                        break;
                    case R.array.lv_musics:
                        intent = new Intent(ItemsActivity.this, MusicActivity.class);
                        break;
                    case R.array.lv_books:
                        intent = new Intent(ItemsActivity.this, BookActivity.class);
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
                                deleteFile("courses", R.array.lv_courses, position, "mp3");
                                deleteFile("courses", R.array.lv_courses, position, "txt");
                                break;
                            case R.array.lv_musics:
                                deleteFile("musics", R.array.lv_musics, position, "mp3");
                                break;
                            case R.array.lv_books:
                                deleteFile("books", R.array.lv_books, position, "txt");
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


    private void deleteFile(String type, int sid, int position, String ext) {
        String name = type+"-" + getResources().getStringArray(sid)[position].split("\\|")[0] + "." + ext;
        deleteFile(name);
        Log.d("删除文件", name);
    }

    private int lvId;

}
