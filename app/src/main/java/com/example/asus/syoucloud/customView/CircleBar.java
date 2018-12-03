package com.example.asus.syoucloud.customView;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

import com.example.asus.syoucloud.R;

public class CircleBar extends View {

    private int firstColor;
    private int secondColor;
    private int radius;
    private int circleWidth;
    private int maxProgress;
    private int progress = 0;
    private Paint mPaint;
    private RectF cir = new RectF();

    public CircleBar(Context context) {
        super(context, null);
    }

    public CircleBar(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs, 0);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.CircleBar);
        for (int i = 0; i < a.getIndexCount(); i++) {
            switch (a.getIndex(i)) {
                case R.styleable.CircleBar_firstColor:
                    firstColor = a.getColor(a.getIndex(i), Color.GRAY);
                    break;
                case R.styleable.CircleBar_secondColor:
                    secondColor = a.getColor(a.getIndex(i), Color.BLUE);
                    break;
                case R.styleable.CircleBar_circleWidth:
                    circleWidth = a.getDimensionPixelOffset(a.getIndex(i), (int) TypedValue.applyDimension(
                            TypedValue.COMPLEX_UNIT_PX, 20, getResources().getDisplayMetrics()));
                    break;
                case R.styleable.CircleBar_maxProgress:
                    maxProgress = a.getInt(a.getIndex(i), 1500);
                    break;
            }
        }
        a.recycle();

        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setStrokeWidth(circleWidth);
        mPaint.setStyle(Paint.Style.STROKE);
    }

    public CircleBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int left, top;
        int height = getMeasuredHeight(), width = getMeasuredWidth();
        if (height < width) {
            left = (width - height) / 2 + circleWidth / 2;
            top = circleWidth / 2;
            radius = (height - circleWidth) / 2;
        } else {
            top = (height - width + circleWidth) / 2;
            left = circleWidth / 2;
            radius = (width - circleWidth) / 2;
        }
        cir.set(left, top, width - left, height - top);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        mPaint.setColor(firstColor);
        canvas.drawCircle(getWidth() / 2, getHeight() / 2, radius, mPaint);

        float angle = 1F * progress / maxProgress * 360;
        mPaint.setColor(secondColor);
        canvas.drawArc(cir, -90F, 360F - angle, false, mPaint);
    }

    public int getMaxProgress() {
        return maxProgress;
    }

    public void setMaxProgress(int maxProgress) {
        this.maxProgress = maxProgress;
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }
}
