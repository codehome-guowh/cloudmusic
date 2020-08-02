package com.example.cloudmusic.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;


import com.example.cloudmusic.R;
import com.example.cloudmusic.servcie.MediaPlayService;


public class PlayActivity extends AppCompatActivity {
    private MusicPlayBroadcast musicPlayBroadcast;





    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);


        //        动态注册广播
        musicPlayBroadcast=new MusicPlayBroadcast();
        IntentFilter filter=new IntentFilter("com.example.cloudmusic.broadcastreceiver.MusicPlayBroadcast");
        registerReceiver(musicPlayBroadcast,filter);
//        发送广播
        Intent intentToPlayBroadCast=new Intent();
        intentToPlayBroadCast.setAction("com.example.cloudmusic.broadcastreceiver.MusicPlayBroadcast");
        sendBroadcast(intentToPlayBroadCast);



//        设置actionbar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);//左侧添加一个默认的返回图标
        getSupportActionBar().setHomeButtonEnabled(true); //设置返回键可用
        getSupportActionBar().setTitle(R.string.songname);






    }

    //        广播
    public class MusicPlayBroadcast extends BroadcastReceiver {
        private ImageView viewPlay;
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
            Log.d("music123456","受到广播");





            playState=0;
            viewPlay =findViewById(R.id.imageview_play_play);


            Intent intentToBind=new Intent(context,MediaPlayService.class);
            intentToBind.putExtra("songname","song.mp3");
            bindService(intentToBind,conn,BIND_AUTO_CREATE);





             final ImageView imageViewBigHead = (ImageView) findViewById(R.id.imageview_song_bighead);








            viewPlay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent=new Intent(context,MediaPlayService.class);
                    intent.putExtra("songname","song.mp3");

                    if(playState==0){
                        viewPlay.setImageResource(R.drawable.pause);
                        intent.putExtra("state",0);
                        startService(intent);
                        //动画
                        Animation animation = AnimationUtils.loadAnimation(context, R.anim.anim);
                        LinearInterpolator lin = new LinearInterpolator();//设置动画匀速运动
                        animation.setInterpolator(lin);
                        imageViewBigHead.startAnimation(animation);
                        playState=1;

                    }
                    else if (playState==1)
                    {
                        viewPlay.setImageResource(R.drawable.play);
                        intent.putExtra("state",1);
                        startService(intent);
                        imageViewBigHead.clearAnimation();
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
