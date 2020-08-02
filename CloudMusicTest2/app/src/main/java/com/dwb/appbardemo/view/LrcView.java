package com.dwb.appbardemo.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;


import com.dwb.appbardemo.lrcface.ILrcView;
import com.dwb.appbardemo.lrcface.ILrcViewListener;
import com.dwb.appbardemo.model.LrcRow;

import java.util.List;


public class LrcView extends View implements ILrcView {

    public final static String TAG = "LrcView";

    public final static int DISPLAY_MODE_NORMAL = 0;

    public final static int DISPLAY_MODE_SEEK = 1;

    public final static int DISPLAY_MODE_SCALE = 2;

    private int mDisplayMode = DISPLAY_MODE_NORMAL;

    private List<LrcRow> mLrcRows;

    private int mMinSeekFiredOffset = 10;

    private int mHighLightRow = 0;

    private int mHighLightRowColor = Color.YELLOW;

    private int mNormalRowColor = Color.WHITE;

    private int mSeekLineColor = Color.CYAN;

    private int mSeekLineTextColor = Color.CYAN;

    private int mSeekLineTextSize = 15;

    private int mMinSeekLineTextSize = 13;

    private int mMaxSeekLineTextSize = 18;

    private int mLrcFontSize = 40;    // font size of lrc

    private int mMinLrcFontSize = 15;

    private int mMaxLrcFontSize = 40;

    private int mPaddingY = 10;

    private int mSeekLinePaddingX = 0;

    private ILrcViewListener mLrcViewListener;

    private String mLoadingLrcTip = "抱歉，暂无此音乐歌词！";

    private Paint mPaint;

    long currentMillis;

    private int MODE_HIGH_LIGHT_NORMAL = 0;

    private int MODE_HIGH_LIGHT_KARAOKE = 1;


    private int mode = MODE_HIGH_LIGHT_KARAOKE;


    public LrcView(Context context, AttributeSet attr) {
        super(context, attr);
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setTextSize(mLrcFontSize);
    }

    public void setListener(ILrcViewListener listener) {
        mLrcViewListener = listener;
    }

    public void setLoadingTipText(String text) {
        mLoadingLrcTip = text;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        final int height = getHeight(); // height of this view
        final int width = getWidth(); // width of this view
        if (mLrcRows == null || mLrcRows.size() == 0) {
            if (mLoadingLrcTip != null) {
                // draw tip when no lrc.
                mPaint.setColor(mHighLightRowColor);
                mPaint.setTextSize(mLrcFontSize);
                mPaint.setTextAlign(Paint.Align.CENTER);
                canvas.drawText(mLoadingLrcTip, width / 2, height / 2 - mLrcFontSize, mPaint);
            }
            return;
        }

        int rowY = 0; // vertical point of each row.
        final int rowX = width / 2;
        int rowNum = 0;


        int highlightRowY = height / 2 - mLrcFontSize;

        if (mode == MODE_HIGH_LIGHT_KARAOKE){
            drawKaraokeHighLightLrcRow(canvas, width, rowX, highlightRowY);
        } else {
            drawHighLrcRow(canvas, height, rowX, highlightRowY);
        }

        if (mDisplayMode == DISPLAY_MODE_SEEK) {
            mPaint.setColor(mSeekLineColor);
            canvas.drawLine(mSeekLinePaddingX, highlightRowY + mPaddingY, width - mSeekLinePaddingX, highlightRowY + mPaddingY, mPaint);

            mPaint.setColor(mSeekLineTextColor);
            mPaint.setTextSize(mSeekLineTextSize);
            mPaint.setTextAlign(Paint.Align.LEFT);
            canvas.drawText(mLrcRows.get(mHighLightRow).startTimeString, 0, highlightRowY, mPaint);
        }

        mPaint.setColor(mNormalRowColor);
        mPaint.setTextSize(mLrcFontSize);
        mPaint.setTextAlign(Paint.Align.CENTER);
        rowNum = mHighLightRow - 1;
        rowY = highlightRowY - mPaddingY - mLrcFontSize;


        while (rowY > -mLrcFontSize && rowNum >= 0) {
            String text = mLrcRows.get(rowNum).content;
            canvas.drawText(text, rowX, rowY, mPaint);
            rowY -= (mPaddingY + mLrcFontSize);
            rowNum--;
        }

        rowNum = mHighLightRow + 1;
        rowY = highlightRowY + mPaddingY + mLrcFontSize;



        while (rowY < height && rowNum < mLrcRows.size()) {
            String text = mLrcRows.get(rowNum).content;
            canvas.drawText(text, rowX, rowY, mPaint);
            rowY += (mPaddingY + mLrcFontSize);
            rowNum++;
        }

    }

