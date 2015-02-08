package com.odong.buddhismhomework;

import android.app.Activity;
import android.app.ListActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.odong.buddhismhomework.models.Calendar;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initHomework();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void initHomework(){

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

        ListView lv = (ListView)findViewById(R.id.lv_homework);
        lv.setAdapter(adapter);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //todo
                setTitle("Click"+homeworkActions.get(position));
            }
        });
    }
    private List<String> homeworkActions;
}
