package com.example.neteasemusic.adapter;

import android.content.Context;

import android.net.Uri;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;


import androidx.annotation.NonNull;

import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.example.neteasemusic.R;
import com.example.neteasemusic.bean.Video;
import com.example.neteasemusic.view.SimpleVideoView;

import java.util.List;

public class VideoAdapter extends RecyclerView.Adapter<VideoAdapter.LinearViewHolder> {
    private Context mContext;
    private List<Video> videos;
    private FrameLayout dl;

    public VideoAdapter(Context context,List<Video>videos){
        this.videos=videos;
        this.mContext = context;
    }
    @NonNull
    @Override
    public VideoAdapter.LinearViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new LinearViewHolder(LayoutInflater.from(mContext).inflate(R.layout.video_item,parent,false));
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onBindViewHolder(@NonNull VideoAdapter.LinearViewHolder holder, int position) {
        Video videoItem=videos.get(position);
        holder.video.setInitPicture(mContext.getResources().getDrawable(videoItem.getCover(),null));
        holder.video.setBigVideoTitle(videoItem.getTitle());
        holder.video.setHeadImg(mContext.getResources().getDrawable(videoItem.getHeadImg(),null));
        holder.video.setUser(videoItem.getUser());
        holder.video.setVideoUri(Uri.parse(videoItem.getPath()));
        holder.video.setFingerNumber(videoItem.getFingerNumber());
        if (position==videos.size()-1){//最后一个分隔线不可见
            dl.setVisibility(View.GONE);
        }
    }
    @Override
    public int getItemCount() {
        return videos.size();
    }

    protected class LinearViewHolder extends RecyclerView.ViewHolder {
        private SimpleVideoView video;
        public LinearViewHolder(@NonNull View itemView) {
            super(itemView);
            video=itemView.findViewById(R.id.video);
            dl=itemView.findViewById(R.id.divideLine);
        }
    }
}