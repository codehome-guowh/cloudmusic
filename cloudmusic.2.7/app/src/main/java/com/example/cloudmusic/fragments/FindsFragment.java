package com.example.cloudmusic.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.cloudmusic.R;

import java.util.ArrayList;

public class FindsFragment extends Fragment {

    private static final int UPDATE_ITEM = 1;
    private ArrayList<ImageView> imageViews = new ArrayList<>();
    private int[] imageResIds = {R.drawable.head1,R.drawable.head2,R.drawable.head3,R.drawable.head4,R.drawable.head5};
    private ViewPager viewPager;
    private TextView tv_desc;
    private LinearLayout layout_dot;
    private int count = 10000000;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case UPDATE_ITEM:
                    upDataItem();
                    break;
            }
            super.handleMessage(msg);
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_finds, container, false);
        //初始化控件
        viewPager = view.findViewById(R.id.vp);
        layout_dot =view.findViewById(R.id.layout_dot);
        //给viewPager设置适配器
        viewPager.setAdapter(new MyPagerAdapter());
        //对viewPager设置监听器
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            //当页面滚动时触发的时间
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            //当页面被选中时触发的方法
            @Override
            public void onPageSelected(int position) {
                //对position进行处理
                position = position % imageViews.size();
                //当页面被选中的时候,改变描述文本
                changeDots(position);
            }

            //当页面状态滚动状态发生改变时触发的事件
            @Override
            public void onPageScrollStateChanged(int state) {
                //当页面空闲状态被改变的时候
                if (state == viewPager.SCROLL_STATE_IDLE) {
                    handler.sendEmptyMessageDelayed(UPDATE_ITEM, 3000);
                } else {
                    handler.removeMessages(UPDATE_ITEM);
                }
            }
        });


        //初始化图片
        initImage();
        //初始化文字下方的点
        initDot();
        //当加载页面的时候,默认让第一个文本加载出来
//    initDescFirst();

        //使两边都可以无限轮播
        viewPager.setCurrentItem(count / 2);
//页面加载时更新
        upDataItem();
        return view;
    }


    private void upDataItem() {
        int index = viewPager.getCurrentItem();
        viewPager.setCurrentItem(++index);
        handler.sendEmptyMessageDelayed(UPDATE_ITEM, 3000);
    }

    //选中对应的原点
    private void changeDots(int position) {
        //先把所有的点恢复为白色
        for (int i = 0; i < layout_dot.getChildCount(); i++) {
            View view = layout_dot.getChildAt(i);
            view.setSelected(false);
        }
        //获取当前被选中的条目 设置为选中状态
        layout_dot.getChildAt(position).setSelected(true);

    }

    //初始化文字下方的点
    private void initDot() {
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(10, 10);
        layoutParams.setMargins(4, 4, 4, 4);
        for (int i = 0; i < imageViews.size(); i++) {
            View view = new View(getContext());
            view.setBackgroundResource(R.drawable.ic_stars_black_24dp);
            view.setLayoutParams(layoutParams);
            layout_dot.addView(view);
        }
    }

    //当加载页面的时候,默认让第一个文本加载出来
    private void initDescFirst() {
        changeDots(0);
    }

    //添加图片,准备一个ImageView集合,用来交给instantiateItem添加到页面
    private void initImage() {
        for (int i = 0; i < imageResIds.length; i++) {
            //创建出ImageView对象
            ImageView imageView = new ImageView(getContext());
            imageView.setImageResource(imageResIds[i]);
            imageViews.add(imageView);
        }
    }

    private class MyPagerAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return count;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            //判断这个view是不是通过instantiateItem创建出来的
            return view == object;
        }

        //用来创建条目
        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            position = position % imageResIds.length;
            //获取条目
            ImageView imageView = imageViews.get(position);
            container.addView(imageView);
            return imageView;
        }

        //用来销毁条目,,且最多会创建出三个条目,多出来的条目将会被销毁
        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {

            //销毁创建的条目
            container.removeView((View) object);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        //当页面显示的时候,更新轮播图
        handler.sendEmptyMessageDelayed(UPDATE_ITEM, 3000);
    }

    @Override
    public void onStop() {
        super.onStop();
        //当页面不可见时,停止更新
        handler.removeCallbacksAndMessages(null);
    }





    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    @Override
    public void onDetach() {
        super.onDetach();

    }


}
