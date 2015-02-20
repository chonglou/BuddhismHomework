package com.odong.buddhismhomework.pages;

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

import com.google.gson.Gson;
import com.odong.buddhismhomework.R;
import com.odong.buddhismhomework.models.Book;
import com.odong.buddhismhomework.models.CacheFile;
import com.odong.buddhismhomework.utils.XmlHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by flamen on 15-2-8.
 */
public class BooksActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_items);

        type = getIntent().getStringExtra("type");
        setTitle(getResources().getIdentifier("title_" + type, "string", getPackageName()));
        getActionBar().setIcon(getResources().getIdentifier("ic_" + type, "drawable", getPackageName()));

        books = new XmlHelper(this).getBookList(type);
        initListView();


    }


    private void initListView() {

        List<Map<String, String>> items = new ArrayList<Map<String, String>>();
        for (Book b : books) {
            Map<String, String> map = new HashMap<String, String>();
            map.put("title", b.getName());
            map.put("details", b.getAuthor());
            items.add(map);
        }
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
                Intent intent = new Intent(BooksActivity.this, PlayerActivity.class);
                Book book = books.get(position);
                intent.putExtra("book", new Gson().toJson(book));
                intent.putExtra("type", type);
                startActivity(intent);
            }
        });
        lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                final Book book = books.get(position);
                if (book.getMp3() != null) {

                    AlertDialog.Builder adb = new AlertDialog.Builder(BooksActivity.this);
                    adb.setMessage(R.string.lbl_remove_item_cache);
                    adb.setTitle(R.string.lbl_are_you_sure);

                    adb.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            new CacheFile(BooksActivity.this, book.getMp3()).remove();


                        }
                    });
                    adb.setNegativeButton(android.R.string.no, null);

                    adb.create().show();
                }
                return true;
            }
        });

    }

    private List<Book> books;
    private String type;


}
