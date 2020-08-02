package com.example.cloudmusic;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.RecoverySystem;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.AbsListView;
import android.widget.Toast;

import com.example.cloudmusic.adapter.LocalMusicListAdapter;
import com.example.cloudmusic.bean.Song;
import com.example.cloudmusic.util.SongUtil;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Spliterator;

import ie.moses.recyclerviewadapter.core.OnItemClickListener;

public class MainActivity extends AppCompatActivity {


    private RecyclerView ryLocalmusic;

    final static String TAG = "MainActivity";

    List<Song> songList;

    private OnItemClickListener onItemClickListener = new OnItemClickListener() {
        @Override
        public void onItemClick(int i) {

        }
    };




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        setContentView(R.layout.activity_main);
        ryLocalmusic = findViewById(R.id.ry_local_music);
        songList = new ArrayList<Song>();
        LocalMusicListAdapter localMusicListAdapter = new LocalMusicListAdapter(this,songList,
                onItemClickListener);

    }

    @Override
    protected void onStart() {
        super.onStart();
        songList = SongUtil.ResolveMusicToList(this);


    }




}