package com.example.neteasemusic.service;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.widget.RemoteViews;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.example.neteasemusic.R;
import com.example.neteasemusic.activity.PlayerActivity;
import com.example.neteasemusic.bean.Song;
import com.example.neteasemusic.db.MyDatabaseHelper;
import com.example.neteasemusic.db.Mydb;
import com.example.neteasemusic.util.MusicUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MusicService extends Service {
    private MediaPlayer mediaPlayer;
    private IBinder binder = new playerBinder();
    List<Song> songList;
    List<Song> allSongList;
    List loveName;
    int pos = 0;
    int playType = 1;
    private RemoteViews remoteViews;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    public class playerBinder extends Binder {

        public void setSequencePlay() {
            playType = 1;
        }

        public void setRandomPlay() {
            playType = 2;
        }

        public void setSinglePlay() {
            playType = 3;
        }

        public int getPlaytype() {
            return playType;
        }

        public void autoPlay() {
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {
                    switch (playType) {
                        case 1:
                            playNextSong();
                            break;
                        case 2:
                            Random random = new Random();
                            int i = random.nextInt(songList.size());
                            setMediaPlayer(i);
                            break;
                        case 3:
                            setMediaPlayer(pos);
                            break;
                    }
                }
            });
        }


        public Drawable getAlbumImg() {
            Song song;
            if (pos == -1) {
                song = songList.get(0);
            } else {
                song = songList.get(pos);
            }
            Drawable drawable;
            drawable = new BitmapDrawable(MusicUtil.getAlbum(song.getData()));
            return drawable;
        }

        public List<Song> getSongList() {
            return songList;
        }


        public boolean getMediaState() {
            boolean flag = mediaPlayer.isPlaying();
            return flag;
        }

        public String getName() {
            String name = songList.get(pos).getTitle();
            return name;
        }

        public int getPosition() {
            return pos;
        }

        public void mediaPlay(int position) {
            pos = position;
            setMediaPlayer(position);
        }

        public void mediaPause() {
            onClickRemoteViewsSendBroad(mediaPlayer.isPlaying());
        }

        public void start() {
            mediaPlayer.start();
            onClickRemoteViewsSendBroad(mediaPlayer.isPlaying());
        }

        public void playNextSong() {
            switch (playType) {
                case 2:
                    Random random = new Random();
                    int i = random.nextInt(songList.size());
                    setMediaPlayer(i);
                    break;
                default:
                    if (++pos > songList.size() - 1) {
                        pos = 0;
                    }
                    setMediaPlayer(pos);
                    break;
            }
        }

        public void backSong() {
            if (--pos < 0) {
                pos = songList.size() - 1;
            }
            setMediaPlayer(pos);
        }
    }


//    private void pause() {
//        if (mediaPlayer.isPlaying() && mediaPlayer != null) {
//            mediaPlayer.pause();
//        }
//    }

    public void setMediaPlayer(int position) {
        ActivityManager am = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        String activity = am.getRunningTasks(1).get(0).topActivity.getClassName();
        Song song;
        if (activity.equals("com.example.neteasemusic.musicactivity.MyloveMusic")) {
            loveName = getMyLove();
            boolean flag = false;
            for (int i = 0; i < songList.size(); i++) {
                for (int j = 0; j < loveName.size(); j++) {
                    if (songList.get(i).getTitle().equals(loveName.get(j))) {
                        flag = true;
                        break;
                    }
                }
                if (!flag) {
                    songList.remove(i);
                    i--;
                }
                flag = false;
            }

            List<Song> songs = new ArrayList<>(songList);
            for (int i = 0; i < loveName.size(); i++) {
                for (int j = 0; j < songs.size(); j++) {
                    if (loveName.get(i).equals(songs.get(j).getTitle())) {
                        songList.set(i, songs.get(j));
                        break;
                    }
                }
            }
            song = songList.get(position);
        } else {
            songList = new ArrayList<>(allSongList);
            song = songList.get(position);
        }
        try {
            pos = position;

            mediaPlayer.reset();
            mediaPlayer.setDataSource(song.getData());

            mediaPlayer.prepareAsync();

            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    mediaPlayer.start();
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public ArrayList<String> getMyLove() {
        MyDatabaseHelper helper = new MyDatabaseHelper(this, "musicdb1", null, 1);
        final Mydb mydb = new Mydb(helper);
        List<ArrayList<String>> loveList = mydb.query();
        return loveList.get(0);

    }


    @Override
    public void onCreate() {
        mediaPlayer = new MediaPlayer();
        songList = MusicUtil.init(getApplicationContext());
        allSongList = new ArrayList<>(songList);
        onClickRemoteViewsSendBroad(mediaPlayer.isPlaying());
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }


    @Override
    public void onDestroy() {
        mediaPlayer.stop();
        mediaPlayer.release();
    }

    public void onClickRemoteViewsSendBroad(boolean flag) {
        initRemotesView(flag);
        Intent intentPause = new Intent("action_intent");

        intentPause.putExtra("control", 2);
        PendingIntent pre_intent = PendingIntent.getBroadcast(this, 2, intentPause, PendingIntent.FLAG_CANCEL_CURRENT);
        remoteViews.setOnClickPendingIntent(R.id.notification_last, pre_intent);

        intentPause.putExtra("control", 1);
        PendingIntent play_intent = PendingIntent.getBroadcast(this, 1, intentPause, PendingIntent.FLAG_CANCEL_CURRENT);
        remoteViews.setOnClickPendingIntent(R.id.notification_ing, play_intent);

        intentPause.putExtra("control", 3);
        PendingIntent next_intent = PendingIntent.getBroadcast(this, 3, intentPause, PendingIntent.FLAG_CANCEL_CURRENT);
        remoteViews.setOnClickPendingIntent(R.id.notification_next, next_intent);

        Notification notification = createForegroundNotification();

        startForeground(1, notification);

    }

    private Notification createForegroundNotification() {
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        String notificationChannelId = "notification_music_05";

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            String channelName = "Foreground Service Notification";

            int importance = NotificationManager.IMPORTANCE_NONE;
            NotificationChannel notificationChannel = new NotificationChannel(notificationChannelId, channelName, importance);
            notificationChannel.setDescription("Channel description");

            if (notificationManager != null) {
                notificationManager.createNotificationChannel(notificationChannel);
            }
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, notificationChannelId);

        builder.setContent(remoteViews)
                .setSmallIcon(R.drawable.icon)
                .setTicker("享受好音乐")
                .setWhen(System.currentTimeMillis());


        Intent activityIntent = new Intent(this, PlayerActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 1, activityIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(pendingIntent);

        return builder.build();
    }

    public void initRemotesView(boolean flag) {
        remoteViews = new RemoteViews(getPackageName(), R.layout.notification);
        if (flag) {
            remoteViews.setImageViewResource(R.id.notification_ing, R.drawable.player_pause);
        } else {
            remoteViews.setImageViewResource(R.id.notification_ing, R.drawable.player_ing);
        }
        remoteViews.setImageViewResource(R.id.notification_last, R.drawable.player_last);
        remoteViews.setImageViewResource(R.id.notification_next, R.drawable.player_next);
        remoteViews.setTextViewText(R.id.songname, songList.get(pos).getTitle());
        remoteViews.setTextViewText(R.id.singer, songList.get(pos).getArtist());
    }
}


