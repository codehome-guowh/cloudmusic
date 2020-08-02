package com.example.neteasemusic.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.neteasemusic.R;
import com.example.neteasemusic.bean.Song;
import com.example.neteasemusic.db.MyDatabaseHelper;
import com.example.neteasemusic.db.Mydb;
import com.example.neteasemusic.util.MusicUtil;

import java.util.ArrayList;
import java.util.List;

public class MusicAdapter extends BaseAdapter {
    Context context;
    private List<Song> songList;
    private LayoutInflater inflater;
    public static int selected = -1;

    public MusicAdapter(Context context, List<Song> songsList) {
        this.context = context;
        this.songList = songsList;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return songList.size();
    }

    @Override
    public Object getItem(int i) {
        return songList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {

        view = inflater.inflate(R.layout.musicitem, null);
        TextView title = view.findViewById(R.id.musicTitle);
        TextView artist = view.findViewById(R.id.musicartist);
        TextView duration = view.findViewById(R.id.musicduration);
        ImageView isplaying = view.findViewById(R.id.isplaying);

        ImageButton imageButton = (ImageButton) view.findViewById(R.id.love);

        Song song = songList.get(i);

        title.setText(song.getTitle());
        artist.setText(song.getArtist());
        duration.setText(MusicUtil.formatTime(song.getDuration()));
        if (selected == i) {
            isplaying.setVisibility(View.VISIBLE);
        }

        initLove(title, imageButton);

        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemLoveButtonListener1.onLoveClick(i);
            }
        });
        return view;
    }

    public interface onItemLoveButtonListener {
        void onLoveClick(int i);
    }

    private onItemLoveButtonListener onItemLoveButtonListener1;

    public void setOnItemLoveButtonListener(onItemLoveButtonListener mOnItemDeleteListener) {
        this.onItemLoveButtonListener1 = mOnItemDeleteListener;
    }

    public void initLove(TextView textView, ImageButton imageButton) {
        MyDatabaseHelper helper = new MyDatabaseHelper(context, "musicdb1", null, 1);
        final Mydb mydb = new Mydb(helper);
        List<ArrayList<String>> love_list = mydb.query();

        boolean isTrue = false;
        ArrayList<String> loveName;
        if (love_list.size() != 0) {
            loveName = love_list.get(0);
            for (int j = 0; j < loveName.size(); j++) {
                if (textView.getText().toString().equals(loveName.get(j))) {
                    isTrue = true;
                }
            }
            if (isTrue) {
                imageButton.setBackgroundResource(R.drawable.love);
            } else {
                imageButton.setBackgroundResource(R.drawable.notlove);
            }
        } else {
            Toast.makeText(context, "未查到数据", Toast.LENGTH_LONG).show();
        }

    }
}
