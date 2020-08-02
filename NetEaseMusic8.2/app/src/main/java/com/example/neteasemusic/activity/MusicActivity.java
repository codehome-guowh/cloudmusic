package com.example.neteasemusic.activity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;


import com.example.neteasemusic.R;
import com.example.neteasemusic.db.MyDatabaseHelper;
import com.example.neteasemusic.db.Mydb;
import com.example.neteasemusic.adapter.MusicAdapter;
import com.example.neteasemusic.service.MusicService;
import com.example.neteasemusic.util.MusicUtil;
import com.example.neteasemusic.bean.Song;

import java.util.List;
import java.util.Random;

public class MusicActivity extends AppCompatActivity implements View.OnClickListener {

    private MediaPlayer mediaPlayer;
    private MusicService.playerBinder playerBinder;
    Intent intent = new Intent();
    PlayerConnection playerConnection = new PlayerConnection();
    List<Song> songList;
    Button pause;
    Button next;
    Button last;
    Button musicitem;
    ImageView songImage;
    View lastSelectView;
    int lastPosition;
    ListView listView;

    MyDatabaseHelper helper = new MyDatabaseHelper(this, "musicdb1", null, 1);
    final Mydb mydb = new Mydb(helper);

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_musicactivity);
        setStatusBar();
        Toolbar mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        //设置是否有返回箭头
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        //设置标题文字
        mToolbar.setTitle("我的音乐");


        //连接service
        intent.setClass(this, MusicService.class);
        startService(intent);
        bindService(intent, playerConnection, BIND_AUTO_CREATE);


        //初始化
        mediaPlayer = new MediaPlayer();
        pause = findViewById(R.id.startmusic);
        next = findViewById(R.id.nextSong);
        last = findViewById(R.id.lastSong);
        musicitem = findViewById(R.id.musicitem);
        songImage = findViewById(R.id.SongImage);
        pause.setOnClickListener(this);
        next.setOnClickListener(this);
        last.setOnClickListener(this);


    }

    //设置每个按钮的单击事件
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.startmusic:
                if (playerBinder.getMediaState()) {
                    playerBinder.mediaPause();
                    pause.setBackgroundResource(R.drawable.stopping1);
                    break;
                }
                if (playerBinder.getPosition() == -1) {
                    playerBinder.mediaPlay(0);
                    pause.setBackgroundResource(R.drawable.starting1);
                    songImage.setImageDrawable(playerBinder.getAlbumImg());
                    break;
                } else {
                    playerBinder.start();
                    pause.setBackgroundResource(R.drawable.starting1);
                }
                break;
            case R.id.nextSong:
                playerBinder.playNextSong();

                //设置在点击下一首歌时，listview中将正在播放的歌曲前的图片显示
                if (playerBinder.getPlaytype() != 2) {     //判断是否是随机播放状态
                    if (++MusicAdapter.selected > playerBinder.getSongList().size()) {
                        MusicAdapter.selected = 0;
                    }
                } else {
                    Random random = new Random();
                    MusicAdapter.selected = random.nextInt(playerBinder.getSongList().size());
                }
                if (MusicAdapter.selected <= 11) {
                    listView.getChildAt(MusicAdapter.selected).findViewById(R.id.isplaying).setVisibility(View.VISIBLE);
                }
                if (MusicAdapter.selected != lastPosition && lastSelectView != null) {
                    lastSelectView.findViewById(R.id.isplaying).setVisibility(View.GONE);
                }
                lastSelectView = listView.getChildAt(MusicAdapter.selected);
                lastPosition = MusicAdapter.selected;

                pause.setBackgroundResource(R.drawable.starting1);
                musicitem.setText(playerBinder.getName());
                songImage.setImageDrawable(playerBinder.getAlbumImg());
                break;
            case R.id.lastSong:
                playerBinder.backSong();

                //设置在点击下一首歌时，listview中将正在播放的歌曲前的图片显示
                if (playerBinder.getPlaytype() != 2) {     //判断是否是随机播放状态
                    if (--MusicAdapter.selected < 0) {
                        MusicAdapter.selected = playerBinder.getSongList().size() - 1;
                    }
                } else {
                    MusicAdapter.selected = lastPosition;
                }
                if (MusicAdapter.selected <= 11) {
                    listView.getChildAt(MusicAdapter.selected).findViewById(R.id.isplaying).setVisibility(View.VISIBLE);
                }
                if (MusicAdapter.selected != lastPosition && lastSelectView != null) {
                    lastSelectView.findViewById(R.id.isplaying).setVisibility(View.GONE);
                }
                lastSelectView = listView.getChildAt(MusicAdapter.selected);
                lastPosition = MusicAdapter.selected;


                pause.setBackgroundResource(R.drawable.starting1);
                musicitem.setText(playerBinder.getName());
                songImage.setImageDrawable(playerBinder.getAlbumImg());
                break;
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
        }
        unbindService(playerConnection);
    }


    private class PlayerConnection implements ServiceConnection {

        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            playerBinder = (MusicService.playerBinder) iBinder;
            songList = playerBinder.getSongList();

            //设置listiew
            listView = findViewById(R.id.listView);
            MusicAdapter adapter = new MusicAdapter(MusicActivity.this, songList);
            listView.setAdapter(adapter);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    playerBinder.mediaPlay(i);
                    musicitem.setText(playerBinder.getName());
                    pause.setBackgroundResource(R.drawable.starting1);
                    songImage.setImageDrawable(playerBinder.getAlbumImg());

                    MusicAdapter.selected = i;
                    if (MusicAdapter.selected == i) {
                        view.findViewById(R.id.isplaying).setVisibility(View.VISIBLE);
                    }
                    if (i != lastPosition && lastSelectView != null) {
                        lastSelectView.findViewById(R.id.isplaying).setVisibility(View.GONE);
                    }
                    lastSelectView = view;
                    lastPosition = i;
                }
            });

            musicitem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(MusicActivity.this, PlayerActivity.class);
                    startActivity(intent);
                }
            });

            //我喜欢按钮点击事件
            adapter.setOnItemLoveButtonListener(new MusicAdapter.onItemLoveButtonListener() {
                @Override
                public void onLoveClick(int i) {

                    ConstraintLayout constraintLayout = (ConstraintLayout) getViewByPosition(i, listView);//获取listview中的一个item的布局
                    ImageButton imageButton = (ImageButton) constraintLayout.findViewById(R.id.love);//在该小布局中找到控件
                    boolean isLove = mydb.isLoved(songList.get(i).getTitle());
                    if (!isLove) {
                        mydb.add(songList.get(i).getTitle(), songList.get(i).getArtist(), MusicUtil.formatTime(songList.get(i).getDuration()));
                        Toast.makeText(MusicActivity.this, "已喜欢", Toast.LENGTH_SHORT).show();
                        imageButton.setBackgroundResource(R.drawable.love);
                    } else {
                        mydb.delete(songList.get(i).getTitle());
                        Toast.makeText(MusicActivity.this, "已取消喜欢", Toast.LENGTH_SHORT).show();
                        imageButton.setBackgroundResource(R.drawable.notlove);
                    }

                }
            });


            if (playerBinder.getMediaState()) {
                pause.setBackgroundResource(R.drawable.starting1);
                songImage.setImageDrawable(playerBinder.getAlbumImg());
                musicitem.setText(playerBinder.getName());
            } else {
                pause.setBackgroundResource(R.drawable.stopping1);
                if (playerBinder.getPosition() != -1) {
                    songImage.setImageDrawable(playerBinder.getAlbumImg());
                }
            }
            playerBinder.autoPlay();
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {

        }
    }

    protected void setStatusBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);//隐藏状态栏但不隐藏状态栏字体
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);//实现状态栏图标和文字颜色为暗色
        }
    }

    //获取listview中的控件
    private View getViewByPosition(int pos, ListView listView) {
        final int firstListItemPosition = listView.getFirstVisiblePosition();
        final int lastListItemPosition = firstListItemPosition + listView.getChildCount() - 1;

        if (pos < firstListItemPosition || pos > lastListItemPosition) {
            return listView.getAdapter().getView(pos, null, listView);
        } else {
            final int childIndex = pos - firstListItemPosition;
            return listView.getChildAt(childIndex);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        //监听左上角的返回箭头
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        switch (item.getItemId()) {
            case R.id.sequence_play:
                playerBinder.setSequencePlay();
                Toast.makeText(MusicActivity.this, "顺序播放", Toast.LENGTH_SHORT).show();
                break;
            case R.id.random_play:
                playerBinder.setRandomPlay();
                Toast.makeText(MusicActivity.this, "随机播放", Toast.LENGTH_SHORT).show();
                break;
            case R.id.single_play:
                playerBinder.setSinglePlay();
                Toast.makeText(MusicActivity.this, "单曲循环", Toast.LENGTH_SHORT).show();
                break;
            default:
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar, menu);
        return true;
    }

}