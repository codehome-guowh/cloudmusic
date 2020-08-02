package com.example.cloudmusic.contentprovider;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import com.example.cloudmusic.database.SongDatabaseHelper;
import com.example.cloudmusic.database.SongSheetDatabaseHelper;

public class SongSheetProvider extends ContentProvider {
    private static UriMatcher uriMatcher=new UriMatcher(UriMatcher.NO_MATCH);
    private static final int SONGSHEETS=1;
    private static final int SONGSHEET=2;
    private SongSheetDatabaseHelper songSheetDatabaseHelper;
    static {
    //        注册uri
        uriMatcher.addURI("com.example.cloudmusic.contentprovider.songprovider","songsheets",SONGSHEETS);
        uriMatcher.addURI("com.example.cloudmusic.contentprovider.songprovider","songsheet/#",SONGSHEET);
    }




    @Override
    public boolean onCreate() {
        songSheetDatabaseHelper =new SongSheetDatabaseHelper(this.getContext());
        return true;
    }

    @Override
    public String getType(Uri uri) {
        switch (uriMatcher.match(uri)){

            case SONGSHEETS:
                return "vnd.android.cursor.dir/com.example.songsheet";
            case SONGSHEET:
                return "vnd.android.cursor.dir/com.example.songsheet";
            default:
                throw new IllegalArgumentException();

        }

    }
    //插入数据
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        SQLiteDatabase dbSong= songSheetDatabaseHelper.getReadableDatabase();
        long rowId= dbSong.insert("songsheet",null,values);
        if(rowId>0){
            Uri songUri= ContentUris.withAppendedId(uri,rowId);
            getContext().getContentResolver().notifyChange(songUri,null);
            return songUri;
        }
        return null;
    }


    //删除数据
    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase dbSong= songSheetDatabaseHelper.getReadableDatabase();
        int num=0;
        num=dbSong.delete("songsheet",selection,selectionArgs);
        return num;
    }







    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        SQLiteDatabase dbSong= songSheetDatabaseHelper.getReadableDatabase();
        return dbSong.query("songsheet",projection,selection,selectionArgs,null,null,null,sortOrder);
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        SQLiteDatabase dbSong= songSheetDatabaseHelper.getReadableDatabase();
        return dbSong.update("songsheet",values,selection,selectionArgs);
    }
}
