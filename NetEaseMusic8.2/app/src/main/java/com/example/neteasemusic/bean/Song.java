package com.example.neteasemusic.bean;

import android.graphics.Bitmap;

public class Song {

    private String title;

    private String artist;

    private String data;

    private int duration;


    private long size;

    private Bitmap image;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String arttist) {
        this.artist = artist;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public Bitmap getImage() {
        return image;
    }

    public void setImage(Bitmap image) {
        this.image = image;
    }

    public Song(String title, String artist, String data, int duration, long size) {
        this.title = title;
        this.artist = artist;
        this.data = data;
        this.duration = duration;
        this.size = size;
    }

    public Song(String title, String artist, String data, int duration, long size, Bitmap image) {
        this.title = title;
        this.artist = artist;
        this.data = data;
        this.duration = duration;
        this.size = size;
        this.image = image;
    }
}
