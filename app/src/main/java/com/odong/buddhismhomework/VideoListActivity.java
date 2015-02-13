package com.odong.buddhismhomework;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubeStandalonePlayer;
import com.google.gson.Gson;
import com.odong.buddhismhomework.models.Video;

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

                Intent intent = YouTubeStandalonePlayer.createVideoIntent(VideoListActivity.this, Config.GOOGLE_DEVELOPER_KEY, vid, 0, true, true);

                try {
                    if (canResolveIntent(intent)) {
                        startActivityForResult(intent, REQ_START_STANDALONE_PLAYER);
                    } else {
                        YouTubeInitializationResult.SERVICE_MISSING.getErrorDialog(VideoListActivity.this, REQ_RESOLVE_SERVICE_MISSING).show();
                    }
                } catch (ActivityNotFoundException e) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.youtube.com/watch?v=" + vid)));
                }

            }
        });
    }

    private boolean canResolveIntent(Intent intent) {
        List<ResolveInfo> resolveInfo = getPackageManager().queryIntentActivities(intent, 0);
        return resolveInfo != null && !resolveInfo.isEmpty();
    }

    private static final int REQ_START_STANDALONE_PLAYER = 1;
    private static final int REQ_RESOLVE_SERVICE_MISSING = 2;


    private Video video;

}
