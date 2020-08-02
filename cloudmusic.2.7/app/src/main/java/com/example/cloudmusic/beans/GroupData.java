package com.example.cloudmusic.beans;

public class GroupData {
    private String sonSheetClass;
    private String sonSheetCount;

    public GroupData(String sonsheetclass, String sonsheetcount) {
        this.sonSheetClass = sonsheetclass;
        this.sonSheetCount = sonsheetcount;
    }

    public GroupData() {
    }

    public String getSonsheetclass() {
        return sonSheetClass;
    }

    public void setSonsheetclass(String sonsheetclass) {
        this.sonSheetClass = sonsheetclass;
    }

    public String getSonsheetcount() {
        return sonSheetCount;
    }

    public void setSonsheetcount(String sonsheetcount) {
        this.sonSheetCount = sonsheetcount;
    }

    @Override
    public String toString() {
        return "GroupData{" +
                "sonsheetclass='" + sonSheetClass + '\'' +
                ", sonsheetcount='" + sonSheetCount + '\'' +
                '}';
    }
}
