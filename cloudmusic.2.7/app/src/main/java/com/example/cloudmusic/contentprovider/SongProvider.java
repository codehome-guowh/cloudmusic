package com.example.cloudmusic.contentprovider;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.provider.UserDictionary;

import com.example.cloudmusic.database.SongDatabaseHelper;
import com.example.cloudmusic.database.SongSheetDatabaseHelper;

import java.util.function.DoubleBinaryOperator;

public class SongProvider extends ContentProvider {
    private static UriMatcher uriMatcher=new UriMatcher(UriMatcher.NO_MATCH);
    private static final int SONGS=1;
    private static final int SONG=2;
    private SongDatabaseHelper songDatabaseHelper;
    static {
//        注册uri
        uriMatcher.addURI("com.example.cloudmusic.contentprovider.songprovider","songs",SONGS);
        uriMatcher.addURI("com.example.cloudmusic.contentprovider.songprovider","song/#",SONG);
    }




    @Override
    public boolean onCreate() {
        songDatabaseHelper=new SongDatabaseHelper(this.getContext(),"song",null,1);
        return true;
    }

    @Override
    public String getType(Uri uri) {
       switch (uriMatcher.match(uri)){

           case SONGS:
               return "vnd.android.cursor.dir/com.example.song";
           case SONG:
               return "vnd.android.cursor.dir/com.example.song";
           default:
               throw new IllegalArgumentException();

       }

    }
//插入数据
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        SQLiteDatabase dbSong=songDatabaseHelper.getReadableDatabase();
        long rowId= dbSong.insert("song",null,values);
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
        SQLiteDatabase dbSong=songDatabaseHelper.getReadableDatabase();
        int num=0;
        num=dbSong.delete("song",selection,selectionArgs);
        return num;
    }







    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        SQLiteDatabase dbSong=songDatabaseHelper.getReadableDatabase();
        return dbSong.query("song",projection,selection,selectionArgs,null,null,null,sortOrder);
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        SQLiteDatabase dbSong=songDatabaseHelper.getReadableDatabase();
       return dbSong.update("song",values,selection,selectionArgs);
    }
}
