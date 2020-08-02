package com.example.neteasemusic.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.Manifest;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.neteasemusic.fragment.MyFragmentFind;
import com.example.neteasemusic.fragment.MyFragmentMy;
import com.example.neteasemusic.fragment.MyFragmentVideo;
import com.example.neteasemusic.R;
import com.example.neteasemusic.view.SimpleVideoView;
import com.example.neteasemusic.service.MusicService;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    TabLayout tabLayout;
    ViewPager viewPager;
    NavigationView navigationView;
    Button btn;
    Button start;
    Button next;
    Button last;
    Button musicitem1;
    ImageView songImage;
    DrawerLayout drawer;
    String[] title = {"我的","发现","视频"};
    List<Fragment> fragments;
    private MusicService.playerBinder playerBinder;
    private SimpleVideoView video;
    Intent intent = new Intent();
    PlayerConnection playerConnection = new PlayerConnection();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setStatusBar();
        //申请权限
        if (ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{
                    Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        } else {
        }


        //连接service
        intent.setClass(this, MusicService.class);
        startService(intent);
        bindService(intent, playerConnection, BIND_AUTO_CREATE);

        start = findViewById(R.id.startmusic1);
        next = findViewById(R.id.nextSong1);
        last = findViewById(R.id.lastSong1);
        musicitem1 = findViewById(R.id.musicitem1);
        songImage = findViewById(R.id.SongImage1);
        start.setOnClickListener(this);
        next.setOnClickListener(this);
        last.setOnClickListener(this);

        navigationView = findViewById(R.id.nav);
        navigationView.setItemIconTintList(null);
        drawer = findViewById(R.id.drawer);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Toast.makeText(MainActivity.this, item.getTitle().toString(), Toast.LENGTH_SHORT).show();
                return true;
            }
        });

        tabLayout = findViewById(R.id.TabLayout);
        viewPager = findViewById(R.id.ViewPager);
        initFragment();
        viewPager.setAdapter(new MyAdapter(getSupportFragmentManager(), fragments));
        viewPager.setCurrentItem(1);
        tabLayout.setupWithViewPager(viewPager);
        btn = findViewById(R.id.btn);
        video = ((MyFragmentVideo) fragments.get(2)).getVideo();
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawer.openDrawer(Gravity.LEFT);
            }
        });

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                } else {
                    Toast.makeText(MainActivity.this, "没有权限", Toast.LENGTH_SHORT).show();
                    finish();
                }
                break;
        }
    }

    private void initFragment() {
        fragments = new ArrayList<>();
        fragments.add(new MyFragmentMy());
        fragments.add(new MyFragmentFind());
        fragments.add(new MyFragmentVideo());

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.startmusic1:
                if (playerBinder.getMediaState()) {
                    playerBinder.mediaPause();
                    start.setBackgroundResource(R.drawable.stopping1);
                    musicitem1.setText(playerBinder.getName());
                    break;
                }
                if (playerBinder.getPosition() == -1) {
                    playerBinder.mediaPlay(0);
                    start.setBackgroundResource(R.drawable.starting1);
                    musicitem1.setText(playerBinder.getName());
                    songImage.setImageDrawable(playerBinder.getAlbumImg());
                    break;
                } else {
                    playerBinder.start();
                    start.setBackgroundResource(R.drawable.starting1);
                    musicitem1.setText(playerBinder.getName());
                }
                break;
            case R.id.nextSong1:
                playerBinder.playNextSong();
                start.setBackgroundResource(R.drawable.starting1);
                musicitem1.setText(playerBinder.getName());
                songImage.setImageDrawable(playerBinder.getAlbumImg());
                break;
            case R.id.lastSong1:
                playerBinder.backSong();
                start.setBackgroundResource(R.drawable.starting1);
                musicitem1.setText(playerBinder.getName());
                songImage.setImageDrawable(playerBinder.getAlbumImg());
                break;
        }

    }

    private class MyAdapter extends FragmentPagerAdapter {
        private List<Fragment> list;

        public MyAdapter(@NonNull FragmentManager fm, List<Fragment> list) {
            super(fm);
            this.list = list;
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            return list.get(position);
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return title[position];
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                drawer.openDrawer(GravityCompat.START);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    protected void setStatusBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);//隐藏状态栏但不隐藏状态栏字体
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);//实现状态栏图标和文字颜色为暗色
        }
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
    protected void onRestart() {
        super.onRestart();
        if (playerBinder.getPosition() != -1) {
            songImage.setImageDrawable(playerBinder.getAlbumImg());
            musicitem1.setText(playerBinder.getName());
        }
        if (playerBinder.getMediaState()) {
            start.setBackgroundResource(R.drawable.starting1);
        } else {
            start.setBackgroundResource(R.drawable.stopping1);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
