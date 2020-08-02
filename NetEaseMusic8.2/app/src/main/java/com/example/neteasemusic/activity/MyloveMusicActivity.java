package com.example.neteasemusic.activity;

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
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.neteasemusic.R;
import com.example.neteasemusic.adapter.MusicAdapter;
import com.example.neteasemusic.db.MyDatabaseHelper;
import com.example.neteasemusic.db.Mydb;
import com.example.neteasemusic.service.MusicService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class MyloveMusicActivity extends AppCompatActivity implements View.OnClickListener {

    //创建数据库操作对象
    MyDatabaseHelper helper = new MyDatabaseHelper(this, "musicdb1", null, 1);
    final Mydb mydb = new Mydb(helper);

    Button pause;
    Button next;
    Button last;
    Button musicitem;

    ImageView songImage;
    View lastSelectView;
    int lastPosition;

    private MediaPlayer mediaPlayer;
    private MusicService.playerBinder playerBinder;
    Intent intent = new Intent();
    PlayerConnection playerConnection = new PlayerConnection();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_musicactivity);

        //创建数据库（若无）
        helper.getWritableDatabase();
        initListView();
        setStatusBar();
        Toolbar mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        //设置是否有返回箭头
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        //设置标题文字
        mToolbar.setTitle("我喜欢");

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
        pause.setOnClickListener(this);
        next.setOnClickListener(this);
        last.setOnClickListener(this);

    }

    public void initListView() {
        //显示的listView
        SimpleAdapter adapter;
        ListView listView2 = findViewById(R.id.listView);//我喜欢列表
        List<Map<String, String>> list_add = new ArrayList<>();
        List<ArrayList<String>> list2 = mydb.query();
        final ArrayList<String> name2 = list2.get(0);
        ArrayList<String> author2 = list2.get(1);
        ArrayList<String> time2 = list2.get(2);

        for (int i = 0; i < name2.size(); i++) {
            Map<String, String> map = new HashMap<>();
            map.put("name", name2.get(i));
            map.put("author", author2.get(i));
            map.put("time", time2.get(i));
            list_add.add(map);
        }
        adapter = new SimpleAdapter(MyloveMusicActivity.this,
                list_add, R.layout.mylovemusicitem, new String[]{"name", "author", "time"}, new int[]{R.id.mylove_musictitle, R.id.mylove_musicartist, R.id.mylove_musicduration});
        listView2.setAdapter(adapter);
        listView2.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                playerBinder.mediaPlay(i);
                musicitem.setText(playerBinder.getName());
                pause.setBackgroundResource(R.drawable.starting1);
            }
        });
    }

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

                if (playerBinder.getPlaytype() != 2) {
                    if (++MusicAdapter.selected > playerBinder.getSongList().size()) {
                        MusicAdapter.selected = 0;
                    }
                } else {
                    Random random = new Random();
                    MusicAdapter.selected = random.nextInt(playerBinder.getSongList().size());
                }
                if (MusicAdapter.selected != lastPosition && lastSelectView != null) {
                    lastSelectView.findViewById(R.id.isplaying).setVisibility(View.GONE);
                }
                lastPosition = MusicAdapter.selected;

                pause.setBackgroundResource(R.drawable.starting1);
                musicitem.setText(playerBinder.getName());
                songImage.setImageDrawable(playerBinder.getAlbumImg());
                break;
            case R.id.lastSong:
                playerBinder.backSong();

                if (playerBinder.getPlaytype() != 2) {
                    if (--MusicAdapter.selected < 0) {
                        MusicAdapter.selected = playerBinder.getSongList().size() - 1;
                    }
                } else {
                    MusicAdapter.selected = lastPosition;
                }
                if (MusicAdapter.selected <= 11) {
                }
                if (MusicAdapter.selected != lastPosition && lastSelectView != null) {
                    lastSelectView.findViewById(R.id.isplaying).setVisibility(View.GONE);
                }
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
        switch (item.getItemId()) {
            case R.id.sequence_play:
                playerBinder.setSequencePlay();
                Toast.makeText(MyloveMusicActivity.this, "顺序播放", Toast.LENGTH_SHORT).show();
                break;
            case R.id.random_play:
                playerBinder.setRandomPlay();
                Toast.makeText(MyloveMusicActivity.this, "随机播放", Toast.LENGTH_SHORT).show();
                break;
            case R.id.single_play:
                playerBinder.setSinglePlay();
                Toast.makeText(MyloveMusicActivity.this, "单曲循环", Toast.LENGTH_SHORT).show();
                break;
            default:
        }
        return super.onOptionsItemSelected(item);
    }

    private class PlayerConnection implements ServiceConnection {

        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            playerBinder = (MusicService.playerBinder) iBinder;
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar, menu);
        return true;
    }
}