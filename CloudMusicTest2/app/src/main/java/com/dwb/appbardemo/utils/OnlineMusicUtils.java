package com.dwb.appbardemo.utils;

import com.dwb.appbardemo.OnlineMusicActivity;
import com.dwb.appbardemo.model.OnlineMusicBean;

import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

public class OnlineMusicUtils {

    public static void ResolveMusicToList() {
        BmobQuery<OnlineMusicBean> bmobQuery = new BmobQuery<>();
        bmobQuery.findObjects(new FindListener<OnlineMusicBean>() {
            @Override
            public void done(List<OnlineMusicBean> list, BmobException e) {
                if (e == null) {
                    for (int i = 0; i < list.size(); i++) {
                        String sid = "" + (i + 1);
                        String song = list.get(i).getSong();
                        String singer = list.get(i).getSinger();
                        String album = list.get(i).getAlbum();
                        String path = list.get(i).getPath();
                        Boolean love = list.get(i).getLove();
                        String time = list.get(i).getDuration();
                        OnlineMusicBean bean = new OnlineMusicBean(sid, song, singer, album, time, path, love);
                        OnlineMusicActivity.mDatas.add(bean);

                    }
                }
            }
        });
    }
}
