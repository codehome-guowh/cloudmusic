package com.dwb.appbardemo.lrcface;

import com.dwb.appbardemo.model.LrcRow;

public interface ILrcViewListener {
    /**
     * 当歌词被用户上下拖动的时候回调该方法    (ps:seek的过去式和过去分词为sought)
     */
    void onLrcSought(int newPosition, LrcRow row);

}
