package com.example.cloudmusic.util;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.widget.Toast;

import com.example.cloudmusic.bean.Song;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class SongUtil {

    public static List<Song> ResolveMusicToList(Context context) {
        List<Song> songList = new ArrayList<Song>();


        List<Song> mDatas = new ArrayList<>();
        ContentResolver resolver = context.getContentResolver();
        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        Cursor cursor = resolver.query(uri, null, null, null, null);
        int id = 1;

        if (cursor != null){
            while (cursor.moveToNext()) {
                String songName = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE));
                String singer = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST));
                String path = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA));
                long duration = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION));
                SimpleDateFormat sdf = new SimpleDateFormat("mm:ss");
                String time = sdf.format(new Date(duration));
                Song song = new Song(String.valueOf(id),songName,singer,path,time);
                id++;
                mDatas.add(song);
            }
        }
        else {
            Toast.makeText(context,"数据查询异常",Toast.LENGTH_SHORT).show();
            return null;
        }

        return mDatas;
    }
}
