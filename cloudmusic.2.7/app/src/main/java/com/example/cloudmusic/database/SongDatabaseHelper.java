package com.example.cloudmusic.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class SongDatabaseHelper extends SQLiteOpenHelper {
    public SongDatabaseHelper(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, "song", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("create table song"+"(songid integer primary key autoincrement,"
                        +"songname varchar,"
                        +"songinsheet varchar,"
                        +"singer varchar,"
                        +"playstate integer,"
                        +"songsource varchar)");

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
