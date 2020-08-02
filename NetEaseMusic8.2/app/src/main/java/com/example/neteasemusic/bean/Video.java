package com.example.neteasemusic.bean;

public class Video {

    private String title;

    private int headImg;

    private String path;

    private String user;

    private int cover;

    private int fingerNumber;

    //private int commend;
    public String getTitle() {
        return title;
    }

    public int getHeadImg() {
        return headImg;
    }

    public String getPath() {
        return path;
    }

    public String getUser() {
        return user;
    }

    public int getCover() {
        return cover;
    }

    public int getFingerNumber() {
        return fingerNumber;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setHeadImg(int headImg) {
        this.headImg = headImg;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public void setCover(int cover) {
        this.cover = cover;
    }

    public void setFingerNumber(int fingerNumber) {
        this.fingerNumber = fingerNumber;
    }

    public Video(String title, int headImg, String path, String user, int cover, int fingerNumber) {
        this.title = title;
        this.headImg = headImg;
        this.path = path;
        this.user = user;
        this.cover = cover;
        this.fingerNumber = fingerNumber;
    }
}
