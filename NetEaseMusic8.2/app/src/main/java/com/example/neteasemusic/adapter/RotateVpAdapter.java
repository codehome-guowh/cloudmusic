package com.example.neteasemusic.adapter;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.viewpager.widget.PagerAdapter;

import com.example.neteasemusic.bean.RotateBean;
import com.example.neteasemusic.fragment.MyFragmentFind;
import com.example.neteasemusic.R;

import java.util.List;


public class RotateVpAdapter extends PagerAdapter {

    private List<RotateBean> datas;
    private Context context;
    private LayoutInflater inflater;

    public RotateVpAdapter(List<RotateBean> datas, MyFragmentFind context) {
        this.datas = datas;
        this.context = context.getActivity();
        inflater = LayoutInflater.from(context.getActivity());
    }

    public RotateVpAdapter(Context context) {
        this.context = context;
        inflater = LayoutInflater.from(context);
    }

    public void setDatas(List<RotateBean> datas) {
        this.datas = datas;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return datas == null ? 0 : Integer.MAX_VALUE;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        // position是int最大值所以这里可能是几百甚至上千,因此取余避免数组越界
        int newPosition = position % datas.size();
        View convertView = inflater.inflate(R.layout.item_vp, container, false);
        ImageView imageView = (ImageView) convertView.findViewById(R.id.item_iv);
        imageView.setImageResource(datas.get(newPosition).getImgId());

        container.addView(convertView);
        return convertView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {

    }
}

