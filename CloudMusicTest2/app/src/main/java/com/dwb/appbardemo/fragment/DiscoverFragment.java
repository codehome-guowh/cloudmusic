package com.dwb.appbardemo.fragment;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.dwb.appbardemo.MusicAlbumActivity;
import com.dwb.appbardemo.R;
import com.dwb.appbardemo.adapter.RollAdapter;
import com.jude.rollviewpager.RollPagerView;
import com.jude.rollviewpager.hintview.ColorPointHintView;

public class DiscoverFragment extends Fragment {

    public static String type;

    RollPagerView rollPagerView;
    ImageView img_cx,img_dz,img_hy;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.layout_discover,container,false);

        img_cx = view.findViewById(R.id.image_cx);
        img_dz = view.findViewById(R.id.image_dz);
        img_hy = view.findViewById(R.id.image_hy);

        Onclick();


        //轮播图展示
        setRollPagerView(view);

        return view;
    }

    //展示轮播图
    public void setRollPagerView(View view){

        rollPagerView = view.findViewById(R.id.rollpager);
        //设置播放间隔
        rollPagerView.setPlayDelay(1500);
        //透明度设置
        rollPagerView.setAnimationDurtion(500);
        //设置适配器
        rollPagerView.setAdapter(new RollAdapter());
        rollPagerView.setHintView(new ColorPointHintView(view.getContext(), Color.RED,Color.WHITE));

    }

    public  void  Onclick(){
        img_cx.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), MusicAlbumActivity.class);
                type ="出行";
                startActivity(intent);
            }
        });

        img_dz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), MusicAlbumActivity.class);
                type ="独自";
                startActivity(intent);
            }
        });

        img_hy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), MusicAlbumActivity.class);
                type ="华语";
                startActivity(intent);
            }
        });
    }

}
