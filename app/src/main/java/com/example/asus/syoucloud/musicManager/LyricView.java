package com.example.asus.syoucloud.musicManager;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class LyricView extends android.support.v7.widget.AppCompatTextView {
    public static final int DT = 60;
    private static final int DY = 120;
    private List<Lyric> lyricList = new ArrayList<>();
    private Paint mLoseFocusPaint;
    private Paint mOnFocusPaint;
    private MusicService.MusicPlayer musicPlayer;
    private float mX = 0;
    private float mMiddleY = 0;
    private float mY = 0;
    private int moveStep = 0;
    private int dMove = 12;
    private boolean hasIndex[];
    private boolean hasTranslate = false;

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

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (moveStep > 0) moveStep--;
        int progress = musicPlayer.getCurrentProgress();
        int index = 0;
        for (int i = 1; i < lyricList.size(); i++) {
            if (lyricList.get(i).getTime() > progress) {
                index = i - 1;
                if (!hasIndex[index]) {
                    hasIndex[index] = true;
                    moveStep = 10;
                }
                break;
            }
        }

        Paint p = mLoseFocusPaint;
        p.setTextAlign(Paint.Align.CENTER);
        Paint p2 = mOnFocusPaint;
        p2.setTextAlign(Paint.Align.CENTER);

        canvas.drawText(lyricList.get(index).getText(), mX, mMiddleY + moveStep * dMove, p2);

        int alphaValue = 30;
        float tempY = mMiddleY + moveStep * dMove;
        for (int i = index - 1; i >= 0; i--) {
            tempY -= DY;
            if (tempY < 0) break;
            Lyric lyric = lyricList.get(i);
            p.setAlpha(255 - alphaValue);
            if (hasTranslate) {
                if (lyric.getTranslate() != null)
                    canvas.drawText(lyric.getTranslate(), mX, tempY, p);
                tempY -= DT;
                if (tempY < 0) break;
            }
            canvas.drawText(lyricList.get(i).getText(), mX, tempY, p);
            alphaValue += 30;
        }

        if (lyricList.get(index).getTranslate() != null)
            canvas.drawText(lyricList.get(index).getTranslate(), mX, mMiddleY + DT + moveStep * dMove, p2);
        if (hasTranslate) tempY = mMiddleY + moveStep * dMove + DT;
        else tempY = mMiddleY + moveStep * dMove;
        alphaValue = 30;
        for (int i = index + 1; i < lyricList.size(); i++) {
            tempY += DY;
            if (tempY > mY) break;
            Lyric lyric = lyricList.get(i);
            p.setAlpha(255 - alphaValue);
            canvas.drawText(lyric.getText(), mX, tempY, p);
            if (hasTranslate) {
                tempY += DT;
                if (tempY > mY) break;
                if (lyric.getTranslate() != null)
                    canvas.drawText(lyric.getTranslate(), mX, tempY, p);
            }
            alphaValue += 30;
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mX = w * 0.5f;
        mY = h;
        mMiddleY = h * 0.4f;
    }

    public void setMusicPlayer(MusicService.MusicPlayer musicPlayer) {
        this.musicPlayer = musicPlayer;
    }

    public void seekTo(int progress) {
        for (int i = lyricList.size() - 1; i >= 0; i--)
            if (lyricList.get(i).getTime() > progress) hasIndex[i] = false;
            else break;
    }

    private void init() {
        setFocusable(true);

        hasIndex = new boolean[100];
        LrcHandle lrcHandle = new LrcHandle();
        lrcHandle.readLRC("/storage/emulated/0/Download/鳥の詩.lrc");
        lyricList = lrcHandle.getLyricList();

        for (int i = 0; i < lyricList.size(); i++)
            if (lyricList.get(i).getTranslate() != null) {
                dMove = (DY + DT) / 10;
                hasTranslate = true;
                break;
            }

        mLoseFocusPaint = new Paint();
        mLoseFocusPaint.setAntiAlias(true);
        mLoseFocusPaint.setTextSize(50);
        mLoseFocusPaint.setColor(Color.WHITE);
        mLoseFocusPaint.setTypeface(Typeface.SERIF);

        mOnFocusPaint = new Paint();
        mOnFocusPaint.setAntiAlias(true);
        mOnFocusPaint.setTextSize(50);
        mOnFocusPaint.setColor(Color.BLUE);
        mOnFocusPaint.setTypeface(Typeface.SANS_SERIF);
    }

    private boolean hasLyric() {
        return lyricList != null && lyricList.size() > 0;
    }

}
