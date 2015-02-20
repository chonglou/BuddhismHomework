package com.odong.buddhismhomework.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.google.gson.Gson;
import com.odong.buddhismhomework.R;
import com.odong.buddhismhomework.models.Dzj;
import com.odong.buddhismhomework.pages.DzjBookActivity;
import com.odong.buddhismhomework.pages.SearchActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by flamen on 15-2-20.
 */
public class WidgetHelper {
    public WidgetHelper(Activity context) {
        this.context = context;
    }

    public interface BookListCallback {
        boolean run(SimpleAdapter adapter, int position, List<Map<String, String>> items);
    }

    public void initTextViewFont(int rid) {
        Float size = new KvHelper(context).get("book.font.size", Float.class, null);
        if (size != null) {
            ((TextView) context.findViewById(rid)).setTextSize(size);
        }
    }

    public void zoomTextView(int rid, boolean out) {
        TextView tv = ((TextView) context.findViewById(rid));
        KvHelper kh = new KvHelper(context);

        float i = kh.get("book.font.size", Float.class, tv.getTextSize());
        float j = 1f;
        float k = out ? (i + j) : (i - j);

        Log.d("字体大小", "" + i + "\t" + k);

        tv.setTextSize(k);
        kh.set("book.font.size", k);

    }

    public void initDzjBookList(final List<Dzj> books, final BookListCallback callback) {
        final List<Map<String, String>> items = new ArrayList<Map<String, String>>();
        for (Dzj d : books) {
            Map<String, String> map = new HashMap<String, String>();
            map.put("title", d.getTitle());
            map.put("details", d.getAuthor());
            items.add(map);
        }
        final SimpleAdapter adapter = new SimpleAdapter(context,
                items,
                android.R.layout.two_line_list_item,
                new String[]{"title", "details"},
                new int[]{android.R.id.text1, android.R.id.text2});

        ListView lv = (ListView) context.findViewById(R.id.lv_items);
        lv.setAdapter(adapter);


        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(context, DzjBookActivity.class);
                intent.putExtra("book", new Gson().toJson(books.get(position)));
                context.startActivity(intent);
            }
        });

        if (callback != null) {
            lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                    return callback.run(adapter, position, items);
                }
            });
        }
    }

    public void showSearchDialog() {
        AlertDialog.Builder adb = new AlertDialog.Builder(context);
        adb.setTitle(R.string.action_search);

        final EditText keyword = new EditText(context);
        keyword.setHint(R.string.lbl_hint_search);
        adb.setView(keyword);
        adb.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(context, SearchActivity.class);
                intent.putExtra("type", "dzj");
                intent.putExtra("keyword", keyword.getText().toString().trim());
                context.startActivity(intent);
            }
        });
        adb.setNegativeButton(android.R.string.no, null);
        adb.create().show();
    }

    private Activity context;
}
