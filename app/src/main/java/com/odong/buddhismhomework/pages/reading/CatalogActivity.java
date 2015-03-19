package com.odong.buddhismhomework.pages.reading;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.odong.buddhismhomework.R;
import com.odong.buddhismhomework.models.Book;
import com.odong.buddhismhomework.utils.DwDbHelper;
import com.odong.buddhismhomework.utils.WidgetHelper;

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

        if ("fav".equals(type)) {
            getActionBar().setIcon(R.drawable.ic_books);
            setTitle(R.string.title_books);
            initList(new DwDbHelper(this).getFavBookList());
        } else if ("dzj".equals(type)) {
            setTitle(R.string.title_dzj);
            getActionBar().setIcon(R.drawable.ic_dzj);
            initList(new DwDbHelper(this).getBookList());
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


    private void initList(final List<Book> books) {
        List<Map<String, String>> items = new ArrayList<>();
        for (Book b : books) {
            Map<String, String> map = new HashMap<String, String>();
            map.put("title", b.getTitle());
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
                new WidgetHelper(CatalogActivity.this).showBook(books.get(position));
            }
        });
    }


    private String type;

}
