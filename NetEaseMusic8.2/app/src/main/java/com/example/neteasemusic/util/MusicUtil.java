package com.example.neteasemusic.util;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.provider.MediaStore;

import com.example.neteasemusic.R;
import com.example.neteasemusic.bean.Song;
import com.example.neteasemusic.MyApplication;

import java.util.ArrayList;
import java.util.List;

public class MusicUtil {
    private Context context;
    private List<Song> songList;

    public MusicUtil(Context context, List<Song> songList) {
        this.context = context;
        this.songList = songList;
    }

    public static List<Song> init(Context context) {
        List<Song> songList = new ArrayList<>();
        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        String[] strings = {MediaStore.Audio.Media.TITLE, MediaStore.Audio.Media.ARTIST
                , MediaStore.Audio.Media.DATA, MediaStore.Audio.Media.DURATION, MediaStore.Audio.Media.SIZE,
                MediaStore.Audio.Media.ALBUM_ID};
        ContentResolver contentResolver = context.getContentResolver();
        Cursor cursor = contentResolver.query(uri, strings, null, null, null);
        while (cursor.moveToNext()) {
            String title = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE));
            String artist = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST));
            String data = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA));
            int duration = cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION));
            int size = cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Media.SIZE));
            Song song = new Song(title, artist, data, duration, size);
            songList.add(song);
        }
        cursor.close();
        return songList;
    }


    public static String formatTime(int time) {
        if (time / 1000 % 60 < 10) {
            return time / 1000 / 60 + ":0" + time / 1000 % 60;
        } else {
            return time / 1000 / 60 + ":" + time / 1000 % 60;
        }
    }



    public static Bitmap getAlbum(String mediaUri) {
        MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();
        mediaMetadataRetriever.setDataSource(mediaUri);
        byte[] picture = mediaMetadataRetriever.getEmbeddedPicture();
        Bitmap bitmap;
        if(picture == null){
            bitmap = BitmapFactory.decodeResource(MyApplication.getContext().getResources(), R.drawable.defaultpicture);
            return bitmap;
        }
        bitmap = BitmapFactory.decodeByteArray(picture, 0, picture.length);
            return bitmap;
    }
}
