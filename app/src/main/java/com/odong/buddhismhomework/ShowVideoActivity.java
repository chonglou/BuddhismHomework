package com.odong.buddhismhomework;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.google.gson.Gson;
import com.odong.buddhismhomework.models.Video;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by flamen on 15-2-12.
 */
public class ShowVideoActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_items);

        video = new Gson().fromJson(getIntent().getStringExtra("video"), Video.class);
        setTitle(video.getName());
        initList();
    }

    private void initList() {
        List<Map<String, String>> items = new ArrayList<Map<String, String>>();
        for ( Map.Entry<String,String> e : video.getItems().entrySet()) {
            Map<String, String> map = new HashMap<String, String>();
            map.put("title", e.getValue());
            map.put("details", e.getKey());
            items.add(map);
        }
        ListAdapter adapter = new SimpleAdapter(this,
                items,
                android.R.layout.simple_list_item_1,
                new String[]{"title", "details"},
                new int[]{android.R.id.text1, android.R.id.text2});

        ListView lv = (ListView) findViewById(R.id.lv_items);
        lv.setAdapter(adapter);


        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                LinkedHashMap<String,String>aaa=new LinkedHashMap<String, String>();
                String link = video.getItems().keySet().toArray(new String[1])[position];
                Log.d("VIDEO", link);
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(link)));
            }
        });
    }
    private Video video;
}