    private void drawKaraokeHighLightLrcRow(Canvas canvas, int width, int rowX, int highlightRowY) {
        LrcRow highLrcRow = mLrcRows.get(mHighLightRow);
        String highlightText = highLrcRow.content;

        mPaint.setColor(mNormalRowColor);
        mPaint.setTextSize(mLrcFontSize);
        mPaint.setTextAlign(Paint.Align.CENTER);
        canvas.drawText(highlightText, rowX, highlightRowY, mPaint);

        int highLineWidth = (int) mPaint.measureText(highlightText);
        int leftOffset = (width - highLineWidth) / 2;
        long start = highLrcRow.getStartTime();
        long end = highLrcRow.getEndTime();
        int highWidth = (int) ((currentMillis - start) * 1.0f / (end - start) * highLineWidth);
        if (highWidth > 0) {
            mPaint.setColor(mHighLightRowColor);
            Bitmap textBitmap = Bitmap.createBitmap(highWidth, highlightRowY + mPaddingY, Bitmap.Config.ARGB_8888);
            Canvas textCanvas = new Canvas(textBitmap);
            textCanvas.drawText(highlightText, highLineWidth / 2, highlightRowY, mPaint);
            canvas.drawBitmap(textBitmap, leftOffset, 0, mPaint);
        }
    }

    private void drawHighLrcRow(Canvas canvas, int height, int rowX, int highlightRowY) {
        String highlightText = mLrcRows.get(mHighLightRow).content;
        mPaint.setColor(mHighLightRowColor);
        mPaint.setTextSize(mLrcFontSize);
        mPaint.setTextAlign(Paint.Align.CENTER);
        canvas.drawText(highlightText, rowX, highlightRowY, mPaint);
    }


    public void seekLrc(int position, boolean cb) {
        if (mLrcRows == null || position < 0 || position > mLrcRows.size()) {
            return;
        }
        LrcRow lrcRow = mLrcRows.get(position);
        mHighLightRow = position;
        invalidate();
        if (mLrcViewListener != null && cb) {
            mLrcViewListener.onLrcSought(position, lrcRow);
        }
    }

    private float mLastMotionY;

    private PointF mPointerOneLastMotion = new PointF();

    private PointF mPointerTwoLastMotion = new PointF();

