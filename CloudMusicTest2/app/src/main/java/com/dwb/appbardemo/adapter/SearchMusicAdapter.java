package com.dwb.appbardemo.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dwb.appbardemo.R;
import com.dwb.appbardemo.model.LocalMusicBean;

import java.util.List;

public class SearchMusicAdapter extends RecyclerView.Adapter<SearchMusicAdapter.SearchMusicViewHolder>{
    Context context;
    List<LocalMusicBean> mDatas;




    public SearchMusicAdapter(Context context, List<LocalMusicBean> mDatas) {
        this.context = context;
        this.mDatas = mDatas;
    }

    @NonNull
    @Override
    public SearchMusicViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_local_music, parent, false);
        SearchMusicViewHolder holder = new SearchMusicViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull SearchMusicViewHolder holder, int position) {
        LocalMusicBean musicBean = mDatas.get(position);
        holder.idTv.setText(musicBean.getId());
        holder.songTv.setText(musicBean.getSong());
        holder.SingerTv.setText(musicBean.getSinger());
        holder.albumTv.setText(musicBean.getAlbum());
        holder.timeTv.setText(musicBean.getDuration());

    }

    @Override
    public int getItemCount() {
        return mDatas.size();
    }

    class SearchMusicViewHolder extends RecyclerView.ViewHolder {
        TextView idTv, songTv, SingerTv, albumTv, timeTv;

        public SearchMusicViewHolder(@NonNull View itemView) {
            super(itemView);
            idTv = itemView.findViewById(R.id.item_local_music_num);
            songTv = itemView.findViewById(R.id.item_local_music_song);
            SingerTv = itemView.findViewById(R.id.item_local_music_singer);
            albumTv = itemView.findViewById(R.id.item_local_music_album);
            timeTv = itemView.findViewById(R.id.item_local_music_duition);
        }
    }
}
