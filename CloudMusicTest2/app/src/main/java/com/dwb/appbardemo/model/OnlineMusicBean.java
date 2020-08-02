package com.dwb.appbardemo.model;

import cn.bmob.v3.BmobObject;

public class OnlineMusicBean extends BmobObject {

    private String id;
    private String song;
    private String singer;
    private String album;
    private String duration;
    private String path;
    private Boolean love;

    public OnlineMusicBean(String id, String song, String singer, String album, String duration, String path, Boolean love) {
        this.id = id;
        this.song = song;
        this.singer = singer;
        this.album = album;
        this.duration = duration;
        this.path = path;
        this.love = love;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSong() {
        return song;
    }

    public void setSong(String song) {
        this.song = song;
    }

    public String getSinger() {
        return singer;
    }

    public void setSinger(String singer) {
        this.singer = singer;
    }

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Boolean getLove() {
        return love;
    }

    public void setLove(Boolean love) {
        this.love = love;
    }

    @Override
    public String toString() {
        return "OnlineMusicBean{" +
                "id='" + id + '\'' +
                ", song='" + song + '\'' +
                ", singer='" + singer + '\'' +
                ", album='" + album + '\'' +
                ", duration='" + duration + '\'' +
                ", path='" + path + '\'' +
                ", love=" + love +
                '}';
    }
}
