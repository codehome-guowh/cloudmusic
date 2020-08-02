package com.example.neteasemusic.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.neteasemusic.R;
import com.example.neteasemusic.view.SimpleVideoView;
import com.example.neteasemusic.adapter.VideoAdapter;
import com.example.neteasemusic.bean.Video;
import com.example.neteasemusic.util.VideoUtil;

import java.util.List;

public class MyFragmentVideo extends Fragment {
    private View view;
    private Context context;
    private RecyclerView mRv;
    private List<Video> videos;
    private SimpleVideoView video;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.layout_list, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        context = view.getContext();
        mRv = view.findViewById(R.id.rv_main);
        video = view.findViewById(R.id.video);
        mRv.setLayoutManager(new LinearLayoutManager(context));
        videos = VideoUtil.init(context);
        mRv.setAdapter(new VideoAdapter(context, videos));
    }

    public SimpleVideoView getVideo() {
        return video;
    }
}
