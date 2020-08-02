package com.example.cloudmusic.activity;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.viewpager.widget.ViewPager;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.IBinder;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.example.cloudmusic.R;
import com.example.cloudmusic.adapter.IndexViewPagerAdapter;
import com.example.cloudmusic.servcie.MediaPlayService;
import java.lang.reflect.Field;

public class IndexActivity extends AppCompatActivity {
    private ImageButton ibtnMenu;
    private ImageView iViewSearch,userHeader,songHeaderPlay;
    private DrawerLayout mDrawerLayout;
    private TextView userInfo;
    private ImageButton btnPlay;
    private RadioGroup rdbGroup;
    private RadioButton rdbMine,rdbFriends,rdbFinds,rdbVideo;
    private ViewPager viewPager;
    private IndexViewPagerAdapter indexAdapter;
    public static final int PAGE_MINE=0;
    public static final int PAGE_FINDS=1;
    public static final int PAGE_FRIENDS=2;
    public static final int PAGE_VIDEO=3;
    private MusicPlayBroadcast musicPlayBroadcast;





    //        oncreate方法
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_index);
        ActionBar actionBar=getSupportActionBar();
        actionBar.hide();
//        menu设置
        View viewNoClick=findViewById(R.id.layout_noclick);
        viewNoClick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                null
            }
        });



//        动态注册广播
        musicPlayBroadcast=new MusicPlayBroadcast();
        IntentFilter filter=new IntentFilter("com.example.cloudmusic.broadcastreceiver.MusicPlayBroadcast");
        registerReceiver(musicPlayBroadcast,filter);



//        设置菜单栏按钮图片
        ibtnMenu=findViewById(R.id.btn_index_menu);
        ibtnMenu.setImageDrawable(this.getResources().getDrawable(R.drawable.ic_menu_black_24dp));

//        设置搜索页面按钮图片
        iViewSearch=findViewById(R.id.iView_index_search);
        iViewSearch.setBackgroundResource(R.drawable.sousuo);

//        设置用户信息(初始化写死)
        userHeader=findViewById(R.id.imageview_mine_user_head);
        userInfo=findViewById(R.id.text_mine_userinfo);
        userHeader.setImageResource(R.drawable.head2);
        userInfo.setText("伟虎威");


//        底部播放栏初始化
        songHeaderPlay=findViewById(R.id.imageview_mine_songimagesmall);
        songHeaderPlay.setImageResource(R.drawable.head3);
        btnPlay=findViewById(R.id.imagebutton_mine_play);
        btnPlay.setBackgroundResource(R.drawable.play);

        Intent intentToPlayBroadCast=new Intent();
        intentToPlayBroadCast.setAction("com.example.cloudmusic.broadcastreceiver.MusicPlayBroadcast");
        sendBroadcast(intentToPlayBroadCast);
        View viewPlay=findViewById(R.id.lt_mine_play);
        viewPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(IndexActivity.this,PlayActivity.class);
                startActivity(intent);
            }
        });








//         抽屉效果的实现
        mDrawerLayout = findViewById(R.id.drawer_layout);
        try {
            // 用反射来设置划动出来的距离 mMinDrawerMargin
            Field mMinDrawerMarginField = DrawerLayout.class.getDeclaredField("mMinDrawerMargin");
            mMinDrawerMarginField.setAccessible(true);
            int minDrawerMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                    50, getResources().getDisplayMetrics());
            mMinDrawerMarginField.set(mDrawerLayout, minDrawerMargin);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // 设置阴影颜色
        mDrawerLayout.setScrimColor(Color.parseColor("#55000000"));
        // 设置边缘颜色
        mDrawerLayout.setDrawerShadow(new ColorDrawable(Color.parseColor("#22000000")), Gravity.LEFT);
        mDrawerLayout.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                // 这里可以写动画
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                // 打开的时候，手势可以划动
                mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
            }

            @Override
            public void onDrawerClosed(View drawerView) {
            }

            @Override
            public void onDrawerStateChanged(int newState) {

            }
        });

