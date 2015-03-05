package com.odong.buddhismhomework.pages.reading;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.odong.buddhismhomework.R;
import com.odong.buddhismhomework.models.Favorite;
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
        final List<Favorite> favorites = ddh.listFavorite();
        ddh.close();


        final List<Map<String, String>> items = new ArrayList<Map<String, String>>();

        for (Favorite f : favorites) {
            Map<String, String> map = new HashMap<String, String>();
            map.put("title", f.getTitle());
            map.put("details", getString(f.getTypeId()));
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

                DwDbHelper ddh = new DwDbHelper(FavoritesActivity.this);
                WidgetHelper wh = new WidgetHelper(FavoritesActivity.this);
                switch (fav.getType()) {
                    case "dzj":
                        wh.showBook(ddh.getBook(fav.getTid()));
                        return;
                    case "ddc":
                        wh.showDdc(ddh.getDdc(fav.getTid()));
                        break;
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
                        ddh.setFavorite(fav.getType(), fav.getTid(), null, false);
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


}
