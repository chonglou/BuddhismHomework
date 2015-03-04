package com.odong.buddhismhomework.pages;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.google.gson.Gson;
import com.odong.buddhismhomework.Config;
import com.odong.buddhismhomework.R;
import com.odong.buddhismhomework.models.Video;
import com.odong.buddhismhomework.utils.HttpClient;
import com.odong.buddhismhomework.utils.XmlHelper;
import com.odong.buddhismhomework.utils.YoutubePlayer;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by flamen on 15-2-12.
 */
public class VideoActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_items);

        new AsyncTask<String, Void, Void>(){

            @Override
            protected Void doInBackground(String... params) {
                String url = "https://www.googleapis.com/youtube/v3/channels?part=snippet&forUsername=DDMTV01&userIp=192.168.1.10&key=" + Config.GOOGLE_DEVELOPER_ANDROID_KEY;
                Map<String, String> map = new HashMap<>();
                try {

                    String pl = HttpClient.getS(url);
                    Log.d("播放列表", pl);
                } catch (IOException |URISyntaxException e) {
                    Log.d("Youtube", "播放列表", e);
                }
                return null;
            }
        }.execute();



        Video video = new Gson().fromJson(getIntent().getStringExtra("video"), Video.class);
        if (video == null) {
            initList();
        } else {
            initList(video);
        }


    }

    private void initList(final Video video) {
        setTitle(video.getName());

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
                new YoutubePlayer(VideoActivity.this, vid).start();

            }
        });
    }

    private void initList() {
        setTitle(R.string.title_videos);

        List<Map<String, String>> items = new ArrayList<Map<String, String>>();
        final List<Video> videos = new XmlHelper(this).getVideoList();

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
                Intent intent = new Intent(VideoActivity.this, VideoActivity.class);
                Video video = videos.get(position);
                intent.putExtra("video", new Gson().toJson(video));
                startActivity(intent);
            }
        });
    }


}
