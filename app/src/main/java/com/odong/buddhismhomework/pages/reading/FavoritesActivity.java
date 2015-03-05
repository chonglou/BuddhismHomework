package com.odong.buddhismhomework.pages.reading;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.odong.buddhismhomework.R;
import com.odong.buddhismhomework.models.Book;
import com.odong.buddhismhomework.models.Favorite;
import com.odong.buddhismhomework.pages.WebActivity;
import com.odong.buddhismhomework.utils.DwDbHelper;
import com.odong.buddhismhomework.utils.WidgetHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by flamen on 15-2-19.
 */
public class FavoritesActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_items);
        getActionBar().setIcon(R.drawable.ic_action_favorite);
        setTitle(R.string.action_favorites);

        initList();
    }

    private void initList() {
        DwDbHelper ddh = new DwDbHelper(this);
        List<Book> books = ddh.getFavBookList();
        Map<String, String> ddc = ddh.listDdc();
        ddh.close();

        favorites = new ArrayList<>();
        for (Book b : books) {
            Favorite f = new Favorite();
            f.setTitle(b.getTitle());
            f.setDetails(b.getAuthor());
            f.setObj(b);
            f.setType("dzj");
            favorites.add(f);
        }
        for (Map.Entry<String, String> e : ddc.entrySet()) {
            Favorite f = new Favorite();
            f.setTitle(e.getValue());
            f.setDetails(e.getKey());
            f.setObj(e.getKey());
            f.setType("ddc");
            favorites.add(f);
        }

        final List<Map<String, String>> items = new ArrayList<Map<String, String>>();


        for (Favorite f : favorites) {
            Map<String, String> map = new HashMap<String, String>();
            map.put("title", f.getTitle());
            map.put("details", f.getDetails());
            items.add(map);
        }


        final SimpleAdapter adapter = new SimpleAdapter(this,
                items,
                android.R.layout.two_line_list_item,
                new String[]{"title", "details"},
                new int[]{android.R.id.text1, android.R.id.text2});

        ListView lv = (ListView) findViewById(R.id.lv_items);
        lv.setAdapter(adapter);


        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Favorite fav = favorites.get(position);
                Intent intent = null;
                switch (fav.getType()) {
                    case "dzj":
                        Log.d("点击收藏夹", fav.getObj().toString());
                        new WidgetHelper(FavoritesActivity.this).showBook((Book) fav.getObj());
                        return;
                    case "ddc":
                        intent = new Intent(FavoritesActivity.this, WebActivity.class);
                        intent.putExtra("url", (String) fav.getObj());
                        intent.putExtra("icon", R.drawable.ic_ddc);
                        intent.putExtra("title", R.string.title_ddc);

                        break;
                }
                if (intent != null) {
                    startActivity(intent);
                }
            }
        });


        lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                AlertDialog.Builder adb = new AlertDialog.Builder(FavoritesActivity.this);
                adb.setMessage(R.string.lbl_remove_favorites_item);
                adb.setTitle(R.string.lbl_are_you_sure);

                adb.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Favorite fav = favorites.get(position);
                        DwDbHelper ddh = new DwDbHelper(FavoritesActivity.this);
                        switch (fav.getType()) {
                            case "dzj":
                                ddh.setBookFav(((Book) fav.getObj()).getId(), false);
                                break;
                            case "ddc":
                                ddh.delDdc((String) fav.getObj());
                                break;
                        }
                        ddh.close();
                        items.remove(position);
                        favorites.remove(position);
                        adapter.notifyDataSetChanged();
                    }
                });
                adb.setNegativeButton(android.R.string.no, null);

                adb.create().show();

                return true;
            }
        });

    }

    private List<Favorite> favorites;


}
