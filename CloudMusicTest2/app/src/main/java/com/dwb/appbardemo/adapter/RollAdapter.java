package com.dwb.appbardemo.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.dwb.appbardemo.R;
import com.jude.rollviewpager.adapter.StaticPagerAdapter;

public class RollAdapter extends StaticPagerAdapter {

    private int[] pic ={R.drawable.pic1, R.drawable.pic2, R.drawable.pic3};

    @Override
    public View getView(ViewGroup container, int position) {
        ImageView imageView = new ImageView(container.getContext());
        imageView.setImageResource(pic[position]);
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        imageView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT));
        return imageView;
    }

    @Override
    public int getCount() {
        return pic.length;
    }
}
