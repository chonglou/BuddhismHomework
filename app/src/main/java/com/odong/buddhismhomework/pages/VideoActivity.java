package com.odong.buddhismhomework.pages;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.odong.buddhismhomework.R;
import com.odong.buddhismhomework.models.Channel;
import com.odong.buddhismhomework.models.Playlist;
import com.odong.buddhismhomework.models.Video;
import com.odong.buddhismhomework.utils.DbHelper;
import com.odong.buddhismhomework.utils.YoutubePlayer;
import com.odong.buddhismhomework.widgets.ColorListAdapter;

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
                initPlaylist((Channel) getIntent().getSerializableExtra("channel"));
                break;
            case "videos":
                initVideosList((Playlist) getIntent().getSerializableExtra("playlist"));
                break;
        }

    }


    private void initChannelList() {
        setTitle(R.string.title_videos);
        final List<Channel> channels = new DbHelper(this).listChannel();


        List<Map<String, String>> items = new ArrayList<>();

        for (Channel c : channels) {
            Map<String, String> map = new HashMap<>();
            map.put("title", c.getTitle());
            map.put("details", shortTxt(c.getDescription()));
            items.add(map);
        }
        ListAdapter adapter = new ColorListAdapter(this,
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
                intent.putExtra("type", "playlist");
                intent.putExtra("channel", ch);
                startActivity(intent);
            }
        });
    }

    private void initPlaylist(Channel channel) {
        setTitle(channel.getTitle());
        final List<Playlist> playlist = new DbHelper(this).listPlaylist(channel.getCid());


        List<Map<String, String>> items = new ArrayList<>();

        for (Playlist pl : playlist) {
            Map<String, String> map = new HashMap<>();
            map.put("title", pl.getTitle());
            map.put("details", shortTxt(pl.getDescription()));
            items.add(map);
        }
        ListAdapter adapter = new ColorListAdapter(this,
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
                intent.putExtra("type", "videos");
                intent.putExtra("playlist", pl);
                startActivity(intent);
            }
        });
    }

    private void initVideosList(Playlist playlist) {
        setTitle(playlist.getTitle());
        final List<Video> videos = new DbHelper(this).listVideo(playlist.getPid());

        List<Map<String, String>> items = new ArrayList<>();

        for (Video v : videos) {
            Map<String, String> map = new HashMap<>();
            map.put("title", v.getTitle());
            map.put("details", shortTxt(v.getDescription()));
            items.add(map);
        }
        ListAdapter adapter = new ColorListAdapter(this,
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

    private String shortTxt(String text) {
        return text.length() > 128 ? text.substring(0, 120) : text;
    }


}
