package com.example.cloudmusic.servcie;

import android.app.Service;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompatSideChannelService;

import java.io.IOException;

public class MediaPlayService extends Service {
    private MediaPlayer mediaPlayer;
    public MediaPlayService() {
    }
    private MyBinder binder=new MyBinder();
    public class MyBinder   extends Binder{

    }

    @Override
    public void onCreate() {
        super.onCreate();
        mediaPlayer=new MediaPlayer();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public IBinder onBind(Intent intent) {
        String songName=intent.getStringExtra("songname");


        try {
            AssetFileDescriptor fd = null;
            fd = getAssets().openFd(songName);
            mediaPlayer.setDataSource(fd);
            mediaPlayer.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return binder;


    }


    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onStart(Intent intent, int startId) {

        super.onStart(intent, startId);
        int state=intent.getIntExtra("state",0);

        if (state==0){




                mediaPlayer.start();



            }
        else {

                mediaPlayer.pause();
            }
//        if(mediaPlayer.isPlaying()){
//
//        }






    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }


    @Override
    public boolean onUnbind(Intent intent) {
        return true;
    }

    @Override
    public void onRebind(Intent intent) {
        super.onRebind(intent);
    }
}
