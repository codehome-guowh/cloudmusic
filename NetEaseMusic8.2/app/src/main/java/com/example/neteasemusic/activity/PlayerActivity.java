package com.example.neteasemusic.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.example.neteasemusic.R;
import com.example.neteasemusic.service.MusicService;


public class PlayerActivity extends AppCompatActivity implements View.OnClickListener {

    ImageView iv_play, iv_needle, player_ing, player_last, player_next;
    Animation mPlayMusicAnim, mPlayNeedleAnim, mStopNeedleAnim;
    private MediaPlayer mediaPlayer;
    private MusicService.playerBinder playerBinder;
    Toolbar mToolbar;
    private MyReceiver serviceReceiver;
    private static final String ACTION_INTENT = "action_intent";

    PlayerConnection playerConnection = new PlayerConnection();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

        setStatusBar();
        mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        mToolbar.setTitle("我的音乐");

        iv_play = findViewById(R.id.iv_play);
        iv_needle = findViewById(R.id.iv_needle);
        player_ing = findViewById(R.id.player_ing);
        player_last = findViewById(R.id.player_last);
        player_next = findViewById(R.id.player_next);

        mediaPlayer = new MediaPlayer();

        player_ing.setOnClickListener(this);
        player_last.setOnClickListener(this);
        player_next.setOnClickListener(this);

        mPlayMusicAnim = AnimationUtils.loadAnimation(this, R.anim.play_music_anim);
        mPlayNeedleAnim = AnimationUtils.loadAnimation(this, R.anim.play_needle_anim);
        mStopNeedleAnim = AnimationUtils.loadAnimation(this, R.anim.stop_needle_anim);

        Intent intent = getIntent();
        intent.setClass(this, MusicService.class);
        startService(intent);
        bindService(intent, playerConnection, BIND_AUTO_CREATE);

        boolean musicstatus = intent.getBooleanExtra("musicstatus", true);
        if (musicstatus) {
            player_ing.setImageResource(R.drawable.player_ing);
            iv_play.startAnimation(mPlayMusicAnim);
            iv_needle.startAnimation(mPlayNeedleAnim);

        } else {
            player_ing.setImageResource(R.drawable.player_pause);
            iv_play.clearAnimation();
            iv_needle.startAnimation(mStopNeedleAnim);
        }

        initMyReceive();
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {

            case R.id.player_ing:
                if (playerBinder.getMediaState()) {
                    playerBinder.mediaPause();
                    player_ing.setImageResource(R.drawable.player_pause);
                    iv_play.clearAnimation();
                    iv_needle.startAnimation(mStopNeedleAnim);
                    break;
                }
                if (playerBinder.getPosition() == -1) {
                    playerBinder.mediaPlay(0);
                    player_ing.setImageResource(R.drawable.player_ing);
                    iv_play.clearAnimation();
                    iv_needle.startAnimation(mStopNeedleAnim);
                    break;
                } else {
                    playerBinder.start();
                    player_ing.setImageResource(R.drawable.player_ing);
                    iv_play.startAnimation(mPlayMusicAnim);
                    iv_needle.startAnimation(mPlayNeedleAnim);
                }
                break;
            case R.id.player_next:
                playerBinder.playNextSong();
                player_ing.setImageResource(R.drawable.player_ing);
                break;
            case R.id.player_last:
                playerBinder.backSong();
                player_ing.setImageResource(R.drawable.player_ing);
                break;
        }
    }

    private class PlayerConnection implements ServiceConnection {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            playerBinder = (MusicService.playerBinder) iBinder;
            if (playerBinder.getMediaState()) {
                player_ing.setImageResource(R.drawable.player_ing);

            } else {
                player_ing.setImageResource(R.drawable.player_pause);
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {

        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(playerConnection);
    }

    private void initMyReceive() {
        serviceReceiver = new MyReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_INTENT);
        registerReceiver(serviceReceiver, filter);
    }

    public class MyReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(final Context context, Intent intent) {
            int control = intent.getIntExtra("control", -1);
            switch (control) {
                case 1:
                    playerBinder.mediaPause();
                    break;
                case 2:
                    playerBinder.backSong();
                    break;
                case 3:
                    playerBinder.playNextSong();
                    break;
            }

        }

    }

    protected void setStatusBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);//隐藏状态栏但不隐藏状态栏字体
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);//实现状态栏图标和文字颜色为暗色
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}