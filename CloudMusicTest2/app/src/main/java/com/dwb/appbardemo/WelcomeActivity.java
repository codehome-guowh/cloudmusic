package com.dwb.appbardemo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.dwb.appbardemo.utils.OnlineMusicUtils;

import java.util.Timer;
import java.util.TimerTask;

import cn.bmob.v3.Bmob;

public class WelcomeActivity extends AppCompatActivity {

    public final String APP_KEY = "fbf4651be93e3a3387c890aeac417d6e";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.
                FLAG_FULLSCREEN);
        setContentView(R.layout.activity_welcome);

        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(getResources().getColor(R.color.colorStatus));

        Timer timer = new Timer();
        timer.schedule(timerTask,2000);

        //Bmob初始化
        Bmob.initialize(this,APP_KEY);
        //加载数据库音乐数据
        OnlineMusicUtils.ResolveMusicToList();


    }

    private TimerTask timerTask = new TimerTask() {
        @Override
        public void run() {
            startActivity(new Intent(WelcomeActivity.this,MainActivity.class));
            finish();
        }
    };


    //去掉底部按钮
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus){
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE|
                            View.SYSTEM_UI_FLAG_HIDE_NAVIGATION|
                            View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN|
                            View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION|
                            View.SYSTEM_UI_FLAG_FULLSCREEN|
                            View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
            );
        }
    }
}