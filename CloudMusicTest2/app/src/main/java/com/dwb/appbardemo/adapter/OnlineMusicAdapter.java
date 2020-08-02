package com.dwb.appbardemo.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dwb.appbardemo.R;
import com.dwb.appbardemo.model.OnlineMusicBean;

import java.util.List;

public class OnlineMusicAdapter extends RecyclerView.Adapter<OnlineMusicAdapter.OnlineMusicViewHolder> {

    Context context;

    List<OnlineMusicBean> mDatas;

    OnItemClickListener onItemClickListener;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public interface OnItemClickListener {
        public void OnItemClick(View view, int position);
    }

    public OnlineMusicAdapter(Context context, List<OnlineMusicBean> mDatas) {
        this.context = context;
        this.mDatas = mDatas;
    }

    @NonNull
    @Override
    public OnlineMusicViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_local_music, parent, false);
        OnlineMusicViewHolder holder = new OnlineMusicViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull OnlineMusicViewHolder holder, final int position) {
        OnlineMusicBean musicBean = mDatas.get(position);
        holder.idTv.setText(musicBean.getId());
        holder.songTv.setText(musicBean.getSong());
        holder.SingerTv.setText(musicBean.getSinger());
        holder.albumTv.setText(musicBean.getAlbum());
        holder.timeTv.setText(musicBean.getDuration());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClickListener.OnItemClick(v, position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mDatas.size();
    }

    class OnlineMusicViewHolder extends RecyclerView.ViewHolder {
        TextView idTv, songTv, SingerTv, albumTv, timeTv;

        public OnlineMusicViewHolder(@NonNull View itemView) {
            super(itemView);
            idTv = itemView.findViewById(R.id.item_local_music_num);
            songTv = itemView.findViewById(R.id.item_local_music_song);
            SingerTv = itemView.findViewById(R.id.item_local_music_singer);
            albumTv = itemView.findViewById(R.id.item_local_music_album);
            timeTv = itemView.findViewById(R.id.item_local_music_duition);
        }
    }
}
