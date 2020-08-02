package com.example.neteasemusic.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.example.neteasemusic.R;
import com.example.neteasemusic.bean.RotateBean;
import com.example.neteasemusic.adapter.RotateVpAdapter;

import java.util.ArrayList;
import java.util.List;

public class MyFragmentFind extends Fragment {
    private static final int TIME = 3000;
    private ViewPager viewPager;
    private LinearLayout pointLl;// 轮播状态改变的小圆点容器
    private List<RotateBean> datas;
    private RotateVpAdapter vpAdapter;
    private Handler handler;
    private boolean isRotate = false;
    private Runnable rotateRunnable;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.layout_find, container, false);
        viewPager = view.findViewById(R.id.rotate_vp);
        pointLl = view.findViewById(R.id.rotate_point_container);

        buildDatas();// 构造数据
        vpAdapter = new RotateVpAdapter(datas, this);
        viewPager.setAdapter(vpAdapter);
        viewPager.setCurrentItem(datas.size() * 100);
        handler = new Handler();
        startRotate();
        addPoints();
        changePoints();
        return view;
    }

    private void changePoints() {
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (isRotate) {
                    for (int i = 0; i < datas.size(); i++) {
                        ImageView pointIv = (ImageView) pointLl.getChildAt(i);
                        pointIv.setImageResource(R.drawable.point_white);
                    }
                    ImageView iv = (ImageView) pointLl.getChildAt(position % datas.size());
                    iv.setImageResource(R.drawable.point_balck);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private void addPoints() {
        for (int i = 0; i < datas.size(); i++) {
            ImageView pointIv = new ImageView(this.getActivity());
            pointIv.setPadding(5, 5, 5, 5);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(20, 20);
            pointIv.setLayoutParams(params);
            if (i == 0) {
                pointIv.setImageResource(R.drawable.point_balck);
            } else {
                pointIv.setImageResource(R.drawable.point_white);
            }
            pointLl.addView(pointIv);
        }
    }

    private void startRotate() {
        rotateRunnable = new Runnable() {
            @Override
            public void run() {
                int nowIndex = viewPager.getCurrentItem();
                viewPager.setCurrentItem(++nowIndex);
                if (isRotate) {
                    handler.postDelayed(rotateRunnable, TIME);
                }
            }
        };
        handler.postDelayed(rotateRunnable, TIME);
    }

    private void buildDatas() {
        datas = new ArrayList<>();
        datas.add(new RotateBean(R.drawable.rotate1));
        datas.add(new RotateBean(R.drawable.rotate2));
        datas.add(new RotateBean(R.drawable.rotate3));
        datas.add(new RotateBean(R.drawable.rotate4));
    }

    @Override
    public void onResume() {
        super.onResume();
        isRotate = true;
    }

    @Override
    public void onPause() {
        super.onPause();
        isRotate = false;
    }
}
