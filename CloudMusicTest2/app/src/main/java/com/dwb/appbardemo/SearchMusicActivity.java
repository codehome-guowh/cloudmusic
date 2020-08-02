package com.dwb.appbardemo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.SearchView;

import com.dwb.appbardemo.adapter.SearchMusicAdapter;
import com.dwb.appbardemo.model.LocalMusicBean;
import com.dwb.appbardemo.utils.MusicDataUtils;

import java.util.ArrayList;
import java.util.List;

public class SearchMusicActivity extends AppCompatActivity {

    public static List<LocalMusicBean> onlineMusic = new ArrayList<>();

    private SearchMusicAdapter adapter;

    SearchView searchView;

    RecyclerView musicRv;

    ImageView iv_back_search;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_music);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN |
                    View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }

        iv_back_search = findViewById(R.id.iv_back_search);
        searchView = findViewById(R.id.searchView);
        musicRv = findViewById(R.id.local_music_rv);

        iv_back_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                moveTaskToBack(true);
            }
        });
        setData();


    }





    public void setData(){
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                Log.i("123","111");
                search(s);
                MusicDataUtils.getOnlineMusic(SearchMusicActivity.this,s);

                return true;
            }


            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });




    }

    public void search(String s){
        try {
            MusicDataUtils.getOnlineMusic(SearchMusicActivity.this,s);
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        adapter = new SearchMusicAdapter(SearchMusicActivity.this, onlineMusic);
        musicRv.setAdapter(adapter);
        //RecyclerView设置布局管理器
        LinearLayoutManager layoutManager = new LinearLayoutManager(SearchMusicActivity.this, LinearLayoutManager.VERTICAL, false);
        musicRv.setLayoutManager(layoutManager);
    }


}