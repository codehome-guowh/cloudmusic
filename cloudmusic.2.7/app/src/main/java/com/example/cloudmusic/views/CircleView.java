package com.example.cloudmusic.views;



import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Path;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatImageView;

public class CircleView extends AppCompatImageView {
    int width;
    int height;
    public CircleView(Context context) {
        super(context);
    }

    public CircleView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onDraw(Canvas canvas)
    {
        width = getMeasuredWidth();
        height = getMeasuredHeight();
        Path path = new Path();
        path.addCircle(width/2,width/2,width/2, Path.Direction.CW);
        canvas.clipPath(path);
        super.onDraw(canvas);
    }









}
