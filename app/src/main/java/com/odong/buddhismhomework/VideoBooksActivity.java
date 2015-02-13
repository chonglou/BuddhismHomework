package com.odong.buddhismhomework;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.google.gson.Gson;
import com.odong.buddhismhomework.models.Video;
import com.odong.buddhismhomework.utils.XmlHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by flamen on 15-2-12.
 */
public class VideoBooksActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_items);
        setTitle(R.string.title_videos);

        videos = new XmlHelper(this).getVideoList();
        initList();
    }

    private void initList() {
        List<Map<String, String>> items = new ArrayList<Map<String, String>>();
        for (Video v : videos) {
            Map<String, String> map = new HashMap<String, String>();
            map.put("title", v.getName());
            map.put("details", v.getAuthor());
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
                Intent intent = new Intent(VideoBooksActivity.this, VideoListActivity.class);
                Video video = videos.get(position);
                intent.putExtra("video", new Gson().toJson(video));
                startActivity(intent);
            }
        });
    }

    private List<Video> videos;
}
