package com.example.cloudmusic.adapter;

import android.content.Context;
import android.provider.ContactsContract;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cloudmusic.R;
import com.example.cloudmusic.bean.Song;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.w3c.dom.Text;

import java.util.List;

import javax.crypto.spec.PSource;

import ie.moses.recyclerviewadapter.core.OnItemClickListener;
import ie.moses.recyclerviewadapter.core.RecyclerViewAdapter;

public class LocalMusicListAdapter extends RecyclerViewAdapter<RecyclerView.ViewHolder,List<Song>> {



    private Context mContext;

    private List<Song> songList;

    private OnItemClickListener mOnItemClickListener;



    static class ViewHolder extends RecyclerView.ViewHolder{

        TextView textSongName;

        TextView textSinger;

        TextView textId;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textId = itemView.findViewById(R.id.text_song_id);
            textSinger = itemView.findViewById(R.id.text_singer);
            textSongName = itemView.findViewById(R.id.text_song_name);
        }
    }

    public LocalMusicListAdapter(@NotNull Context context, @NotNull List data, @Nullable OnItemClickListener
            onItemClickListener) {
        super(context, data, onItemClickListener);
        mContext = context;
        songList = data;
        mOnItemClickListener = onItemClickListener;

    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = parent.inflate(mContext, R.layout.item_localmusic,parent);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        ViewHolder holder = (ViewHolder)viewHolder;
        Song song = songList.get(position);
        holder.textId.setText(song.getId());
        holder.textSongName.setText(song.getSongName());
        holder.textSinger.setText(song.getSinger());
    }


    @Override
    public int getItemCount() {
        return songList.size();
    }
}
