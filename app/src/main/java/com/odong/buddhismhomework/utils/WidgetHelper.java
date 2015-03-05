package com.odong.buddhismhomework.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.odong.buddhismhomework.R;
import com.odong.buddhismhomework.models.Book;
import com.odong.buddhismhomework.pages.reading.EpubActivity;
import com.odong.buddhismhomework.pages.reading.SearchActivity;
import com.odong.buddhismhomework.pages.reading.TextActivity;
import com.odong.buddhismhomework.services.SyncService;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by flamen on 15-2-20.
 */
public class WidgetHelper {
    public WidgetHelper(Context context) {
        this.context = context;
    }

    public void showFavoriteDialog(final Book book) {
        AlertDialog.Builder adbF = new AlertDialog.Builder(context);
        adbF.setTitle(R.string.action_add_to_favorites);
        adbF.setMessage(R.string.lbl_are_you_sure);
        adbF.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                DwDbHelper ddh = new DwDbHelper(context);
                ddh.setBookFav(book.getId(), true);
                ddh.close();
                toast(context.getString(R.string.lbl_success), false);
            }
        });
        adbF.setNegativeButton(android.R.string.no, null);
        adbF.create().show();
    }

    public void showSyncDialog() {
        AlertDialog.Builder adb = new AlertDialog.Builder(context);
        adb.setTitle(R.string.action_sync);
        adb.setMessage(R.string.dlg_sync);
        adb.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                context.startService(new Intent(context, SyncService.class));
            }
        });
        adb.setNegativeButton(android.R.string.no, null);
        adb.create().show();
    }

    public String readFile(Integer... files) throws IOException {
        StringBuilder sb = new StringBuilder();
        for (int i : files) {
            if (i > 0) {
                BufferedReader br = new BufferedReader(new InputStreamReader(context.getResources().openRawResource(i)));
                String line;
                while ((line = br.readLine()) != null) {
                    sb.append(line);
                    sb.append("\n");
                }
                br.close();
            }

            sb.append("\n\n");
        }
        return sb.toString();
    }

    public void toast(final String msg, boolean back) {
        if (back) {
            Handler handler = new Handler(Looper.getMainLooper());
            handler.post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
        }
    }

    public void notification(Intent intent, String msg) {
        PendingIntent pd = PendingIntent.getActivity(context, 0, intent, 0);
        Notification.Builder nf = new Notification.Builder(context)
                .setContentText(msg)
                .setContentTitle(context.getString(R.string.app_name))
                .setSmallIcon(R.drawable.ic_launcher)
                .setContentIntent(pd)
                .setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_ALL);

        NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        nm.notify(0, nf.build());
    }

    public interface BookListCallback {
        boolean run(SimpleAdapter adapter, int position, List<Map<String, String>> items);
    }

    public void initTextViewFont(int rid) {
        Activity context = (Activity) this.context;
        float size = new KvHelper(context).get("book.font.size", Float.class, 20.0f);
        ((TextView) context.findViewById(rid)).setTextSize(TypedValue.COMPLEX_UNIT_SP, size);
    }

    public void zoomTextView(int rid, boolean out) {
        Activity context = (Activity) this.context;
        TextView tv = ((TextView) context.findViewById(rid));
        KvHelper kh = new KvHelper(context);

        float i = kh.get("book.font.size", Float.class, tv.getTextSize());
        float j = 1f;
        float k = out ? (i + j) : (i - j);

        Log.d("字体大小", "" + i + "\t" + k);

        tv.setTextSize(k);
        kh.set("book.font.size", k);

    }

    public void initDzjBookList(int lvId, final List<Book> books, final BookListCallback callback) {
        Activity context = (Activity) this.context;

        final List<Map<String, String>> items = new ArrayList<Map<String, String>>();
        for (Book d : books) {
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

        ListView lv = (ListView) context.findViewById(lvId);
        lv.setAdapter(adapter);


        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                showDzj(books.get(position));
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

    public void showDzj(Book book) {
        Intent intent;
        if (SyncService.CBETA_NAME.equals(book.getType())) {
            intent = new Intent(context, EpubActivity.class);
        } else {
            intent = new Intent(context, TextActivity.class);
            intent.putExtra("type", "dzj");
        }
        intent.putExtra("file", new Gson().toJson(book));
        context.startActivity(intent);
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

    private Context context;
}
