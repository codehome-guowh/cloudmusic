package com.example.cloudmusic.views;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;

/**
 * 可滚动的ListView，这里是因为这个ListView被包裹在一个ScrollView中才需要设置纵向拉伸
 */

public class ScrollListView extends ListView {
    public ScrollListView(Context context) {
        super(context);
    }

    public ScrollListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int height = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, height);
    }
}