//    fragment切换
//       初始化
        rdbMine=findViewById(R.id.rb_index_mine);
        rdbFinds=findViewById(R.id.rb_index_finds);
        rdbFriends=findViewById(R.id.rb_index_friends);
        rdbVideo=findViewById(R.id.rb_index_video);
        rdbGroup=findViewById(R.id.rg_tab_bar);
        viewPager=findViewById(R.id.viewpager);
        indexAdapter=new IndexViewPagerAdapter(getSupportFragmentManager());
        rdbGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
                switch (checkedId) {
                    case R.id.rb_index_mine: viewPager.setCurrentItem(PAGE_MINE);
                        break;
                    case R.id.rb_index_finds: viewPager.setCurrentItem(PAGE_FINDS);
                        break;
                    case R.id.rb_index_friends: viewPager.setCurrentItem(PAGE_FRIENDS);
                        break;
                    case R.id.rb_index_video: viewPager.setCurrentItem(PAGE_VIDEO);
                        break;
                }

            }
        });



//        viewpager 滑动效果
        viewPager.setAdapter(indexAdapter);
        viewPager.setCurrentItem(0);
        rdbMine.setTextSize(20);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {
                if(state==0)
                {

                }

                if (state == 2) {
                    int currentItemPosition = viewPager.getCurrentItem();

                    switch (currentItemPosition) {
                        case PAGE_MINE:
                            rdbMine.setChecked(true);
                            rdbMine.setTextSize(20);
                            rdbFinds.setTextSize(15);
                            rdbFriends.setTextSize(15);
                            rdbVideo.setTextSize(15);
                        break;
                        case PAGE_FINDS:
                            rdbFinds.setChecked(true);
                            rdbFinds.setTextSize(20);
                            rdbMine.setTextSize(15);
                            rdbFriends.setTextSize(15);
                            rdbVideo.setTextSize(15);
                        break;
                        case PAGE_FRIENDS:
                            rdbFriends.setChecked(true);
                            rdbFriends.setTextSize(20);
                            rdbMine.setTextSize(15);
                            rdbFinds.setTextSize(15);
                            rdbVideo.setTextSize(15);
                        break;
                        case PAGE_VIDEO:
                            rdbVideo.setChecked(true);
                            rdbVideo.setTextSize(20);
                            rdbMine.setTextSize(15);
                            rdbFinds.setTextSize(15);
                            rdbFriends.setTextSize(15);
                        break;
                    }
                }


            }
        });









    }




    //  抽屉打开的点击事件
    public void toggle(View view) {
        // 如果没有打开，就去打开
        if (!mDrawerLayout.isDrawerOpen(Gravity.LEFT)) {
            mDrawerLayout.openDrawer(Gravity.LEFT);
        }

    }

    //        广播
    public class MusicPlayBroadcast extends BroadcastReceiver {
        private ImageButton btnPlay;
        int playState;
        MediaPlayService.MyBinder myBinder;
        private ServiceConnection conn=new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
                myBinder=(MediaPlayService.MyBinder)iBinder;
            }

            @Override
            public void onServiceDisconnected(ComponentName componentName) {

            }
        };

        @Override
        public void onReceive(final Context context, final Intent intent) {
            playState=0;
            btnPlay=findViewById(R.id.imagebutton_mine_play);


            Intent intentToBind=new Intent(context,MediaPlayService.class);
            intentToBind.putExtra("songname","song.mp3");
            bindService(intentToBind,conn,BIND_AUTO_CREATE);








            btnPlay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent=new Intent(context,MediaPlayService.class);
                    intent.putExtra("songname","song.mp3");

                    if(playState==0){
                        btnPlay.setBackgroundResource(R.drawable.pause);
                        intent.putExtra("state",0);
                        startService(intent);
                        playState=1;
                    }
                    else if (playState==1)
                    {
                        btnPlay.setBackgroundResource(R.drawable.play);
                        intent.putExtra("state",1);
                        startService(intent);
                        playState=0;
                    }



                }

            });





        }
    }
//    注销广播

    @Override
    protected void onDestroy() {
        super.onDestroy();

        unregisterReceiver(musicPlayBroadcast);
    }













}
