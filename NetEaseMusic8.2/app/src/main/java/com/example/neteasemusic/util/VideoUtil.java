package com.example.neteasemusic.util;

import android.content.Context;

import com.example.neteasemusic.R;
import com.example.neteasemusic.bean.Video;

import java.util.ArrayList;
import java.util.List;

public class VideoUtil {
    private Context context;
    private List<Video> videos;

    public VideoUtil(Context context, List<Video> videos) {
        this.context = context;
        this.videos = videos;
    }

    public static List<Video> init(Context context) {
        List<Video> videos = new ArrayList<>();
        //test本地加
        videos.add(new Video("广岛之恋", R.drawable.head_gdzl,"android.resource://" +
                context.getPackageName() + "/" + R.raw.gdzl,"莫文蔚", R.drawable.cover_gdzl,2936));
        videos.add(new Video("重启",R.drawable.head_cq,"android.resource://" +
                context.getPackageName() + "/" + R.raw.cq,"张韶涵", R.drawable.cover_cq,8999));
        videos.add(new Video("夹缝中的人",R.drawable.head_jfzdr,"android.resource://" +
                context.getPackageName() + "/" + R.raw.jfzdr,"陈翔", R.drawable.cover_jfzdr,21985));
        videos.add(new Video("南昌街王子",R.drawable.head_ncjwz,"android.resource://" +
                context.getPackageName() + "/" + R.raw.ncjwz,"薛凯琪", R.drawable.cover_nsjwz,999));
        return videos;
    }
}
