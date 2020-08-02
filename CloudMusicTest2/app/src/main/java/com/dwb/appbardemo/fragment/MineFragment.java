package com.dwb.appbardemo.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.dwb.appbardemo.LocalMusicActivity;
import com.dwb.appbardemo.LoveMusic;
import com.dwb.appbardemo.OnlineMusicActivity;
import com.dwb.appbardemo.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.bmob.v3.BmobUser;

public class MineFragment extends Fragment {
    TextView name;
    GridView gridView,gridView2;

    String[] title=new String[]{"本地音乐","在线音乐","我的喜爱","我的收藏","关注新歌"};
    int[] imgs=new int[]{R.drawable.ly_localmusic,R.drawable.ly_download,
            R.drawable.ly_broadcaststation,R.drawable.ly_myfavorite,R.drawable.ly_concernnewsong};

    String[] title2=new String[]{"云音乐热歌榜","云音乐新歌榜"};
    int[] imgs2=new int[]{R.drawable.ly_hotsong,R.drawable.ly_newsong};

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.layout_mine,container,false);

        gridView=view.findViewById(R.id.gridView);
        gridView2=view.findViewById(R.id.gridView2);
        name = view.findViewById(R.id.mine_name);

        List<Map<String,Object>> list=new ArrayList<>();
        for (int i = 0; i < title.length; i++) {
            Map<String,Object> map=new HashMap<>();
            map.put("name", title[i]);
            map.put("img", imgs[i]);
            list.add(map);
        }
        List<Map<String,Object>> list2=new ArrayList<>();
        for (int i = 0; i < title2.length; i++) {
            Map<String,Object> map2=new HashMap<>();
            map2.put("name2", title2[i]);
            map2.put("img2", imgs2[i]);
            list2.add(map2);
        }
        SimpleAdapter simpleAdapter=new SimpleAdapter(getContext(),list,R.layout.layout_mine_item1,new String[]{"name","img"},new  int[]{R.id.mine_name,R.id.imageView2});
        gridView.setAdapter(simpleAdapter);

        SimpleAdapter simpleAdapter2=new SimpleAdapter(getContext(),list2,R.layout.layout_mine_item2,new String[]{"name2","img2"},new  int[]{R.id.textView7,R.id.imageView4});
        gridView2.setAdapter(simpleAdapter2);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (i == 0){
                    Intent intent = new Intent(getContext(), LocalMusicActivity.class);
                    startActivity(intent);
                }else if(i == 1){
                    Intent intent = new Intent(getContext(), OnlineMusicActivity.class);
                    startActivity(intent);
                }else if(i == 2){
                    Intent intent = new Intent(getContext(), LoveMusic.class);
                    startActivity(intent);
                }
            }
        });

        return view;
    }
}
