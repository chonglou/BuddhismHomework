package com.odong.buddhismhomework;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.sql.Timestamp;
import java.util.Date;

/**
 * Created by flamen on 15-2-7.
 */
public class DwDbHelper extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "BuddhismHomework.db";

    public void set(String key, String val) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("val", val);
        if (get(key) == null) {
            cv.put("`key`", key);
            db.insert("settings", null, cv);
        } else {
            db.update("settings", cv, "`key` = ?", new String[]{key});
        }
    }


    public String get(String key) {
        Cursor c = getReadableDatabase().query("settings", new String[]{"val"}, "`key` = ?", new String[]{key}, null, null, "id DESC", "1");
        if (c.moveToFirst()) {
            return c.getString(c.getColumnIndexOrThrow("val"));
        }
        return null;

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

    private void uninstall(SQLiteDatabase db, int version) {
        switch (version) {
            case 1:
                for (String s : new String[]{
                        //"homework",
                        "logs",
                        "settings"}) {
                    db.execSQL(drop_table(s));
                }
                break;
        }
    }

    private void install(SQLiteDatabase db, int version) {

        switch (version) {
            case 1:
                for (String s : new String[]{
                        //"CREATE TABLE homework(id INTEGER PRIMARY KEY AUTOINCREMENT, name VARCHAR(32) NOT NULL, details VARCHAR(500) NOT NULL) IF NOT EXISTS",
                        "CREATE TABLE logs(id INTEGER PRIMARY KEY AUTOINCREMENT, message VARCHAR(255) NOT NULL, created TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP) IF NOT EXISTS",
                        "CREATE TABLE settings(`key` VARCHAR(32) PRIMARY KEY, val TEXT NOT NULL) IF NOT EXISTS"}) {
                    db.execSQL(s);
                }
                break;

        }

    }

    private String drop_table(String name) {
        return "DROP TABLE IF EXISTS " + name;
    }


}
