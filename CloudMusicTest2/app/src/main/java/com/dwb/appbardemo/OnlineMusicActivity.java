package com.dwb.appbardemo;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RemoteViews;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dwb.appbardemo.adapter.OnlineMusicAdapter;
import com.dwb.appbardemo.lrcface.ILrcBuilder;
import com.dwb.appbardemo.lrcface.ILrcViewListener;
import com.dwb.appbardemo.lrcface.impl.DefaultLrcBuilder;
import com.dwb.appbardemo.model.LrcRow;
import com.dwb.appbardemo.model.OnlineMusicBean;
import com.dwb.appbardemo.service.MediaService;
import com.dwb.appbardemo.utils.MusicDataUtils;
import com.dwb.appbardemo.view.LrcView;
import com.dwb.appbardemo.view.MyImageView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.TimerTask;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.UpdateListener;

public class OnlineMusicActivity extends AppCompatActivity implements View.OnClickListener {

    public final static String TAG = "OnlineMusicActivity";

    MyImageView iv_icon;
    ImageView nextIv, playIv, lastIv, playStyle,love_music,iv_back_online;
    TextView singerTv, songTv, currentProTv, totalProTv;
    RecyclerView musicRv;
    LrcView lrcView;
    View inc,inc_online,inc_love;
    //数据源
    public static List<OnlineMusicBean> mDatas = new ArrayList<>();
    private OnlineMusicAdapter adapter;
    //进度条下面的当前进度文字，将毫秒化为mm:ss格式
    @SuppressLint("SimpleDateFormat")
    private SimpleDateFormat time = new SimpleDateFormat("mm:ss");

    //创建handler对象
    Handler handler = new Handler();
    //音乐播放进度条
    SeekBar seekBar;



    //播放模式，0为列表循环，1为单曲循环,2为随机播放
    int play_style = 0;

    //设置view可见属性
    int seeFlag = 0;

    //存放播放模式的图标
    int[] imgs_playStyle = new int[]{R.drawable.list_play, R.drawable.single_play, R.drawable.random_play};

    //记录当前正在播放的音乐的位置
    int currentPlayPosition = -1;


    private MediaService.MyBinder binder;

    //“绑定”服务的intent
    Intent mediaServiceIntent;

    //当音乐播放完毕后发送广播，对其接收
    PlayMusicBroadcastReceiver broadcastReceiver;

    //接收MainActivity广播，是否在播放，用于底部导航栏同步播放暂停
    IsPlayingBroadcastReceiver isPlayingBroadcastReceiver;

    //用于接收MainActivity底部导航栏专辑图片进入歌词界面
    ShowLrcBroadcastReceiver showLrcBroadcastReceiver;

    SendBroadcastReceiver sendBroadcastReceiver;



    //通知栏定义需求
    int flag = 0; //0:播放状态,1:暂停状态
    private MyBroadCastRecever receiver;
    RemoteViews mRemoteViews;
    public static final String PLAYMUSIC_INTENT = "PLAYMUSIC_INTENT";
    public static final String PAUSEMUSIC_INTENT = "PAUSEMUSIC_INTENT";
    public static final String NEXTMUSIC_INTENT = "NEXTMUSIC_INTENT";
    public static final String PREMUSIC_INTENT = "PREMUSIC_INTENT";
    public static final String CLOSED = "CLOSED";

