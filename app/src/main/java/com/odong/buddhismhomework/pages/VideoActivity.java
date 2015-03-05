package com.odong.buddhismhomework.pages;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.google.gson.Gson;
import com.odong.buddhismhomework.R;
import com.odong.buddhismhomework.models.Channel;
import com.odong.buddhismhomework.models.Playlist;
import com.odong.buddhismhomework.models.Video;
import com.odong.buddhismhomework.utils.DwDbHelper;
import com.odong.buddhismhomework.utils.YoutubePlayer;

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


        switch (getIntent().getStringExtra("type")) {
            case "channel":
                initChannelList();
                break;
            case "playlist":
                initPlaylist(new Gson().fromJson(getIntent().getStringExtra("channel"), Channel.class));
                break;
            case "videos":
                initVideoList(new Gson().fromJson(getIntent().getStringExtra("playlist"), Playlist.class));
                break;
        }

    }

    private void initChannelList() {
        setTitle(R.string.title_videos);
        final List<Channel> channels = new DwDbHelper(this).listChannel();


        List<Map<String, String>> items = new ArrayList<>();

        for (Channel c : channels) {
            Map<String, String> map = new HashMap<>();
            map.put("title", c.getTitle());
            map.put("details", c.getDescription());
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
                Channel ch = channels.get(position);
                intent.putExtra("type", "channel");
                intent.putExtra("channel", new Gson().toJson(ch));
                startActivity(intent);
            }
        });
    }

    private void initPlaylist(Channel channel) {
        setTitle(channel.getTitle());
        final List<Playlist> playlist = new DwDbHelper(this).listPlaylist(channel.getCid());


        List<Map<String, String>> items = new ArrayList<>();

        for (Playlist pl : playlist) {
            Map<String, String> map = new HashMap<>();
            map.put("title", pl.getTitle());
            map.put("details", pl.getDescription());
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
                Playlist pl = playlist.get(position);
                intent.putExtra("type", "playlist");
                intent.putExtra("playlist", new Gson().toJson(pl));
                startActivity(intent);
            }
        });
    }

    private void initVideoList(Playlist playlist) {
        setTitle(playlist.getTitle());
        final List<Video> videos = new DwDbHelper(this).listVideo(playlist.getPid());

        List<Map<String, String>> items = new ArrayList<>();

        for (Video v : videos) {
            Map<String, String> map = new HashMap<>();
            map.put("title", v.getTitle());
            map.put("details", v.getDescription());
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
                Video v = videos.get(position);
                new YoutubePlayer(VideoActivity.this, v.getVid()).start();
            }
        });
    }


}
