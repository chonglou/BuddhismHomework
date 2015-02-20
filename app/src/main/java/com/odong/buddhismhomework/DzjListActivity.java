package com.odong.buddhismhomework;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
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
import android.widget.Toast;

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
            case R.id.action_add_to_favorites:
                AlertDialog.Builder adb = new AlertDialog.Builder(this);
                adb.setTitle(R.string.action_add_to_favorites);
                adb.setMessage(R.string.lbl_are_you_sure);
                adb.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(DzjListActivity.this, SearchActivity.class);
                        intent.putExtra("type", "dzj");
                        intent.putExtra("keyword", );
                        startActivity(intent);
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
        List<Map<String, String>> items = new ArrayList<Map<String, String>>();
        DwDbHelper ddh = new DwDbHelper(this);
        final List<Dzj> books = ddh.getDzjList(type);
        ddh.close();
        for (Dzj d : books) {
            Map<String, String> map = new HashMap<String, String>();
            map.put("title", d.getTitle());
            map.put("details", d.getAuthor());
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
                Intent intent = new Intent(DzjListActivity.this, DzjBookActivity.class);
                intent.putExtra("book", new Gson().toJson(books.get(position)));
                startActivity(intent);
            }
        });
    }

}
