package com.odong.buddhismhomework;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.google.gson.Gson;
import com.odong.buddhismhomework.models.Video;
import com.odong.buddhismhomework.utils.YoutubePlayer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by flamen on 15-2-12.
 */
public class VideoListActivity extends Activity {
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
        for (Map.Entry<String, String> e : video.getItems().entrySet()) {
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
                String vid = video.getItems().keySet().toArray(new String[1])[position];
                new YoutubePlayer(VideoListActivity.this, vid).start();

            }
        });
    }


    private Video video;

}