    private boolean mIsFirstMove = false;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (mLrcRows == null || mLrcRows.size() == 0) {
            return super.onTouchEvent(event);
        }
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                Log.d(TAG, "down,mLastMotionY:" + mLastMotionY);
                mLastMotionY = event.getY();
                mIsFirstMove = true;
                invalidate();
                break;
            case MotionEvent.ACTION_MOVE:
                if (event.getPointerCount() == 2) {
                    Log.d(TAG, "two move");
                    doScale(event);
                    return true;
                }
                Log.d(TAG, "one move");
                if (mDisplayMode == DISPLAY_MODE_SCALE) {
                    //if scaling but pointer become not two ,do nothing.
                    return true;
                }
                doSeek(event);
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                if (mDisplayMode == DISPLAY_MODE_SEEK) {
                    seekLrc(mHighLightRow, true);
                }
                mDisplayMode = DISPLAY_MODE_NORMAL;
                invalidate();
                break;
        }
        return true;
    }

    /**
     * 处理双指在屏幕移动时的，歌词大小缩放
     */
    private void doScale(MotionEvent event) {
        if (mDisplayMode == DISPLAY_MODE_SEEK) {
            mDisplayMode = DISPLAY_MODE_SCALE;
            Log.d(TAG, "change mode from DISPLAY_MODE_SEEK to DISPLAY_MODE_SCALE");
            return;
        }
        if (mIsFirstMove) {
            mDisplayMode = DISPLAY_MODE_SCALE;
            invalidate();
            mIsFirstMove = false;
            setTwoPointerLocation(event);
        }
        int scaleSize = getScale(event);
        Log.d(TAG, "scaleSize:" + scaleSize);
        if (scaleSize != 0) {
            setNewFontSize(scaleSize);
            invalidate();
        }
        setTwoPointerLocation(event);
    }

    private void doSeek(MotionEvent event) {
        float y = event.getY();
        float offsetY = y - mLastMotionY;
        if (Math.abs(offsetY) < mMinSeekFiredOffset) {
            return;
        }
        mDisplayMode = DISPLAY_MODE_SEEK;
        int rowOffset = Math.abs((int) offsetY / mLrcFontSize); //歌词要滚动的行数

        Log.d(TAG, "move to new hightlightrow : " + mHighLightRow + " offsetY: " + offsetY + " rowOffset:" + rowOffset);

        if (offsetY < 0) {
            mHighLightRow += rowOffset;
        } else if (offsetY > 0) {
            mHighLightRow -= rowOffset;
        }
        mHighLightRow = Math.max(0, mHighLightRow);
        mHighLightRow = Math.min(mHighLightRow, mLrcRows.size() - 1);
        if (rowOffset > 0) {
            mLastMotionY = y;
            invalidate();
        }
    }

    private void setTwoPointerLocation(MotionEvent event) {
        mPointerOneLastMotion.x = event.getX(0);
        mPointerOneLastMotion.y = event.getY(0);
        mPointerTwoLastMotion.x = event.getX(1);
        mPointerTwoLastMotion.y = event.getY(1);
    }

    private void setNewFontSize(int scaleSize) {
        //设置歌词缩放后的的最新字体大小
        mLrcFontSize += scaleSize;
        mLrcFontSize = Math.max(mLrcFontSize, mMinLrcFontSize);
        mLrcFontSize = Math.min(mLrcFontSize, mMaxLrcFontSize);
        mSeekLineTextSize += scaleSize;
        mSeekLineTextSize = Math.max(mSeekLineTextSize, mMinSeekLineTextSize);
        mSeekLineTextSize = Math.min(mSeekLineTextSize, mMaxSeekLineTextSize);
    }

    private int getScale(MotionEvent event) {
        Log.d(TAG, "scaleSize getScale");
        float x0 = event.getX(0);
        float y0 = event.getY(0);
        float x1 = event.getX(1);
        float y1 = event.getY(1);
        float maxOffset = 0; // max offset between x or y axis,used to decide scale size
        boolean zooMin = false;
        float oldXOffset = Math.abs(mPointerOneLastMotion.x - mPointerTwoLastMotion.x);
        float newXOffset = Math.abs(x1 - x0);
        float oldYOffset = Math.abs(mPointerOneLastMotion.y - mPointerTwoLastMotion.y);
        float newYOffset = Math.abs(y1 - y0);
        maxOffset = Math.max(Math.abs(newXOffset - oldXOffset), Math.abs(newYOffset - oldYOffset));
        if (maxOffset == Math.abs(newXOffset - oldXOffset)) {
            zooMin = newXOffset > oldXOffset ? true : false;
        }
        else {
            zooMin = newYOffset > oldYOffset ? true : false;
        }
        Log.d(TAG, "scaleSize maxOffset:" + maxOffset);
        if (zooMin) {
            return (int) (maxOffset / 10);//放大双指之间移动的最大差距的1/10
        } else {
            return -(int) (maxOffset / 10);//缩小双指之间移动的最大差距的1/10
        }
    }


    public void setLrc(List<LrcRow> lrcRows) {
        mLrcRows = lrcRows;
        invalidate();
    }


    public void seekLrcToTime(long time) {
        if (mLrcRows == null || mLrcRows.size() == 0) {
            return;
        }
        if (mDisplayMode != DISPLAY_MODE_NORMAL) {
            return;
        }

        currentMillis = time;
        Log.d(TAG, "seekLrcToTime:" + time);

        for (int i = 0; i < mLrcRows.size(); i++) {
            LrcRow current = mLrcRows.get(i);
            LrcRow next = i + 1 == mLrcRows.size() ? null : mLrcRows.get(i + 1);
            if ((time >= current.startTime && next != null && time < next.startTime)
                    || (time > current.startTime && next == null)) {
                seekLrc(i, false);
                return;
            }
        }
    }
}
