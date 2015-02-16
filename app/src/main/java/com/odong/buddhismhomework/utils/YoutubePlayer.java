package com.odong.buddhismhomework.utils;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.net.Uri;

import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubeStandalonePlayer;
import com.odong.buddhismhomework.Config;

import java.util.List;

/**
 * Created by flamen on 15-2-16.
 */
public class YoutubePlayer {
    public YoutubePlayer(Activity context, String vid) {
        this.context = context;
        this.vid = vid;
    }

    public void start() {
        Intent intent = YouTubeStandalonePlayer.createVideoIntent(context, Config.GOOGLE_DEVELOPER_KEY, vid, 0, true, true);

        try {
            if (canResolveIntent(intent)) {
                context.startActivityForResult(intent, REQ_START_STANDALONE_PLAYER);
            } else {
                YouTubeInitializationResult.SERVICE_MISSING.getErrorDialog(context, REQ_RESOLVE_SERVICE_MISSING).show();
            }
        } catch (ActivityNotFoundException e) {
            context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.youtube.com/watch?v=" + vid)));
        }
    }

    private boolean canResolveIntent(Intent intent) {
        List<ResolveInfo> resolveInfo = context.getPackageManager().queryIntentActivities(intent, 0);
        return resolveInfo != null && !resolveInfo.isEmpty();
    }


    private static final int REQ_START_STANDALONE_PLAYER = 1;
    private static final int REQ_RESOLVE_SERVICE_MISSING = 2;
    private Activity context;
    private String vid;
}
