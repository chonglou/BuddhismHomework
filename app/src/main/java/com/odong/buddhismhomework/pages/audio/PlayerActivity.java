package com.odong.buddhismhomework.pages.audio;

import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.google.gson.Gson;
import com.odong.buddhismhomework.R;
import com.odong.buddhismhomework.models.Book;
import com.odong.buddhismhomework.models.Pager;
import com.odong.buddhismhomework.services.MusicService;
import com.odong.buddhismhomework.utils.KvHelper;
import com.odong.buddhismhomework.utils.WidgetHelper;

import java.io.IOException;

/**
 * Created by flamen on 15-2-8.
 */
public class PlayerActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);


        String type = getIntent().getStringExtra("type");

        getActionBar().setIcon(getResources().getIdentifier("ic_" + type, "drawable", getPackageName()));

        book = new Gson().fromJson(getIntent().getStringExtra("book"), Book.class);
        setTitle(book.getName());
        receiver = new MusicIntentReceiver();


        ((TextView) findViewById(R.id.tv_player_content)).setMovementMethod(new ScrollingMovementMethod());

        initTextView();
        initMp3View();


    }

    @Override
    protected void onResume() {
        IntentFilter filter = new IntentFilter(Intent.ACTION_HEADSET_PLUG);
        filter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED);
        registerReceiver(receiver, filter);

        super.onResume();
    }

    @Override
    protected void onPause() {
        unregisterReceiver(receiver);
        super.onPause();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (musicIntent == null) {
            musicIntent = new Intent(this, MusicService.class);
            bindService(musicIntent, musicConnection, Context.BIND_AUTO_CREATE);
            startService(musicIntent);
        }
    }

    @Override
    public void onBackPressed() {
        if (book.getMp3() == null) {
            TextView tv = (TextView) findViewById(R.id.tv_player_content);
            Pager p = new Pager();
            p.setX(tv.getScrollX());
            p.setY(tv.getScrollY());

            new KvHelper(this).set("scroll://book/" + book.getName(), p);
        }

        if (musicService.isPlaying()) {
            AlertDialog.Builder adb = new AlertDialog.Builder(this);
            adb.setMessage(R.string.dlg_will_pause);
            adb.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    PlayerActivity.this.finish();
                }
            });
            adb.setNegativeButton(android.R.string.no, null);
            adb.setCancelable(false);
            adb.create().show();
            return;
        }
        super.onBackPressed();


    }

    @Override
    protected void onDestroy() {
        unbindService(musicConnection);
        stopService(musicIntent);
        musicService = null;
        super.onDestroy();
    }

    private void initMp3View() {

        findViewById(R.id.btn_player).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ToggleButton tb = (ToggleButton) v;
                if (tb.isChecked()) {
                    if (isPaused) {
                        musicService.play();
                    } else {
                        musicService.play(book.getMp3());

                        final SeekBar seeker = (SeekBar) findViewById(R.id.sb_player);
                        seeker.setProgress(0);
                        seeker.setClickable(false);

                        findViewById(R.id.tv_player_content).scrollTo(0, 0);
                        durationHandler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                if (musicService != null) {
                                    SeekBar seeker = ((SeekBar) findViewById(R.id.sb_player));
                                    seeker.setProgress(musicService.getCurrentPosition());
                                    seeker.setMax(musicService.getDuration());
                                    ((ToggleButton) findViewById(R.id.btn_player)).setChecked(musicService.isPlaying());
                                    durationHandler.postDelayed(this, 100);
                                }
                            }
                        }, 100);
                    }
                } else {
                    musicService.pause();
                    isPaused = true;
                }
            }
        });


    }


    private void initTextView() {
        TextView tv = (TextView) findViewById(R.id.tv_player_content);
        if (book.getFiles().isEmpty()) {
            tv.setText(R.string.lbl_empty);
            return;
        }

        try {
            tv.setText(new WidgetHelper(this).readFile(book.getFiles().toArray(new Integer[1])));

        } catch (IOException e) {
            Log.e("读取文件", book.getName(), e);
            tv.setText(R.string.lbl_error_io);
        }


        Pager p = new KvHelper(this).get("scroll://book/" + book.getName(), Pager.class, new Pager());
        tv.scrollTo(p.getX(), p.getY());
    }


    private Book book;
    private Handler durationHandler = new Handler();

    private ServiceConnection musicConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MusicService.MusicBinder binder = (MusicService.MusicBinder) service;
            musicService = binder.getService();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            //
        }
    };


    private MusicService musicService;
    private Intent musicIntent;
    private boolean isPaused = false;
    private MusicIntentReceiver receiver;

    public class MusicIntentReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            boolean pause = false;
            if (!new KvHelper(context).get("mp3.earphone", Boolean.class, false)) {
                if (intent.getAction().equals(Intent.ACTION_HEADSET_PLUG)) {
                    int state = intent.getIntExtra("state", -1);
                    switch (state) {
                        case 0:
                            Log.d("Player", "耳机拔出");
                            pause = true;
                            break;
                        case 1:
                            Log.d("Player", "耳机接入");
                            break;
                        default:
                            Log.d("Player", "I have no idea what the headset state is");
                    }
                } else if (intent.getAction().equals(BluetoothDevice.ACTION_ACL_DISCONNECTED)) {
                    Log.d("Player", "Bluetooth low level device disconnect requested");
                    BluetoothDevice btDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    if (btDevice.getBluetoothClass().getMajorDeviceClass() == BluetoothClass.Device.Major.AUDIO_VIDEO) {
                        Log.d("Player", "拔出蓝牙耳机");
                        pause = true;
                    }
                }
            }
            if (pause) {
                if (musicService != null && musicService.isPlaying()) {
                    musicService.pause();
                    isPaused = true;
                }
            }

        }

    }


}
