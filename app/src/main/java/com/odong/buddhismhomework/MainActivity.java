package com.odong.buddhismhomework;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
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


public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        initDownloadDialog();
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
        switch (id) {
            case R.id.action_download:
                List<String> names = new ArrayList<String>();
                for (String s : getResources().getStringArray(R.array.lv_items_homework)) {
                    String name = s.split("\\|")[0];
                    if (name.equals("morning") || name.equals("night") || name.equals("sitting")) {
                        continue;
                    }
                    names.add(name);
                }
                new Downloader().execute(names.toArray(new String[names.size()]));
                break;
            case R.id.action_settings:
                break;
            case R.id.action_about_me:
                AlertDialog.Builder adbAboutMe = new AlertDialog.Builder(this);
                adbAboutMe.setMessage(R.string.lbl_about_me).setTitle(R.string.action_about_me);
                adbAboutMe.setPositiveButton(android.R.string.ok, null);
                adbAboutMe.create().show();
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;


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

        ListView lv = (ListView) findViewById(R.id.lv_homework);
        lv.setAdapter(adapter);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String act = homeworkActions.get(position);
                if (act.equals("morning") || act.equals("night")) {

                } else if (act.equals("sitting")) {

                } else {
                    new Downloader().execute(act);
                }
            }
        });
    }

    private void initDownloadDialog() {
        dlgDownload = new ProgressDialog(this);
        dlgDownload.setTitle(R.string.action_download);
        dlgDownload.setMessage(getString(R.string.lbl_download));
        dlgDownload.setCancelable(true);
        dlgDownload.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);

    }


    private class Downloader extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dlgDownload.setProgress(0);
            dlgDownload.show();
        }

        @Override
        protected String doInBackground(String... names) {
            dlgDownload.setMax(names.length*2);
            String[] exts = new String[]{"mp3", "txt"};
            for (String name : names) {
                for (String ext : exts) {
                    File f = new File(name + "." + ext);
                    if (!f.exists()) {
                        try {
                            DataInputStream dis = new DataInputStream(new URL("https://raw.githubusercontent.com/chonglou/BuddhismHomework/master/tools/" + name).openStream());

                            byte[] buf = new byte[1024];
                            int len;

                            FileOutputStream fos = new FileOutputStream(f);
                            while ((len = dis.read(buf)) > 0) {
                                fos.write(buf, 0, len);
                            }


                        } catch (MalformedURLException e) {
                            Log.e("下载", "地址错误", e);
                        } catch (IOException e) {
                            Log.e("下载", "IO错误", e);
                        } catch (SecurityException e) {
                            Log.e("下载", "安全错误", e);
                        }

                    }
                    dlgDownload.incrementProgressBy(1);
                }

            }
            dlgDownload.dismiss();
            return null;
        }
    }

    private List<String> homeworkActions;
    private ProgressDialog dlgDownload;

}
