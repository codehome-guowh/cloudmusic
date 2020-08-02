package com.dwb.appbardemo;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import androidx.viewpager.widget.ViewPager;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;

import android.os.Build;
import android.os.Bundle;

import android.view.Gravity;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.dwb.appbardemo.adapter.FragmentAdapter;
import com.dwb.appbardemo.fragment.DiscoverFragment;
import com.dwb.appbardemo.fragment.MineFragment;
import com.dwb.appbardemo.fragment.VideoFragment;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobUser;


public class MainActivity extends AppCompatActivity {
    TabLayout tabLayout;
    ViewPager viewPager;
    NavigationView navigationView;
    ImageView imageView, login, headImage, playImage, searchMusic;
    DrawerLayout drawerLayout;
    List<Fragment> fragments;
    TextView login_status, song, author, name;
    SendBroadcastReceiver sendBroadcastReceiver;
    Toolbar toolbar;
    boolean isPlaying = false;


    private class SendBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            toolbar.setVisibility(View.VISIBLE);
            String songText = intent.getStringExtra("song");
            String authorText = intent.getStringExtra("author");
            String albumPicUrl = intent.getStringExtra("albumPicUrl");
            Glide.with(context).load(albumPicUrl).into(headImage);
            isPlaying = intent.getBooleanExtra("isPlaying", false);
            song.setText(songText);
            author.setText(authorText);
            if (isPlaying) {
                playImage.setImageResource(R.drawable.icon_pause);
            } else {
                playImage.setImageResource(R.drawable.icon_play);
            }

        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }


        //初始化
        initView();
        initFragment();
        toolbar = findViewById(R.id.bottom_toolbar);
        toolbar.setVisibility(View.GONE);

        //给左上角按钮点击事件，点击弹出侧边栏
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawerLayout.openDrawer(Gravity.LEFT);
            }
        });


        //通过适配器为viewPaper添加数据
        viewPager.setAdapter(new FragmentAdapter(getSupportFragmentManager(), fragments));
        //tabLayout与viewPaper关联
        tabLayout.setupWithViewPager(viewPager);

        //动态给予权限
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        }
    }

    //初始化组件
    public void initView() {
        navigationView = findViewById(R.id.nav);
        searchMusic = findViewById(R.id.search_music);

        View headerView = navigationView.getHeaderView(0);
        login = headerView.findViewById(R.id.login_img);
        login_status = headerView.findViewById(R.id.login_text);
        name = headerView.findViewById(R.id.head_name);
        //让侧边栏图标着色
        navigationView.setItemIconTintList(null);

        headImage = findViewById(R.id.head_image);
        playImage = findViewById(R.id.play_image);
        song = findViewById(R.id.song);
        author = findViewById(R.id.author);

        tabLayout = findViewById(R.id.tabs);
        viewPager = findViewById(R.id.view);
        imageView = findViewById(R.id.imageView7);
        drawerLayout = findViewById(R.id.drawerlayout);


    }

    @Override
    protected void onResume() {
        //创建广播接收者,捕获sendMessageToMainActivity动作
        sendBroadcastReceiver = new SendBroadcastReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction("sendMessageToMainActivity");
        registerReceiver(sendBroadcastReceiver, filter);

        //播放按钮点击事件
        playImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isPlaying) {
                    playImage.setImageResource(R.drawable.icon_pause);
                    isPlaying = false;
                } else {
                    playImage.setImageResource(R.drawable.icon_play);
                    isPlaying = true;
                }
                Intent intent = new Intent();
                intent.putExtra("isPlaying", isPlaying);
                intent.setAction("isPlaying");
                sendBroadcast(intent);
            }
        });

        headImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent1 = new Intent();
                intent1.setAction("showLrc");
                sendBroadcast(intent1);
                Intent intent2 = new Intent(MainActivity.this, LocalMusicActivity.class);
                startActivity(intent2);
            }
        });

        searchMusic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, SearchMusicActivity.class);
                startActivity(intent);
            }
        });

        super.onResume();
    }


    //初始化fragment
    public void initFragment() {
        fragments = new ArrayList<>();
        fragments.add(new MineFragment());
        fragments.add(new DiscoverFragment());
        fragments.add(new VideoFragment());
    }

}