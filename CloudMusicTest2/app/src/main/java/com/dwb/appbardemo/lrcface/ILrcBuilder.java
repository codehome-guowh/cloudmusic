package com.dwb.appbardemo.lrcface;

import com.dwb.appbardemo.model.LrcRow;

import java.util.List;

public interface ILrcBuilder {


    /**
     * 解析歌词，得到LrcRow的集合
     * @param rawLrc lrc内容
     * @return LrcRow的集合
     */
    List<LrcRow> getLrcRows(String rawLrc);

}
