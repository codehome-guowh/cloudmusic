package com.example.cloudmusic.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class SongSheetDatabaseHelper extends SQLiteOpenHelper {
    public SongSheetDatabaseHelper(Context context){
        super(context,"songsheet",null,1);
    }




    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(
                "create table songsheet(songsheetid integer primary key autoincrement,"+
                "songsheetname varchar,"+"headerid integer,"+"amount integer)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {


    }
}
