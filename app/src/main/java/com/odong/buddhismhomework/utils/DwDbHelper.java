package com.odong.buddhismhomework.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.google.gson.Gson;
import com.odong.buddhismhomework.models.Dzj;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by flamen on 15-2-7.
 */
public class DwDbHelper extends SQLiteOpenHelper {

    public List<Dzj> searchDzj(String keyword) {
        keyword = "%" + keyword + "%";
        List<Dzj> books = new ArrayList<Dzj>();
        Cursor c = getReadableDatabase().query("books", new String[]{"id", "name", "title", "author"}, "name LIKE ? OR title LIKE ? OR author LIKE ?", new String[]{keyword, keyword, keyword}, null, null, "id ASC", "100");
        while (c.moveToNext()) {
            Dzj d = createDzj(c);
            books.add(d);
        }
        c.close();
        return books;
    }

    public List<Dzj> getFavDzjList() {
        List<Dzj> books = new ArrayList<Dzj>();
        Cursor c = getReadableDatabase().query("books", new String[]{"id", "name", "title", "author"}, "fav = ?", new String[]{"1"}, null, null, "id ASC");
        while (c.moveToNext()) {
            Dzj d = createDzj(c);
            d.setFav(true);
            books.add(d);
        }
        c.close();
        return books;
    }

    public void setDzjFav(int id, boolean fav) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("fav", fav ? 1 : 0);
        db.update("books", cv, "id = ?", new String[]{Integer.toString(id)});
    }

    public List<String> getDzjTypeList() {
        List<String> types = new ArrayList<String>();
        Cursor c = getReadableDatabase().query(true, "books", new String[]{"type"}, null, null, null, null, "id ASC", null);
        while (c.moveToNext()) {
            types.add(c.getString(c.getColumnIndexOrThrow("type")));
        }
        c.close();
        return types;
    }

    public List<Dzj> getDzjList(String type) {
        List<Dzj> books = new ArrayList<Dzj>();
        Cursor c = getReadableDatabase().query("books", new String[]{"id", "name", "title", "author", "fav"}, "type = ?", new String[]{type}, null, null, "id ASC");
        while (c.moveToNext()) {

            Dzj d = createDzj(c);
            d.setType(type);
            d.setFav(c.getInt(c.getColumnIndexOrThrow("fav")) == 1);
            books.add(d);
        }
        c.close();
        return books;
    }


    public void resetDzj(List<Dzj> books) {
        SQLiteDatabase db = getWritableDatabase();
        Log.d("数据库", "清空books");
        getWritableDatabase().delete("books", null, null);
        ContentValues cv = new ContentValues();
        for (Dzj d : books) {
            cv.put("type", d.getType());
            cv.put("title", d.getTitle());
            cv.put("author", d.getAuthor());
            cv.put("name", d.getName());

            db.insert("books", null, cv);
        }

    }

    public void set(String key, Object val) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("val", new Gson().toJson(val));
        if (get(key, val.getClass()) == null) {
            cv.put("`key`", key);
            db.insert("settings", null, cv);
        } else {
            db.update("settings", cv, "`key` = ?", new String[]{key});
        }
    }


    public <T> T get(String key, Class<T> clazz) {
        Cursor c = getReadableDatabase().query("settings", new String[]{"val"}, "`key` = ?", new String[]{key}, null, null, null, "1");
        T obj = null;
        if (c.moveToFirst()) {
            String val = c.getString(c.getColumnIndexOrThrow("val"));
            obj = new Gson().fromJson(val, clazz);
        }
        c.close();
        return obj;

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


    private Dzj createDzj(Cursor c) {
        Dzj d = new Dzj();
        d.setId(c.getInt(c.getColumnIndexOrThrow("id")));
        d.setName(c.getString(c.getColumnIndexOrThrow("name")));
        d.setAuthor(c.getString(c.getColumnIndexOrThrow("author")));
        d.setTitle(c.getString(c.getColumnIndexOrThrow("title")));
        return d;
    }

    private void uninstall(SQLiteDatabase db, int version) {
        switch (version) {
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

    public static final int DATABASE_VERSION = 3;
    public static final String DATABASE_NAME = "BuddhismHomework.db";


}
