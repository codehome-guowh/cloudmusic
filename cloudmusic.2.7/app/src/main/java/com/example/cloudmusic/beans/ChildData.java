package com.example.cloudmusic.beans;

public class ChildData {
    private int songSheetHeaderId;
    private String songSheetName;
    private int songCount;

    public ChildData(int songSheetHeaderId, String songSheetName, int songCount) {
        this.songSheetHeaderId = songSheetHeaderId;
        this.songSheetName = songSheetName;
        this.songCount = songCount;
    }

    public ChildData() {
    }

    public int getSongSheetHeaderId() {
        return songSheetHeaderId;
    }

    public void setSongSheetHeaderId(int songSheetHeaderId) {
        this.songSheetHeaderId = songSheetHeaderId;
    }

    public String getSongSheetName() {
        return songSheetName;
    }

    public void setSongSheetName(String songSheetName) {
        this.songSheetName = songSheetName;
    }

    public int getSongCount() {
        return songCount;
    }

    public void setSongCount(int songCount) {
        this.songCount = songCount;
    }

    @Override
    public String toString() {
        return "ChildData{" +
                "songSheetHeaderId=" + songSheetHeaderId +
                ", songSheetName='" + songSheetName + '\'' +
                ", songCount='" + songCount + '\'' +
                '}';
    }
}
