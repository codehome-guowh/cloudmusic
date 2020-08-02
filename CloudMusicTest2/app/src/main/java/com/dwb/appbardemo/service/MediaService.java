package com.dwb.appbardemo.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;

import androidx.annotation.Nullable;

import com.dwb.appbardemo.OnlineMusicActivity;
import com.dwb.appbardemo.model.LocalMusicBean;
import com.dwb.appbardemo.utils.MusicDataUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;



public class MediaService extends Service {
    private MyBinder binder = new MyBinder();
    public MediaPlayer mediaPlayer = new MediaPlayer();

    //记录当前暂停音乐进度条的位置
    int currentPausePositionInSong = 0;

    //更新歌词的频率，每100ms更新一次
    private int mPlayerTimerDuration = 100;
    //更新歌词的定时器
    private Timer mTimer;

    //设置播放模式，0为列表循环，1为单曲循环,2为随机播放
    int play_style = 0;

    //播放位置
    int currentPosition;

    //喜爱音乐的集合大小
    int size;

    //数据源集合
    List<LocalMusicBean> musicBeans = new ArrayList<>();


    LoveMusicListSizeBroadcastReceiver broadcastReceiver;

    private class LoveMusicListSizeBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            size = intent.getIntExtra("LoveMusicListSize", 0);

        }
    }


    @Override
    public void onCreate() {

        //创建广播接收者,捕获play_style动作
        broadcastReceiver = new LoveMusicListSizeBroadcastReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction("LoveMusic");
        registerReceiver(broadcastReceiver, filter);

        //获取数据源数据
        musicBeans = MusicDataUtils.ResolveMusicToList(getApplicationContext());
        super.onCreate();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        play_style = intent.getIntExtra("play_style", 0);
        currentPosition = intent.getIntExtra("currentPosition",0);

        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                if (play_style == 0){
                    if (currentPosition == musicBeans.size() - 1 || currentPosition == OnlineMusicActivity.mDatas.size() - 1 || currentPosition == size - 1){
                        currentPosition = 0;
                    }else {
                        currentPosition +=1;
                    }
                }else if (play_style == 2){
                    Random random = new Random();
                    currentPosition = random.nextInt(musicBeans.size() - 1);

                }

                Intent intent1 = new Intent();
                intent1.setAction("play_style");
                intent1.putExtra("position",currentPosition);
                sendBroadcast(intent1);
                stopSelf();
            }
        });

        mediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {
                return true;
            }
        });

        return super.onStartCommand(intent, flags, startId);
    }






    public class MyBinder extends Binder {



        public int getProgress() {
            return mediaPlayer.getDuration();
        }

        public void setOnPreparedListener(TimerTask mTask) {
            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mediaPlayer) {
                    //mediaPlayer.start();
                    if (mTimer == null) {
                        mTimer = new Timer();
                        mTimer.scheduleAtFixedRate(mTask, 0, mPlayerTimerDuration);
                    }
                }
            });
        }


        //设置MediaPlayer移动到的位置
        public void seekTo(int sec) {
            mediaPlayer.seekTo(sec);
        }

        //判断MediaPlayer是否为空
        public boolean isMediaPlayer() {
            if (mediaPlayer != null) {
                return true;
            } else {
                return false;
            }
        }


        //获取MediaPlayer的当前位置
        public long getCurrentPosition() {


            return mediaPlayer.getCurrentPosition();
        }

        //判断MediaPlayer是否正在播放
        public boolean isPlaying() {
            return mediaPlayer.isPlaying();
        }

        public void resetMusic() {
            mediaPlayer.reset();
        }

        public void setDataPath(String path) {
            try {
                mediaPlayer.setDataSource(path);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }



        public void playMusic() {
            /*播放音乐的函数*/
            if (mediaPlayer != null && !mediaPlayer.isPlaying()) {
                if (currentPausePositionInSong == 0) {
                    try {
                        mediaPlayer.prepare();
                        mediaPlayer.start();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    //从暂停到播放
                    mediaPlayer.seekTo(currentPausePositionInSong);
                    mediaPlayer.start();
                }

            }
        }

        public void pauseMusic() {
            //暂停音乐的函数
            if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                currentPausePositionInSong = mediaPlayer.getCurrentPosition();
                mediaPlayer.pause();

            }

        }

        public void stopMusic() {
            /*停止音乐的函数*/
            if (mediaPlayer != null) {
                currentPausePositionInSong = 0;
                mediaPlayer.pause();
                mediaPlayer.seekTo(0);
                mediaPlayer.stop();

            }
        }
    }


}