    //喜爱的状态
    Boolean status = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_local_music);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }



        //绑定service
        mediaServiceIntent = new Intent(this, MediaService.class);
        bindService(mediaServiceIntent, mServiceConnection, BIND_AUTO_CREATE);

        //对控件进行初始化
        initView();

        //设置两个隐藏
        inc.setVisibility(View.GONE);
        inc_love.setVisibility(View.GONE);

        //创建适配器对象
        adapter = new OnlineMusicAdapter(this, mDatas);
        musicRv.setAdapter(adapter);
        //设置布局管理器
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        musicRv.setLayoutManager(layoutManager);


        adapter.notifyDataSetChanged();
        //设置每一项的单击事件
        setEventListener();

        loveImg();

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == event.KEYCODE_BACK) {
            moveTaskToBack(true);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    //展示歌曲的定时任务
    class LrcTask extends TimerTask {
        @Override
        public void run() {
            //获取歌曲播放的位置
            final long timePassed = binder.getCurrentPosition();
            OnlineMusicActivity.this.runOnUiThread(new Runnable() {
                public void run() {
                    //滚动歌词
                    lrcView.seekLrcToTime(timePassed);
                }
            });

        }
    }

    //获取歌词
    public void initMusicLyc(String name) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                //通过歌曲名获取歌曲id
                String musicId = MusicDataUtils.getMusicId(name);
                //通过歌曲id获取歌曲歌词
                String lrc = MusicDataUtils.getMusicLyc(musicId);
                //创建解析歌词构造器
                ILrcBuilder builder = new DefaultLrcBuilder();
                //解析歌词返回LrcRow集合
                List<LrcRow> rows = builder.getLrcRows(lrc);
                //将得到的歌词集合传给mLrcView用来展示
                lrcView.setLrc(rows);
            }
        }).start();

        //设置自定义的LrcView上下拖动歌词时监听
        lrcView.setListener(new ILrcViewListener() {
            //当歌词被用户上下拖动的时候回调该方法,从高亮的那一句歌词开始播放
            public void onLrcSought(int newPosition, LrcRow row) {
                if (binder.isMediaPlayer()) {
                    Log.d(TAG, "onLrcSought:" + row.startTime);
                    binder.seekTo((int) row.startTime);
                }
            }
        });
    }





    private ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            binder = (MediaService.MyBinder) iBinder;

            //seekBar监听当前位置
            seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    //如果是手拖动，移动到对应位置
                    if (fromUser) {
                        binder.seekTo(seekBar.getProgress());
                    }
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {
                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                }
            });
            handler.post(mRunnable);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {

        }
    };
    //设置每一项的点击事件
    private void setEventListener() {
        adapter.setOnItemClickListener(new OnlineMusicAdapter.OnItemClickListener() {
            @Override
            public void OnItemClick(View view, int position) {
                love_music.setEnabled(true);
                currentPlayPosition = position;
                OnlineMusicBean musicBean = mDatas.get(position);
                playMusicInPosition(musicBean);

                BmobQuery<OnlineMusicBean> bmobQuery = new BmobQuery<>();
                Log.i("pig", "" + musicBean.getSong());
                bmobQuery.addWhereEqualTo("song", musicBean.getSong());
                bmobQuery.findObjects(new FindListener<OnlineMusicBean>() {
                    @Override
                    public void done(List<OnlineMusicBean> list, BmobException e) {
                        if (e == null) {
                            status = list.get(0).getLove();
                            Log.i("pig2", "" + "" + status);
                            if (!status) {
                                love_music.setImageResource(R.drawable.music_love);
                            } else {
                                love_music.setImageResource(R.drawable.music_loved);
                            }
                        }
                    }
                });

                mediaServiceIntent.putExtra("currentPosition", currentPlayPosition);
                startService(mediaServiceIntent);

            }
        });
    }

    private class PlayMusicBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            currentPlayPosition = intent.getIntExtra("position", 0);
            Log.i(TAG, "onReceive: "+currentPlayPosition);
            playMusicInPosition(mDatas.get(currentPlayPosition));
        }
    }


    //接收MainActivity广播，是否在播放，用于底部导航栏同步播放暂停
    private class IsPlayingBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            boolean isPlaying = intent.getBooleanExtra("isPlaying", false);
            if (isPlaying) {
                //此时处于播放状态，需要暂停音乐
                binder.pauseMusic();
                iv_icon.stopImg();
                playIv.setImageResource(R.drawable.icon_play);
                flag = 1;
            } else {
                //此时没有播放音乐，点击开始播放音乐
                binder.playMusic();
                iv_icon.rotateImg();
                playIv.setImageResource(R.drawable.icon_pause);
                flag = 0;
            }
            wakeNotification();
        }
    }

    //用于接收MainActivity底部导航栏专辑图片进入歌词界面
    private class ShowLrcBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            lrcView.setVisibility(View.VISIBLE);
            musicRv.setVisibility(View.GONE);
        }
    }


    public void playMusicInPosition(OnlineMusicBean musicBean) {
        //暂停音乐
        binder.stopMusic();
        //重置多媒体播放器
        binder.resetMusic();
        //设置底部显示的歌名和歌手名
        singerTv.setText(musicBean.getSinger());
        songTv.setText(musicBean.getSong());

        //获取音乐总时长
        totalProTv.setText(musicBean.getDuration());
        //设置播放按钮的图片
        playIv.setImageResource(R.drawable.icon_play);
        //音乐准备时监听
        binder.setOnPreparedListener(new LrcTask());
        //设置新的播放路径
        binder.setDataPath(musicBean.getPath());
        //播放音乐
        binder.playMusic();
        //音乐播放后，设置图片为暂停
        playIv.setImageResource(R.drawable.icon_pause);
        //初始化歌词
        initMusicLyc(musicBean.getSong());
        iv_icon.rotateImg();

        //唤醒通知栏
        wakeNotification();
        //传递给MainActivity数据
        sendMessageToMainActivity(musicBean);
    }



    private void sendMessageToMainActivity(OnlineMusicBean musicBean) {
        Intent intent1 = new Intent();
        intent1.putExtra("song", musicBean.getSong());
        intent1.putExtra("author", musicBean.getSinger());
        intent1.putExtra("isPlaying", binder.isPlaying());

        intent1.setAction("sendMessageToMainActivity");

        sendBroadcast(intent1);
    }




    @Override
    protected void onDestroy() {
        //解绑service
        unbindService(mServiceConnection);
        //删除指定的Runnable对象，使线程对象停止运行
        handler.removeCallbacks(mRunnable);
        //解除广播接收者
        unregisterReceiver(broadcastReceiver);
        super.onDestroy();


    }


    private void initView() {
        love_music = findViewById(R.id.love_click);
        nextIv = findViewById(R.id.local_music_bottom_iv_next);
        playIv = findViewById(R.id.local_music_bottom_iv_play);
        lastIv = findViewById(R.id.local_music_bottom_iv_last);
        singerTv = findViewById(R.id.local_music_bottom_tv_singer);
        songTv = findViewById(R.id.local_music_bottom_tv_song);
        musicRv = findViewById(R.id.local_music_rv);
        iv_back_online = findViewById(R.id.iv_back_online);
        playStyle = findViewById(R.id.playpattern);
        iv_icon = findViewById(R.id.local_music_bottom_iv_icon);
        lrcView = findViewById(R.id.lrcView);
        seekBar = findViewById(R.id.seekBar);
        currentProTv = findViewById(R.id.currentProgress);
        totalProTv = findViewById(R.id.totalProgress);
        inc = findViewById(R.id.inc);
        inc_online = findViewById(R.id.inc_online);
        inc_love = findViewById(R.id.inc_love);
        iv_icon.setOnClickListener(this);
        playStyle.setOnClickListener(this);
        nextIv.setOnClickListener(this);
        lastIv.setOnClickListener(this);
        playIv.setOnClickListener(this);
        iv_back_online.setOnClickListener(this);
        iv_icon.stopImg();

        //创建广播接收者,捕获play_style动作
        broadcastReceiver = new PlayMusicBroadcastReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction("play_style");
        registerReceiver(broadcastReceiver, filter);


    }

    private class SendBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {

            String songText = intent.getStringExtra("song");
            String authorText = intent.getStringExtra("author");
            String duration = intent.getStringExtra("duration");
            boolean isPlaying = intent.getBooleanExtra("isPlaying",false);

            songTv.setText(songText);
            singerTv.setText(authorText);
            if (isPlaying){
                playIv.setImageResource(R.drawable.icon_pause);
            }else {
                playIv.setImageResource(R.drawable.icon_play);
            }

        }
    }

    @Override
    protected void onResume() {

        //创建广播接收者,捕获sendMessageToMainActivity动作
        sendBroadcastReceiver = new SendBroadcastReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction("sendMessageToMainActivity");
        registerReceiver(sendBroadcastReceiver, filter);


        isPlayingBroadcastReceiver = new IsPlayingBroadcastReceiver();
        IntentFilter isPlaying = new IntentFilter();
        isPlaying.addAction("isPlaying");
        registerReceiver(isPlayingBroadcastReceiver, isPlaying);

        showLrcBroadcastReceiver = new ShowLrcBroadcastReceiver();
        IntentFilter showLrc = new IntentFilter();
        showLrc.addAction("showLrc");
        registerReceiver(showLrcBroadcastReceiver,showLrc);



        super.onResume();
    }



    //点击上一首按钮时，获取上一首音乐的位置
    public int preMusic(int currentPlayPosition) {

        //当前播放位置是第一首时，点击上一曲播放最后一首音乐，当是其他情况时，播放当前的上一首。
        if (currentPlayPosition == 0) {
            currentPlayPosition = mDatas.size() - 1;
        } else {
            currentPlayPosition = currentPlayPosition - 1;
        }
        return currentPlayPosition;
    }

    //点击下一首按钮时，获取下一首音乐的位置
    public int nextMusic(int currentPlayPosition) {

        if (currentPlayPosition == mDatas.size() - 1) {
            currentPlayPosition = 0;
        } else {
            currentPlayPosition = currentPlayPosition + 1;
        }
        return currentPlayPosition;

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            //歌词界面显示与否
            case R.id.local_music_bottom_iv_icon:
                if (seeFlag == 0) {
                    lrcView.setVisibility(View.VISIBLE);
                    musicRv.setVisibility(View.GONE);
                    seeFlag = 1;
                } else if (seeFlag == 1) {
                    lrcView.setVisibility(View.GONE);
                    musicRv.setVisibility(View.VISIBLE);
                    seeFlag = 0;
                }
                break;
            //顶部栏返回键
            case R.id.iv_back_online:
                Toast.makeText(this, "点击返回了", Toast.LENGTH_SHORT).show();
                //将activity退到后台,并不是finish()退出
                moveTaskToBack(true);
                break;
            //上一曲按钮
            case R.id.local_music_bottom_iv_last:
                if (currentPlayPosition == -1) {
                    Toast.makeText(this, "请选择要播放的音乐", Toast.LENGTH_SHORT).show();
                } else {
                    currentPlayPosition = preMusic(currentPlayPosition);
                    playMusicInPosition(mDatas.get(currentPlayPosition));
                }
                break;

            //下一曲按钮
            case R.id.local_music_bottom_iv_next:
                if (currentPlayPosition == -1) {
                    Toast.makeText(this, "请选择要播放的音乐", Toast.LENGTH_SHORT).show();
                } else {
                    currentPlayPosition = nextMusic(currentPlayPosition);
                    playMusicInPosition(mDatas.get(currentPlayPosition));
                }
                break;
            //切换播放暂停按钮
            case R.id.local_music_bottom_iv_play:
                if (currentPlayPosition == -1) {
                    //并没有选中要播放的音乐
                    Toast.makeText(this, "请选择要播放的音乐", Toast.LENGTH_SHORT).show();
                } else if (binder.isPlaying()) {
                    //此时处于播放状态，需要暂停音乐
                    binder.pauseMusic();
                    iv_icon.stopImg();
                    playIv.setImageResource(R.drawable.icon_play);
                    flag = 1;
                    sendMessageToMainActivity(mDatas.get(currentPlayPosition));
                    wakeNotification();
                } else {
                    //此时没有播放音乐，点击开始播放音乐
                    binder.playMusic();
                    iv_icon.rotateImg();
                    playIv.setImageResource(R.drawable.icon_pause);
                    flag = 0;
                    sendMessageToMainActivity(mDatas.get(currentPlayPosition));
                    wakeNotification();
                }

                break;
            //点击切换播放模式（循环0，单曲1，随机2）
            case R.id.playpattern:
                play_style += 1;
                if (play_style > 2) {
                    play_style = 0;
                }
                if (play_style == 0){
                    Toast.makeText(this, "循环播放", Toast.LENGTH_SHORT).show();
                }else if (play_style == 1){
                    Toast.makeText(this, "单曲播放", Toast.LENGTH_SHORT).show();
                }else if (play_style == 2){
                    Toast.makeText(this, "随机播放", Toast.LENGTH_SHORT).show();
                }
                mediaServiceIntent.putExtra("play_style", play_style);
                mediaServiceIntent.putExtra("currentPosition", currentPlayPosition);
                startService(mediaServiceIntent);
                playStyle.setImageResource(imgs_playStyle[play_style]);
                break;
        }
    }


    /**
     * 更新ui的runnable
     */
    private Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            //设置seekBar最大值
            seekBar.setMax(binder.getProgress());
            //判断当前是否选择歌曲
            if (currentPlayPosition == -1) {
                seekBar.setProgress(0);
                currentProTv.setText("00:00");
            } else {
                seekBar.setProgress((int) binder.getCurrentPosition());
                currentProTv.setText(time.format(binder.getCurrentPosition()));
            }
            //１秒钟
            handler.postDelayed(mRunnable, 1000);

        }
    };


    public class Notification_Controller {
        public NotificationManager manager;
        public Context context;


        //带参构造，获取manager
        public Notification_Controller(Context context) {
            this.context = context;
            manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        }

        public void showNotification() {
            String channelId = "notification_controller";
            NotificationCompat.Builder builder = new NotificationCompat.Builder(context, channelId);
            mRemoteViews = new RemoteViews(context.getPackageName(), R.layout.activity_notification);

            if (flag == 0) {
                mRemoteViews.setImageViewResource(R.id.play, R.drawable.play);
            } else {
                mRemoteViews.setImageViewResource(R.id.play, R.drawable.pause);
            }

            notificationToActivity(mRemoteViews, flag);

            mRemoteViews.setTextViewText(R.id.song, mDatas.get(currentPlayPosition).getSong());
            mRemoteViews.setTextViewText(R.id.author, mDatas.get(currentPlayPosition).getSinger());
            NotificationChannel channel = new NotificationChannel(channelId, "controller", NotificationManager.IMPORTANCE_HIGH);
            manager.createNotificationChannel(channel);
            builder.setSmallIcon(R.drawable.cloudmusic_icon)    //设置通知小图标
                    .setWhen(System.currentTimeMillis())   //设置通知时间
                    .setCustomBigContentView(mRemoteViews)   //让通知栏大些，防止遮住控件
                    .setCustomContentView(mRemoteViews)   //设置通知栏显示的格式内容
                    .setPriority(NotificationCompat.PRIORITY_HIGH) //设置优先级为1
                    .setOngoing(true)   //设置他为一个正在进行的通知。他们通常是用来表示一个后台任务,用户积极参与(如播放音乐)或以某种方式正在等待,因此占用设备(如一个文件下载,同步操作,主动网络连接)
                    .setChannelId(channel.getId())  //Android 8.0需要创建通道来实现通知栏
                    .build(); //创建通知完成对象
            //唤醒通知栏
            manager.notify(10, builder.build());
        }

    }

    //通知栏交互活动
    private void notificationToActivity(RemoteViews remoteView, int flag) {

        Intent intent = new Intent();
        Intent intentP = new Intent();
        Intent intentN = new Intent();
        Intent intentC = new Intent();

        intentP.setAction(PREMUSIC_INTENT);
        intentN.setAction(NEXTMUSIC_INTENT);
        intentC.setAction(CLOSED);
        if (flag == 0) {//正在播放音乐
            //试图暂停播放音乐
            intent.setAction(PAUSEMUSIC_INTENT);
            //更改图片
            remoteView.setImageViewResource(R.id.play, R.drawable.pause);

        } else {//没有播放音乐
            //试图播放音乐
            intent.setAction(PLAYMUSIC_INTENT);
            //更改图片
            remoteView.setImageViewResource(R.id.play, R.drawable.play);

        }

        //发送广播，更新Activity
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        PendingIntent pendingIntentP = PendingIntent.getBroadcast(this, 0, intentP, PendingIntent.FLAG_UPDATE_CURRENT);
        PendingIntent pendingIntentN = PendingIntent.getBroadcast(this, 0, intentN, PendingIntent.FLAG_UPDATE_CURRENT);
        PendingIntent pendingIntentC = PendingIntent.getBroadcast(this, 0, intentC, PendingIntent.FLAG_UPDATE_CURRENT);


        remoteView.setOnClickPendingIntent(R.id.play, pendingIntent);
        remoteView.setOnClickPendingIntent(R.id.pre, pendingIntentP);
        remoteView.setOnClickPendingIntent(R.id.next, pendingIntentN);
        remoteView.setOnClickPendingIntent(R.id.close, pendingIntentC);

    }

    //广播接收器
    private class MyBroadCastRecever extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {

            switch (intent.getAction()) {
                case PLAYMUSIC_INTENT:
                    //播放音乐
                    binder.playMusic();
                    //是否播放正在播放音乐标志
                    flag = 0;
                    //更改图片标志
                    playIv.setImageResource(R.drawable.icon_pause);
                    break;
                case PAUSEMUSIC_INTENT:
                    //暂停音乐
                    binder.pauseMusic();
                    //是否播放正在播放音乐标志
                    flag = 1;
                    //更改图片标志
                    playIv.setImageResource(R.drawable.icon_play);
                    break;

                case NEXTMUSIC_INTENT:
                    if (currentPlayPosition == mDatas.size() - 1) {
                        Toast.makeText(context, "已经是最后一首了，无法切换下一首！", Toast.LENGTH_SHORT).show();
                        return;
                    } else {
                        currentPlayPosition = currentPlayPosition + 1;
                        OnlineMusicBean nextBean = mDatas.get(currentPlayPosition);
                        playMusicInPosition(nextBean);
                        flag = 0;
                        break;
                    }
                case PREMUSIC_INTENT:
                    if (currentPlayPosition == 0) {
                        Toast.makeText(context, "已经是第一首了，无法切换上一首！", Toast.LENGTH_SHORT).show();
                        return;
                    } else {
                        currentPlayPosition = currentPlayPosition - 1;
                        OnlineMusicBean lastBean = mDatas.get(currentPlayPosition);
                        playMusicInPosition(lastBean);
                        flag = 0;
                    }
                    break;
            }

            final Notification_Controller notificationController = new Notification_Controller(OnlineMusicActivity.this);
            notificationController.showNotification();

            if (intent.getAction() == CLOSED) {

                NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

                manager.cancel(10);

            }

        }

    }

    // 唤醒通知栏
    private void wakeNotification() {
        //唤醒通知栏
        //展示通知栏
        final Notification_Controller notificationController = new Notification_Controller(OnlineMusicActivity.this);

        notificationController.showNotification();

        //注冊接收通知栏发送的广播事件
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(PAUSEMUSIC_INTENT);
        intentFilter.addAction(PLAYMUSIC_INTENT);
        intentFilter.addAction(NEXTMUSIC_INTENT);
        intentFilter.addAction(PREMUSIC_INTENT);
        intentFilter.addAction(CLOSED);

        if (null == receiver) {
            receiver = new MyBroadCastRecever();
        }

        registerReceiver(receiver, intentFilter);
    }

    public void loveImg() {

        if (currentPlayPosition ==-1){
            love_music.setEnabled(false);
        }

        love_music.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                OnlineMusicBean musicBean = mDatas.get(currentPlayPosition);
                BmobQuery<OnlineMusicBean> bmobQuery = new BmobQuery<>();
                Log.i("pig", "" + musicBean.getSong());
                bmobQuery.addWhereEqualTo("song", musicBean.getSong());
                bmobQuery.findObjects(new FindListener<OnlineMusicBean>() {
                    @Override
                    public void done(List<OnlineMusicBean> list, BmobException e) {
                        if (e == null) {
                            status = list.get(0).getLove();
                            Log.i("pig1", "" + status);
                            String id = list.get(0).getObjectId();
                            if (status == true) {
                                love_music.setImageResource(R.drawable.music_love);
                                musicBean.setLove(false);
                                musicBean.update(id, new UpdateListener() {
                                    @Override
                                    public void done(BmobException e) {

                                    }
                                });
                            } else {
                                love_music.setImageResource(R.drawable.music_loved);
                                musicBean.setLove(true);
                                musicBean.update(id, new UpdateListener() {
                                    @Override
                                    public void done(BmobException e) {

                                    }
                                });
                            }
                        }
                    }
                });
            }
        });
    }

}