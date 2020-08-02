package com.example.cloudmusic.bean;

public class Song {

    private String id;
    private String songName;
    private String singer;
    private String duration;
    private String path;

    public Song() {
    }

    public Song(String id, String song, String singer, String duration, String path) {
        this.id = id;
        this.songName = song;
        this.singer = singer;
        this.duration = duration;
        this.path = path;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSongName() {
        return songName;
    }

    public void setSongName(String songName) {
        this.songName = songName;
    }

    public String getSinger() {
        return singer;
    }

    public void setSinger(String singer) {
        this.singer = singer;
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

}
