package com.example.asus.syoucloud.customView;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Looper;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.Scroller;

import com.example.asus.syoucloud.bean.LyricItem;
import com.example.asus.syoucloud.util.Constant;
import com.example.asus.syoucloud.util.TimeUtil;

import java.util.ArrayList;
import java.util.List;

public class LyricView extends View {
    public static final int ADJUST_CENTER = 200;
    public static final int TIMELINE_KEEP_TIME = 2000;

    private List<LyricItem> lyricList = new ArrayList<>();
    private Paint textPaint;
    private Paint timePaint;
    private ValueAnimator mAnimator;
    private GestureDetector mGestureDetector;
    private Scroller mScroller;
    private onLyricListener listener;
    private float mOffset;
    private float mSpacing;
    private boolean isShowTimeLine;
    private boolean isTouch;
    private boolean isFling;
    private int mCurrentLine;
    private int normalTextColor;
    private int currentTextColor;
    private int mAnimationDuration;
    private int timePadding;
    private int timeTextPadding;
    private int timeTextHeight;

    private Runnable hideTimelineRunnable = new Runnable() {
        @Override
        public void run() {
            if (hasLyric() && isShowTimeLine) {
                isShowTimeLine = false;
                scroll(mCurrentLine);
            }
        }
    };

    private GestureDetector.SimpleOnGestureListener mSimpleOnGestureListener
            = new GestureDetector.SimpleOnGestureListener() {
        @Override
        public boolean onDown(MotionEvent e) {
            if (hasLyric()) {
                mScroller.forceFinished(true);
                removeCallbacks(hideTimelineRunnable);
                isTouch = true;
                isShowTimeLine = true;
                invalidate();
                return true;
            }
            return super.onDown(e);
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            if (hasLyric()) {
                mOffset += -distanceY;
                mOffset = Math.min(mOffset, getOffset(0));
                mOffset = Math.max(mOffset, getOffset(lyricList.size() - 1));
                invalidate();
                return true;
            }
            return super.onScroll(e1, e2, distanceX, distanceY);
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            if (hasLyric()) {
                mScroller.fling(0, (int) mOffset, 0, (int) velocityY, 0,
                        0, (int) getOffset(lyricList.size() - 1), (int) getOffset(0));
                isFling = true;
                return true;
            }
            return super.onFling(e1, e2, velocityX, velocityY);
        }

        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            if (hasLyric() && isShowTimeLine && contain((int) e.getX(), (int) e.getY())) {
                if (lyricList.size() == 1) {
                    listener.onDownloadLyric();
                    return true;
                }
                int centerLine = getCenterLine();
                int time = lyricList.get(centerLine).getTime();
                if (listener.onSeekTo(time)) {
                    isShowTimeLine = false;
                    removeCallbacks(hideTimelineRunnable);
                    mCurrentLine = centerLine;
                    invalidate();
                    return true;
                }
            }
            return false;
        }
    };

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

    public void init() {
        normalTextColor = Color.WHITE;
        currentTextColor = Color.BLUE;
        mSpacing = 60;
        mOffset = 0;
        mCurrentLine = 0;
        isShowTimeLine = false;
        mAnimationDuration = 1000;
        timePadding = 10;
        timeTextPadding = 100;
        timeTextHeight = Constant.DY;

        textPaint = new Paint();
        textPaint.setAntiAlias(true);
        textPaint.setTextSize(50);
        textPaint.setTextAlign(Paint.Align.CENTER);

        timePaint = new Paint();
        timePaint.setAntiAlias(true);
        timePaint.setAlpha(200);
        timePaint.setColor(Color.WHITE);

        mGestureDetector = new GestureDetector(getContext(), mSimpleOnGestureListener);
        mGestureDetector.setIsLongpressEnabled(false);
        mScroller = new Scroller(getContext());
    }

    public boolean contain(int x, int y) {
        int centerY = getHeight() / 2;
        return x >= timeTextPadding && x <= getWidth() - timeTextPadding &&
                y >= centerY - timeTextHeight / 2 && y <= centerY + timeTextHeight / 2;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL) {
            isTouch = false;
            if (hasLyric() && !isFling) {
                adjustCenter();
                postDelayed(hideTimelineRunnable, TIMELINE_KEEP_TIME);
            }
        }
        return mGestureDetector.onTouchEvent(event);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int centerLine = getCenterLine(), centerY = getHeight() / 2;
        if (isShowTimeLine) {
            timePaint.setTextSize(30);
            String time = TimeUtil.parseToString(lyricList.get(centerLine).getTime() / 1000);
            canvas.drawText(time, timePadding, centerY + 10, timePaint);
            canvas.drawLine(timeTextPadding, centerY, getWidth() - timeTextPadding,
                    centerY, timePaint);
        }

        canvas.translate(0, mOffset);

        float y = 0;
        for (int i = 0; i < lyricList.size(); i++) {
            if (i > 0) {
                y += (lyricList.get(i - 1).getHeight() + lyricList.get(i).getHeight()) / 2
                        + mSpacing;
            }
            if (i == mCurrentLine) textPaint.setColor(currentTextColor);
            else textPaint.setColor(normalTextColor);
            drawText(canvas, lyricList.get(i), y);
        }
    }

    public void drawText(Canvas canvas, LyricItem lyric, float y) {
        canvas.drawText(lyric.getText(), getWidth() / 2, y, textPaint);
        if (lyric.getTranslate() != null) {
            y += Constant.DT;
            canvas.drawText(lyric.getTranslate(), getWidth() / 2, y, textPaint);
        }
    }

    public void setLyricList(List<LyricItem> lyricList) {
        this.lyricList = lyricList;
        mCurrentLine = 0;
        mOffset = getHeight() / 2;
        isShowTimeLine = false;
        isTouch = false;
        isFling = false;
        removeCallbacks(hideTimelineRunnable);
        invalidate();
    }

    public void setSeekToListener(onLyricListener listener) {
        this.listener = listener;
    }

    public boolean hasLyric() {
        return !lyricList.isEmpty();
    }

    public int findCurrentLine(int time) {
        int l = 0, r = lyricList.size() - 1;
        while (l <= r) {
            int mid = (l + r) / 2;
            if (lyricList.get(mid).getTime() <= time) l = mid + 1;
            else r = mid - 1;
        }
        if (r == -1) r = 0;
        return r;
    }

    public int getCenterLine() {
        int centerLine = 0;
        float minDistance = Float.MAX_VALUE;
        for (int i = 0; i < lyricList.size(); i++) {
            float distance = Math.abs(mOffset - getOffset(i));
            if (distance < minDistance) {
                minDistance = distance;
                centerLine = i;
            }
        }
        return centerLine;
    }

    public float getOffset(int line) {
        if (lyricList.get(line).getOffset() != Float.MIN_VALUE)
            return lyricList.get(line).getOffset();
        float offset = getHeight() / 2;
        for (int i = 1; i <= line; i++) {
            offset -= (lyricList.get(i - 1).getHeight() + lyricList.get(i).getHeight()) / 2
                    + mSpacing;
        }
        lyricList.get(line).setOffset(offset);
        return offset;
    }

    public void updateTime(final int time) {
        runOnUi(() -> {
            if (!hasLyric()) return;
            int line = findCurrentLine(time);
            if (line != mCurrentLine) {
                mCurrentLine = line;
                if (!isShowTimeLine) scroll(line);
                else invalidate();
            }
        });
    }

    private void scroll(int line) {
        scroll(line, mAnimationDuration);
    }

    private void adjustCenter() {
        scroll(getCenterLine(), ADJUST_CENTER);
    }

    private void scroll(int line, int duration) {
        float offset = getOffset(line);
        endAnimation();

        mAnimator = ValueAnimator.ofFloat(mOffset, offset);
        mAnimator.setDuration(duration);
        mAnimator.setInterpolator(new LinearInterpolator());
        mAnimator.addUpdateListener(animation -> {
            mOffset = (float) animation.getAnimatedValue();
            invalidate();
        });
        mAnimator.start();
    }

    @Override
    public void computeScroll() {
        if (mScroller.computeScrollOffset()) {
            mOffset = mScroller.getCurrY();
            invalidate();
        }

        if (isFling && mScroller.isFinished()) {
            isFling = false;
            if (hasLyric() && !isTouch) {
                adjustCenter();
                postDelayed(hideTimelineRunnable, TIMELINE_KEEP_TIME);
            }
        }
    }

    private void endAnimation() {
        if (mAnimator != null && mAnimator.isRunning())
            mAnimator.end();
    }

    private void runOnUi(Runnable r) {
        if (Looper.myLooper() == Looper.getMainLooper())
            r.run();
        else post(r);
    }

    @Override
    protected void onDetachedFromWindow() {
        removeCallbacks(hideTimelineRunnable);
        super.onDetachedFromWindow();
    }

    public interface onLyricListener {
        boolean onSeekTo(int time);

        void onDownloadLyric();
    }
}