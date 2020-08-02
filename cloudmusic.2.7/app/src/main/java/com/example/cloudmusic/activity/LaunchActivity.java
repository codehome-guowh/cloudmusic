package com.example.cloudmusic.activity;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.example.cloudmusic.R;

public class LaunchActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        去除顶部信息栏（全屏操作）写在setcontentview前
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
//        隐藏自带的actionbar
        setContentView(R.layout.activity_launch);
        getWindow().setBackgroundDrawableResource(R.drawable.load);
        ActionBar actionBar=getSupportActionBar();
        actionBar.hide();

        Handler x = new Handler();//定义一个handle对象
        x.postDelayed(new splashhandler(), 3000);//设置3秒钟延迟执行splashhandler线程。
    }

    class splashhandler implements Runnable{
        public void run() {
            startActivity(new Intent(getApplication(), IndexActivity.class));// 这个线程的作用3秒后就是进入到你的主界面  可以利用这个时间检查更新  网络连接  用户信息的搜索
            LaunchActivity.this.finish();// 把当前的LaunchActivity结束掉
        }
    }


//    底部虚拟导航栏去除****
    @Override public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus)
        {
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }
    }


}
