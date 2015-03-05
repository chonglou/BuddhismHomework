package com.odong.buddhismhomework.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabaseLockedException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.google.gson.internal.LinkedHashTreeMap;
import com.odong.buddhismhomework.models.Book;
import com.odong.buddhismhomework.models.Channel;
import com.odong.buddhismhomework.models.Playlist;
import com.odong.buddhismhomework.models.Video;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by flamen on 15-2-7.
 */
public class DwDbHelper extends SQLiteOpenHelper {

    public List<Channel> listChannel() {
        List<Channel> channels = new ArrayList<>();
        Cursor c = getReadableDatabase().query("channels", new String[]{"cid", "title", "description"}, null, null, null, null, "created ASC");

        while (c.moveToNext()) {
            Channel ch = new Channel();
            ch.setCid(c.getString(c.getColumnIndexOrThrow("cid")));
            ch.setTitle(c.getString(c.getColumnIndexOrThrow("title")));
            ch.setDescription(c.getString(c.getColumnIndexOrThrow("description")));
            channels.add(ch);
        }
        c.close();
        return channels;
    }

    public List<Playlist> listPlaylist(String channel) {
        List<Playlist> playlist = new ArrayList<>();
        Cursor c = getReadableDatabase().query("playlist", new String[]{"pid", "title", "description"}, "cid = ?", new String[]{channel}, null, null, "created DESC");

        while (c.moveToNext()) {
            Playlist p = new Playlist();
            p.setPid(c.getString(c.getColumnIndexOrThrow("pid")));
            p.setTitle(c.getString(c.getColumnIndexOrThrow("title")));
            p.setDescription(c.getString(c.getColumnIndexOrThrow("description")));
            playlist.add(p);
        }
        c.close();
        return playlist;
    }

    public List<Video> listVideo(String playlist) {
        List<Video> videos = new ArrayList<>();
        Cursor c = getReadableDatabase().query("videos", new String[]{"vid", "title", "description"}, "pid = ?", new String[]{playlist}, null, null, "created DESC");

        while (c.moveToNext()) {
            Video v = new Video();
            v.setVid(c.getString(c.getColumnIndexOrThrow("vid")));
            v.setTitle(c.getString(c.getColumnIndexOrThrow("title")));
            v.setDescription(c.getString(c.getColumnIndexOrThrow("description")));
            videos.add(v);
        }
        c.close();
        return videos;
    }

    public Map<String, String> listDdc() {
        Map<String, String> map = new LinkedHashTreeMap<>();
        Cursor c = getReadableDatabase().query("ddc", new String[]{"url", "title"}, null, null, null, null, "created DESC");

        while (c.moveToNext()) {
            String url = c.getString(c.getColumnIndexOrThrow("url"));
            String title = c.getString(c.getColumnIndexOrThrow("title"));
            map.put(url, title);
        }
        c.close();
        return map;
    }

