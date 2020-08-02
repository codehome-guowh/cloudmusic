package com.dwb.appbardemo.utils;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;

import androidx.annotation.NonNull;

import com.dwb.appbardemo.SearchMusicActivity;
import com.dwb.appbardemo.model.LocalMusicBean;
import com.dwb.appbardemo.model.MusicDetailBean;
import com.dwb.appbardemo.model.MusicLyc;
import com.dwb.appbardemo.model.MusicOnlineModel;
import com.google.gson.Gson;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MusicDataUtils {

    //查询本地的音频文件
    public static List<LocalMusicBean> ResolveMusicToList(Context context) {


        List<LocalMusicBean> mDatas = new ArrayList<>();
        //1.获取ContentResolver对象
        ContentResolver resolver = context.getContentResolver();
        //2.获取本地音乐存储的Uri地址
        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        //3.开始查询地址
        Cursor cursor = resolver.query(uri, null, null, null, null);
        //4.遍历Cursor
        int id = 0;

        while (cursor.moveToNext()) {
            String song = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE));
            String singer = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST));
            String album = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM));
            String path = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA));
            long duration = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION));
            SimpleDateFormat sdf = new SimpleDateFormat("mm:ss");
            String time = sdf.format(new Date(duration));

            if (!singer.equals("<unknown>")) {
                id++;
                String sid = String.valueOf(id);
                LocalMusicBean bean = new LocalMusicBean(sid, song, singer, album, time, path);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        String musicId = getMusicId(song);
                        bean.setAlbumPic(getAlbumPic(musicId));
                    }
                }).start();
                mDatas.add(bean);
            }
        }

        return mDatas;
    }



    //通过歌名获取其id
    public static String getMusicId(String name) {
        String id = "";
        String url = "http://musicapi.leanapp.cn/search?keywords=" + name;
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .build();
        Response response = null;
        try {
            response = client.newCall(request).execute();
            String jsonContent = response.body().string();
            MusicOnlineModel musicOnlineModel = new Gson().fromJson(jsonContent, MusicOnlineModel.class);
            long id1 = musicOnlineModel.getResult().getSongs().get(0).getId();
            id = String.valueOf(id1);

        } catch (IOException e) {
            e.printStackTrace();
        }
        return id;
    }

    //通过歌曲id获取其歌词
    public static String getMusicLyc(String id) {
        String lyric = "";
        String url = "http://music.163.com/api/song/lyric?os=pc&id=" + id + "&lv=-1&kv=-1&tv=-1";
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .build();
        Response response = null;
        try {
            response = client.newCall(request).execute();
            String jsonContent = response.body().string();
            MusicLyc musicLyc = new Gson().fromJson(jsonContent, MusicLyc.class);
            if (musicLyc.getLrc() != null){
                lyric = musicLyc.getLrc().getLyric();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return lyric;

    }

    //通过歌曲id获取专辑图片
    public static String getAlbumPic(String id) {
        String picUrl = "";
        String url = "http://music.163.com/api/song/detail/?id=" + id + "&ids=%5B" + id + "%5D";
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .build();
        Response response = null;
        try {
            response = client.newCall(request).execute();
            String jsonContent = response.body().string();
            MusicDetailBean musicDetailBean = new Gson().fromJson(jsonContent, MusicDetailBean.class);
            if (musicDetailBean.getSongs().get(0).getAlbum().getPicUrl() != null) {
                picUrl = musicDetailBean.getSongs().get(0).getAlbum().getPicUrl();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return picUrl;
    }

    //查询在线音乐
    public static void getOnlineMusic(Context context, String edit) {
        SearchMusicActivity.onlineMusic = new ArrayList<>();
        new Thread(new Runnable() {
            @Override
            public void run() {

                String url = "http://musicapi.leanapp.cn/search?keywords=" + edit;
                OkHttpClient client = new OkHttpClient();
                Request request = new Request.Builder()
                        .url(url)
                        .build();
                Response response = null;
                try {
                    response = client.newCall(request).execute();
                    String jsonContent = response.body().string();
                    MusicOnlineModel musicOnlineModel = new Gson().fromJson(jsonContent, MusicOnlineModel.class);
                    int i = 0;
                    if (musicOnlineModel.getResult().getSongs() != null) {
                        i = musicOnlineModel.getResult().getSongs().size();
                    }

                    for (int j = 0; j < i; j++) {
                        long musicId = musicOnlineModel.getResult().getSongs().get(j).getId();
                        String id = String.valueOf(musicId);
                        String song = musicOnlineModel.getResult().getSongs().get(j).getName();
                        String singer = musicOnlineModel.getResult().getSongs().get(j).getArtists().get(0).getName();
                        String album = musicOnlineModel.getResult().getSongs().get(j).getAlbum().getName();
                        String path = "http://music.163.com/song/media/outer/url?id=" + id + ".mp3";
                        long duration = musicOnlineModel.getResult().getSongs().get(j).getDuration();
                        SimpleDateFormat sdf = new SimpleDateFormat("mm:ss");
                        String time = sdf.format(new Date(duration));
                        String sid = String.valueOf(j + 1);
                        LocalMusicBean bean = new LocalMusicBean(sid, song, singer, album, time, path);
                        SearchMusicActivity.onlineMusic.add(bean);
                        Log.i("123",""+bean);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }


            }
        }).start();

    }

}
