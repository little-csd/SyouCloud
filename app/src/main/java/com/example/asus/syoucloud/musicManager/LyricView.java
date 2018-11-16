package com.example.asus.syoucloud.musicManager;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.AttributeSet;

import java.util.ArrayList;
import java.util.List;

public class LyricView extends android.support.v7.widget.AppCompatTextView {
    private static final int DY = 100;
    private List<String> wordList = new ArrayList<>();
    private List<Integer> timeList = new ArrayList<>();
    private Paint mLoseFocusPaint;
    private Paint mOnFocusPaint;
    private float mX = 0;
    private float mMiddleY = 0;
    private float mY = 0;
    private int mIndex = 0;

    public LyricView(Context context) {
        super(context);
        init();
    }

    public LyricView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public LyricView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public List<Integer> getTimeList() {
        return timeList;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        Paint p = mLoseFocusPaint;
        p.setTextAlign(Paint.Align.CENTER);
        Paint p2 = mOnFocusPaint;
        p2.setTextAlign(Paint.Align.CENTER);

        canvas.drawText(wordList.get(mIndex), mX, mMiddleY, p2);

        int alphaValue = 30;
        float tempY = mMiddleY;
        for (int i = mIndex - 1; i >= 0; i--) {
            tempY -= DY;
            if (tempY < 0) break;
            p.setAlpha(255 - alphaValue);
            canvas.drawText(wordList.get(i), mX, tempY, p);
            alphaValue += 30;
        }
        alphaValue = 30;
        tempY = mMiddleY;
        for (int i = mIndex + 1; i <= wordList.size(); i++) {
            tempY += DY;
            if (tempY > mY) break;
            p.setAlpha(255 - alphaValue);
            canvas.drawText(wordList.get(i), mX, tempY, p);
            alphaValue += 30;
        }
        mIndex ++;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mX = w * 0.5f;
        mY = h;
        mMiddleY = h * 0.5f;
    }

    public void setIndex(int index) {
        mIndex = index;
    }

    private void init() {
        setFocusable(true);

        LrcHandle lrcHandle = new LrcHandle();
        lrcHandle.readLRC("/storage/emulated/0/Download/七里香.lrc");
        wordList = lrcHandle.getWordList();
        timeList = lrcHandle.getTimeList();

        mLoseFocusPaint = new Paint();
        mLoseFocusPaint.setAntiAlias(true);
        mLoseFocusPaint.setTextSize(44);
        mLoseFocusPaint.setColor(Color.WHITE);
        mLoseFocusPaint.setTypeface(Typeface.SERIF);

        mOnFocusPaint = new Paint();
        mOnFocusPaint.setAntiAlias(true);
        mOnFocusPaint.setTextSize(60);
        mOnFocusPaint.setColor(Color.BLUE);
        mOnFocusPaint.setTypeface(Typeface.SANS_SERIF);
    }
}
