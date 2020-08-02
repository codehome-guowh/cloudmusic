package com.example.cloudmusic.views;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ExpandableListView;

public class ExpandableScrollListView extends ExpandableListView {
    public ExpandableScrollListView(Context context) {
        super(context);
    }

    public ExpandableScrollListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ExpandableScrollListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,

                MeasureSpec.AT_MOST);

        //将重新计算的高度传递回去
        super.onMeasure(widthMeasureSpec, expandSpec);
    }
}
