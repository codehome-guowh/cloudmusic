package com.example.cloudmusic.activity;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import com.example.cloudmusic.R;
import com.example.cloudmusic.servcie.MediaPlayService;

public class SongSheetActivity extends AppCompatActivity {
    private ImageView songHeaderPlay;
    private ImageButton btnPlay;
    ListView songsList;

    private MusicPlayBroadcast musicPlayBroadcast;


    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_song_sheet);
        songsList=findViewById(R.id.list_song_sheet);

//        设置actionbar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);//左侧添加一个默认的返回图标
        getSupportActionBar().setHomeButtonEnabled(true); //设置返回键可用
        getSupportActionBar().setTitle(R.string.title);

        Intent intentGetInf=getIntent();
        final int count=intentGetInf.getIntExtra("songAmount",0);






        //        重写baseadapter

        BaseAdapter songSheetAdapter = new BaseAdapter() {
            class ViewHolder {
                TextView textNumber,textSongName,textSinger;
                ImageButton imgBtnDelete;
            }

            String[] songsName={"铃声"};
            String[] singers={"未知"};




            @Override
            public int getCount() {
                return count;
            }

            @Override
            public Object getItem(int position) {
                return null;
            }

            @Override
            public long getItemId(int position) {
                return position;
            }

            @Override
            public View getView(int position, View view, ViewGroup viewGroup) {
                ViewHolder viewHolder=new ViewHolder();

                    view=LayoutInflater.from(SongSheetActivity.this).inflate(R.layout.song_list,viewGroup,false);
                    viewHolder.textNumber=view.findViewById(R.id.text_song_number);
                    viewHolder.textSinger=view.findViewById(R.id.text_singer);
                    viewHolder.textSongName=view.findViewById(R.id.text_song_name);
                    viewHolder.imgBtnDelete=view.findViewById(R.id.imagebtn_song_sheet_delete);


                //        触发删除事件

                viewHolder.imgBtnDelete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        showDialog();
                    }
                });

                int positionPlus=position+1;
                viewHolder.textNumber.setText(""+positionPlus);
                viewHolder.textSongName.setText(songsName[0]);
                viewHolder.textSinger.setText(singers[0]);
                return view;
            }
        };
        songsList.setAdapter(songSheetAdapter);



        //        动态注册广播
        musicPlayBroadcast= new MusicPlayBroadcast();
        IntentFilter filter=new IntentFilter("com.example.cloudmusic.broadcastreceiver.MusicPlayBroadcast");
        registerReceiver(musicPlayBroadcast,filter);

        //        底部播放栏初始化
        songHeaderPlay=findViewById(R.id.imageview_song_sheet_songimagesmall);
        songHeaderPlay.setImageResource(R.drawable.head3);
        btnPlay=findViewById(R.id.imagebutton_song_sheet_play);
        btnPlay.setBackgroundResource(R.drawable.play);

        Intent intentToPlayBroadCast=new Intent();
        intentToPlayBroadCast.setAction("com.example.cloudmusic.broadcastreceiver.MusicPlayBroadcast");
        sendBroadcast(intentToPlayBroadCast);
        View view=findViewById(R.id.lt_song_sheet_play);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(SongSheetActivity.this,PlayActivity.class);
                startActivity(intent);
            }
        });

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
            btnPlay=findViewById(R.id.imagebutton_song_sheet_play);


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

//     弹窗
    private void showDialog(){
        AlertDialog.Builder builder=new AlertDialog.Builder(this);

        builder.setTitle("温馨提示");
        builder.setMessage("歌曲已删除！");
        builder.setPositiveButton("我知道了",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent intentRefresh=new Intent(SongSheetActivity.this, SongSheetActivity.class);
                        startActivity(intentRefresh);

                    }
                });
        AlertDialog dialog=builder.create();
        dialog.show();

    }
}
