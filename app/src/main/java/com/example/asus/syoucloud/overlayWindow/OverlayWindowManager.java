package com.example.asus.syoucloud.overlayWindow;

import android.content.Context;
import android.graphics.PixelFormat;
import android.os.Build;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.asus.syoucloud.R;

public class OverlayWindowManager implements View.OnTouchListener, View.OnClickListener,
        overlayWindowContract.IOverlayWindowManager {

    private WindowManager.LayoutParams params;
    private WindowManager windowManager;
    private overlayWindowContract.IOverlayWindowPresenter mPresenter;

    private View overlayNormal;
    private View overlayClick;
    private TextView normalLyric;
    private TextView clickLyric;
    private ImageView playImage;

    private float x;
    private float y;
    private float touchStartX;
    private float touchStartY;

    private int statusBarHeight;
    private boolean isClick = false;

    @Override
    public void updateText(String Msg) {
        if (isClick) clickLyric.setText(Msg);
        else normalLyric.setText(Msg);
    }

    @Override
    public void updateImage(boolean isPlay) {
        if (isPlay) playImage.setImageResource(R.drawable.pause_button);
        else playImage.setImageResource(R.drawable.play_button);
    }

    @Override
    public void setPresenter(overlayWindowContract.IOverlayWindowPresenter presenter) {
        mPresenter = presenter;
    }

    @Override
    public void initData(Context context) {
        params = new WindowManager.LayoutParams();
        windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            params.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        else params.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
        params.format = PixelFormat.TRANSPARENT;
        params.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        params.gravity = Gravity.TOP | Gravity.START;
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;

        LayoutInflater inflater = LayoutInflater.from(context);
        overlayNormal = inflater.inflate(R.layout.normal_overlay_window, null);
        overlayClick = inflater.inflate(R.layout.click_overlay_window, null);
        normalLyric = overlayNormal.findViewById(R.id.normal_overlay_lyric);
        clickLyric = overlayClick.findViewById(R.id.click_overlay_lyric);

        int resourceId = context.getResources().getIdentifier("status_bar_height",
                "dimen", "android");
        if (resourceId > 0)
            statusBarHeight = context.getResources().getDimensionPixelSize(resourceId);

        initView();
    }

    private void initView() {
        overlayNormal.setOnTouchListener(this);
        overlayNormal.setOnClickListener(this);
        overlayClick.setOnClickListener(this);
        overlayClick.setOnTouchListener(this);

        playImage = overlayClick.findViewById(R.id.overlay_play);
        ImageView lastImage = overlayClick.findViewById(R.id.overlay_last);
        ImageView nextImage = overlayClick.findViewById(R.id.overlay_next);
        ImageView closeImage = overlayClick.findViewById(R.id.overlay_close);
        ImageView lockImage = overlayClick.findViewById(R.id.overlay_lock);

        closeImage.setOnClickListener(v -> mPresenter.close());
        nextImage.setOnClickListener(v -> mPresenter.next());
        lastImage.setOnClickListener(v -> mPresenter.last());
        playImage.setOnClickListener(v -> mPresenter.play());
        lockImage.setOnClickListener(v -> mPresenter.lock());
    }

    @Override
    public void showLyric(boolean isPlay) {
        windowManager.addView(overlayNormal, params);
        if (isPlay) playImage.setImageResource(R.drawable.pause_button);
        else playImage.setImageResource(R.drawable.play_button);
    }

    @Override
    public void removeLyric() {
        if (isClick) windowManager.removeView(overlayClick);
        else windowManager.removeView(overlayNormal);
        isClick = false;
    }

    @Override
    public void lock() {
        params.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE;
        onClick(clickLyric);
    }

    @Override
    public void unLock() {
        params.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
    }

    @Override
    public void update() {
        windowManager.updateViewLayout(overlayNormal, params);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        x = event.getRawX();
        y = event.getRawY() - statusBarHeight;
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                touchStartX = event.getX();
                touchStartY = event.getY();
                break;
            case MotionEvent.ACTION_UP:
                updateViewPosition();
                touchStartX = touchStartY = 0;
                break;
            case MotionEvent.ACTION_MOVE:
                updateViewPosition();
                break;
            default:
        }
        return false;
    }

    @Override
    public void onClick(View v) {
        String text;
        if (isClick) {
            text = clickLyric.getText().toString();
            windowManager.addView(overlayNormal, params);
            windowManager.removeView(overlayClick);
        } else {
            text = normalLyric.getText().toString();
            windowManager.addView(overlayClick, params);
            windowManager.removeView(overlayNormal);
        }
        isClick = !isClick;
        updateText(text);
    }

    private void updateViewPosition() {
        params.x = (int) (x - touchStartX);
        params.y = (int) (y - touchStartY);
        if (isClick) windowManager.updateViewLayout(overlayClick, params);
        else windowManager.updateViewLayout(overlayNormal, params);
    }
}