    public void addDdc(String url, String title) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("title", title);
        Cursor c = db.rawQuery("SELECT COUNT(*) FROM ddc WHERE url=?", new String[]{url});
        c.moveToFirst();
        int size = c.getInt(0);
        c.close();
        if (size == 0) {
            cv.put("url", url);
            db.insert("ddc", null, cv);
        } else {
            db.update("ddc", cv, "url = ?", new String[]{url});
        }
    }

    public void delDdc(String url) {
        getWritableDatabase().delete("ddc", "url=?", new String[]{url});
    }

    public List<Book> searchBook(String keyword) {
        keyword = "%" + keyword + "%";
        List<Book> books = new ArrayList<Book>();
        try {
            Cursor c = getReadableDatabase().query("books", new String[]{"id", "name", "title", "author", "type"}, "name LIKE ? OR title LIKE ? OR author LIKE ?", new String[]{keyword, keyword, keyword}, null, null, "id ASC", "250");
            while (c.moveToNext()) {
                Book d = createBook(c);
                books.add(d);
            }
            c.close();

        } catch (SQLiteDatabaseLockedException e) {
            Log.d("数据库", "锁定", e);
        }
        return books;
    }

    public List<Book> getFavBookList() {
        List<Book> books = new ArrayList<Book>();
        try {
            Cursor c = getReadableDatabase().query("books", new String[]{"id", "name", "title", "author", "type"}, "fav = ?", new String[]{"1"}, null, null, "id ASC");
            while (c.moveToNext()) {
                Book d = createBook(c);
                d.setFav(true);
                books.add(d);
            }
            c.close();
        } catch (SQLiteDatabaseLockedException e) {
            Log.d("数据库", "锁定", e);
        }
        return books;
    }

    public void setBookFav(int id, boolean fav) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("fav", fav ? 1 : 0);
        db.update("books", cv, "id = ?", new String[]{Integer.toString(id)});
    }

    public List<String> getBookTypeList() {
        List<String> types = new ArrayList<String>();
        try {
            Cursor c = getReadableDatabase().query(true, "books", new String[]{"type"}, null, null, null, null, "id ASC", null);
            while (c.moveToNext()) {
                types.add(c.getString(c.getColumnIndexOrThrow("type")));
            }
            c.close();
        } catch (SQLiteDatabaseLockedException e) {
            Log.d("数据库", "锁定", e);
        }
        return types;
    }

    public List<Book> getBookList(String type) {
        List<Book> books = new ArrayList<Book>();
        Cursor c = getReadableDatabase().query("books", new String[]{"id", "name", "title", "author", "fav", "type"}, "type = ?", new String[]{type}, null, null, "id ASC");
        while (c.moveToNext()) {
            Book d = createBook(c);
            d.setFav(c.getInt(c.getColumnIndexOrThrow("fav")) == 1);
            books.add(d);
        }
        c.close();
        return books;
    }

    public void loadSql(File file) {
        SQLiteDatabase db = getWritableDatabase();

        try {
            db.beginTransaction();
            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
            String line;
            while ((line = br.readLine()) != null) {
                db.execSQL(line);
            }
            br.close();
            db.setTransactionSuccessful();
        } catch (IOException e) {
            Log.d("加载", "SQL", e);
        } finally {
            db.endTransaction();
        }
    }


    public interface LogCallback {
        void call(String message, Date created);
    }

    public void clearLog(int days) {
        getWritableDatabase().delete("logs", "created < ?", new String[]{new Timestamp(new Date().getTime() - days * 1000 * 60 * 60 * 24).toString()});
    }

    public void listLog(int size, LogCallback callback) {
        Cursor c = getReadableDatabase().query("logs", new String[]{"message", "created"}, null, null, null, null, "id DESC", Integer.toString(size));
        while (c.moveToNext()) {
            callback.call(c.getString(c.getColumnIndexOrThrow("id")), Timestamp.valueOf(c.getString(c.getColumnIndexOrThrow("created"))));
        }
        c.close();
    }

    public void addLog(String message) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("message", message);
        db.insert("logs", null, cv);

    }

    public DwDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        onUpgrade(db, 0, DATABASE_VERSION);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        for (int i = oldVersion + 1; i <= newVersion; i++) {
            install(db, i);
        }
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        for (int i = oldVersion; i > newVersion; i--) {
            uninstall(db, i);
        }
    }


    private Book createBook(Cursor c) {
        Book d = new Book();
        d.setId(c.getInt(c.getColumnIndexOrThrow("id")));
        d.setName(c.getString(c.getColumnIndexOrThrow("name")));
        d.setAuthor(c.getString(c.getColumnIndexOrThrow("author")));
        d.setTitle(c.getString(c.getColumnIndexOrThrow("title")));
        d.setType(c.getString(c.getColumnIndexOrThrow("type")));
        return d;
    }

    private void uninstall(SQLiteDatabase db, int version) {
        switch (version) {
            case 4:
                drop_index("ddc_url");
                drop_table("ddc");
                break;
            case 2:
                for (String s : new String[]{
                        drop_index("books_title"),
                        drop_index("books_name"),
                        drop_index("books_type"),
                        drop_index("books_author"),
                        drop_table("books")
                }) {
                    db.execSQL(s);
                }

                break;
            case 1:
                for (String s : new String[]{
                        "logs",
                        "settings"}) {
                    db.execSQL(drop_table(s));
                }
                break;
        }
    }

    private void install(SQLiteDatabase db, int version) {

        switch (version) {
            case 4:
                for (String s : new String[]{
                        "CREATE TABLE IF NOT EXISTS ddc(title VARCHAR(255) NOT NULL, url VARCHAR(255)  PRIMARY KEY, created TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP)",
                        "CREATE INDEX IF NOT EXISTS ddc_url ON ddc(url)",
                }) {
                    db.execSQL(s);
                }
                break;
            case 3:
                db.execSQL("ALTER TABLE books ADD COLUMN fav INTEGER(1) NOT NULL DEFAULT 0");
                break;
            case 2:
                for (String s : new String[]{
                        "CREATE TABLE IF NOT EXISTS books(id INTEGER PRIMARY KEY AUTOINCREMENT, title VARCHAR(255) NOT NULL, name VARCHAR(255) NOT NULL, author VARCHAR(255) NOT NULL, type VARCHAR(255) NOT NULL, created TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP)",
                        "CREATE INDEX IF NOT EXISTS books_name ON books(name)",
                        "CREATE INDEX IF NOT EXISTS books_title ON books(title)",
                        "CREATE INDEX IF NOT EXISTS books_author ON books(author)",
                        "CREATE INDEX IF NOT EXISTS books_type ON books(type)",
                }) {
                    db.execSQL(s);
                }
                break;
            case 1:
                for (String s : new String[]{
                        "CREATE TABLE IF NOT EXISTS logs(id INTEGER PRIMARY KEY AUTOINCREMENT, message VARCHAR(255) NOT NULL, created TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP)",
                        "CREATE TABLE IF NOT EXISTS settings(`key` VARCHAR(32) PRIMARY KEY, val TEXT NOT NULL)"}) {
                    db.execSQL(s);
                }
                break;

        }

    }

    private String drop_table(String name) {
        return "DROP TABLE IF EXISTS " + name;
    }

    private String drop_index(String name) {
        return "DROP INDEX IF EXISTS " + name;
    }

    public static final int DATABASE_VERSION = 4;
    public static final String DATABASE_NAME = "BuddhismHomework.db";


}
