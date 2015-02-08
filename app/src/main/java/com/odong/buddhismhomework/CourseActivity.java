package com.odong.buddhismhomework;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.odong.buddhismhomework.models.Calendar;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by flamen on 15-2-8.
 */
public class CourseActivity extends Activity {
    //todo 梵呗
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_items);
        String name = getIntent().getStringExtra("name");
//
//
//        initDownloadDialog();
//        initHomework();
//
//        List<String> names = new ArrayList<String>();
//        for (String s : getResources().getStringArray(R.array.lv_items_homework)) {
//            String name = s.split("\\|")[0];
//            if (name.equals("morning") || name.equals("night") || name.equals("sitting")) {
//                continue;
//            }
//            names.add(name);
//        }
//        new Downloader().execute(names.toArray(new String[names.size()]));
    }


    private void initHomework() {

        DwDbHelper dh = new DwDbHelper(this);

        List<Map<String, String>> items = new ArrayList<Map<String, String>>();
        homeworkActions = new ArrayList<String>();

        for (String s : getResources().getStringArray(R.array.lv_items_homework)) {
            String[] ss = s.split("\\|");
            if (ss.length == 3) {
                String name = ss[0];
                homeworkActions.add(name);


                Map<String, String> item = new HashMap<String, String>();
                item.put("title", ss[1]);

                String details = ss[2];
                if (name.equals("morning") || name.equals("night")) {
                    Calendar cal = dh.get("homework." + name, Calendar.class);
                    if (cal == null) {
                        cal = new Calendar();
                        cal.setHour(7);
                        cal.setMinute(0);
                        cal.setLength(30);
                        cal.setMon(true);
                        cal.setTues(true);
                        cal.setWed(true);
                        cal.setThur(true);
                        cal.setFri(true);
                        cal.setSat(true);
                        cal.setSun(true);
                        dh.set("homework." + name, cal);
                    }
                    item.put("details", String.format(details,
                            cal.getHour(),
                            cal.getMinute(),
                            cal.getLength(),
                            getResources().getString(R.string.lbl_every_day)));
                } else if (name.equals("sitting")) {
                    Integer begin = dh.get("homework." + name, Integer.class);
                    if (begin == null) {
                        begin = 30;
                        dh.set("homework." + name, begin);
                    }
                    item.put("details", String.format(details, begin));
                } else {
                    item.put(
                            "details",
                            String.format(
                                    details,
                                    getResources().getString(
                                            new File(name + ".mp3").exists() && new File(name + ".txt").exists() ?
                                                    R.string.lbl_already_download :
                                                    R.string.lbl_non_exists)
                            )
                    );
                }

                items.add(item);
            }
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
                String act = homeworkActions.get(position);
                if (act.equals("morning") || act.equals("night")) {

                } else if (act.equals("sitting")) {

                } else {
                    //new Downloader().execute(act);
                }
            }
        });
    }

    private List<String> homeworkActions;
}
