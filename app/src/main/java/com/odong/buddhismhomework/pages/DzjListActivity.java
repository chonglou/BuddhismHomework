package com.odong.buddhismhomework.pages;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.odong.buddhismhomework.R;
import com.odong.buddhismhomework.models.Dzj;
import com.odong.buddhismhomework.utils.DwDbHelper;
import com.odong.buddhismhomework.utils.WidgetHelper;

import java.util.List;

/**
 * Created by flamen on 15-2-19.
 */
public class DzjListActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_items);

        getActionBar().setIcon(R.drawable.ic_dzj);

        String type = getIntent().getStringExtra("type");
        if (type == null) {
            initTypesView();
            setTitle(R.string.title_dzj);
        } else {
            initBooksView(type);
            setTitle(type);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_dzj_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_search:
                new WidgetHelper(DzjListActivity.this).showSearchDialog();
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }

    private void initTypesView() {
        DwDbHelper ddh = new DwDbHelper(this);
        final List<String> types = ddh.getDzjTypeList();
        ddh.close();
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1,
                android.R.id.text1,
                types);

        ListView lv = (ListView) findViewById(R.id.lv_items);
        lv.setAdapter(adapter);


        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(DzjListActivity.this, DzjListActivity.class);
                intent.putExtra("type", types.get(position));
                startActivity(intent);
            }
        });
    }

    private void initBooksView(String type) {

        DwDbHelper ddh = new DwDbHelper(this);
        final List<Dzj> books = ddh.getDzjList(type);
        ddh.close();
        new WidgetHelper(this).initDzjBookList(books, null);
    }

}
