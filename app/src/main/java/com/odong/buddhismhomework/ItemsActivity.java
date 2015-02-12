package com.odong.buddhismhomework;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.odong.buddhismhomework.models.Book;
import com.odong.buddhismhomework.utils.XmlHelper;

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

        String type = getIntent().getStringExtra("type");
        setTitle(getResources().getIdentifier("title_" + type,"string",  getPackageName()));
        getActionBar().setIcon(getResources().getIdentifier("ic_" + type, "drawable",  getPackageName()));

        books = new XmlHelper(this).getBookList(type);
        initListView(type);


    }


    private void initListView(final String type) {

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
                Intent intent = new Intent(ItemsActivity.this, PlayerActivity.class);
                intent.putExtra("type", type);
                intent.putExtra("files", books.get(position));
                startActivity(intent);
            }
        });


    }

    private List<Book> books;


}
