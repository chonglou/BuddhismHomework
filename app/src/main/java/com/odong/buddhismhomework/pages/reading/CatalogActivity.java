package com.odong.buddhismhomework.pages.reading;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.google.gson.Gson;
import com.odong.buddhismhomework.R;
import com.odong.buddhismhomework.models.Book;
import com.odong.buddhismhomework.models.Dzj;
import com.odong.buddhismhomework.utils.DwDbHelper;
import com.odong.buddhismhomework.utils.WidgetHelper;
import com.odong.buddhismhomework.utils.XmlHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by flamen on 15-2-19.
 */
public class CatalogActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_items);

        type = getIntent().getStringExtra("type");

        if ("book".equals(type)) {
            getActionBar().setIcon(R.drawable.ic_books);
            initForBook();
        } else if ("dzj".equals(type)) {
            getActionBar().setIcon(R.drawable.ic_dzj);
            String chapter = getIntent().getStringExtra("chapter");

            if (chapter == null) {
                initForDzj();
            } else {
                initForDzjChapter(chapter);
            }

        }


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_catalog, menu);
        if ("book".equals(type)) {
            menu.findItem(R.id.action_search).setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_search:
                new WidgetHelper(CatalogActivity.this).showSearchDialog();
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }

    private void initForBook() {
        final List<Book> books = new XmlHelper(this).getBookList("books");

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
                Intent intent = new Intent(CatalogActivity.this, ShowActivity.class);
                Book book = books.get(position);
                intent.putExtra("file", new Gson().toJson(book));
                intent.putExtra("type", "book");
                startActivity(intent);
            }
        });


    }

    private void initForDzj() {
        setTitle(R.string.title_dzj);

        DwDbHelper ddh = new DwDbHelper(this);
        final List<String> chapters = ddh.getDzjTypeList();
        ddh.close();
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1,
                android.R.id.text1,
                chapters);

        ListView lv = (ListView) findViewById(R.id.lv_items);
        lv.setAdapter(adapter);


        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(CatalogActivity.this, CatalogActivity.class);
                intent.putExtra("chapter", chapters.get(position));
                intent.putExtra("type", "dzj");
                startActivity(intent);
            }
        });
    }

    private void initForDzjChapter(String chapter) {
        setTitle(chapter);

        DwDbHelper ddh = new DwDbHelper(this);
        final List<Dzj> books = ddh.getDzjList(chapter);
        ddh.close();
        new WidgetHelper(this).initDzjBookList(books, null);
    }

    private String type;

}
