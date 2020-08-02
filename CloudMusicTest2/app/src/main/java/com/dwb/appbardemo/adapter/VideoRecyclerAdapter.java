package com.dwb.appbardemo.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.dwb.appbardemo.R;
import com.dwb.appbardemo.model.VideoModel;

import java.util.List;

import cn.jzvd.JzvdStd;

public class VideoRecyclerAdapter extends RecyclerView.Adapter<VideoRecyclerAdapter.ViewHolder> {

    Context context;
    List<VideoModel.ItemListBean> mDatas;
    public VideoRecyclerAdapter(Context context, List<VideoModel.ItemListBean> mDatas) {
        this.context = context;
        this.mDatas = mDatas;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_video, parent, false));

    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        //获取指定位置的数据源
        VideoModel.ItemListBean.DataBean dataBean = mDatas.get(position).getData();
        //设置发布者信息
        VideoModel.ItemListBean.DataBean.AuthorBean author = dataBean.getAuthor();
        holder.nameTv.setText(author.getName());
        holder.descTv.setText(author.getDescription());
        String iconURL = author.getIcon();
        if (!TextUtils.isEmpty(iconURL)){
            Glide.with(context).load(iconURL).into(holder.iconIv);
        }

        //获取点赞数和评论数
        VideoModel.ItemListBean.DataBean.ConsumptionBean consumption = dataBean.getConsumption();
        holder.likeTv.setText(consumption.getRealCollectionCount()+"");
        holder.likeTv.setText(consumption.getReplyCount()+"");

        //设置视频播放器的信息
        holder.jzvdStd.setUp(dataBean.getPlayUrl(),dataBean.getTitle(),JzvdStd.SCREEN_NORMAL);
        String thumbURL = dataBean.getCover().getFeed();//缩略图地址
        Glide.with(context).load(thumbURL).into(holder.jzvdStd.posterImageView);
        holder.jzvdStd.positionInList = position;

    }

    @Override
    public int getItemCount() {
        return mDatas.size();
    }



    class ViewHolder extends RecyclerView.ViewHolder {
        JzvdStd jzvdStd;
        ImageView iconIv;
        TextView nameTv,descTv,likeTv,replyTv;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            jzvdStd = itemView.findViewById(R.id.item_jzvd);
            iconIv = itemView.findViewById(R.id.item_iv);
            nameTv = itemView.findViewById(R.id.item_tv_name);
            descTv = itemView.findViewById(R.id.item_tv_des);
            likeTv = itemView.findViewById(R.id.item_tv_like);
            replyTv = itemView.findViewById(R.id.item_tv_reply);
        }
    }
}
