package com.example.neteasemusic.db;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

public class Mydb {

    private MyDatabaseHelper helper;

    public Mydb(MyDatabaseHelper helper) {
        this.helper = helper;
    }

    public void add(String name, String author, String time) {
        SQLiteDatabase db = helper.getWritableDatabase();
        db.execSQL("insert into Music1 (name,author,time) values(?,?,?)", new String[]{name, author, time});
        db.close();
    }

    public List<ArrayList<String>> query() {
        SQLiteDatabase db = helper.getWritableDatabase();
        ArrayList<String> name = new ArrayList<>();
        ArrayList<String> author = new ArrayList<>();
        ArrayList<String> time = new ArrayList<>();
        List<ArrayList<String>> list = new ArrayList<>();
        Cursor cursor = db.rawQuery("select * from music", null);
        int i = 0;
        if (cursor.moveToFirst()) {
            do {
                name.add(i, cursor.getString(cursor.getColumnIndex("name")));
                author.add(i, cursor.getString(cursor.getColumnIndex("author")));
                time.add(i, cursor.getString(cursor.getColumnIndex("time")));
                i++;
            } while (cursor.moveToNext());
            cursor.close();
        }
        db.close();
        list.add(name);
        list.add(author);
        list.add(time);
        return list;
    }


    public boolean isLoved(String name) {
        boolean isLove = false;
        SQLiteDatabase db = helper.getWritableDatabase();
        Cursor cursor = db.rawQuery("select * from music where name = ?", new String[]{name});
        if (cursor.getCount() != 0) {
            isLove = true;
        }
        cursor.close();
        db.close();
        return isLove;
    }

    public void delete(String name) {
        SQLiteDatabase db = helper.getWritableDatabase();
        db.execSQL("delete from Music1 where name = ?", new String[]{name});
        db.close();
    }
}
