package com.example.asus.syoucloud.overlayWindow;

import android.content.Context;

public interface overlayWindowContract {

    interface IOverlayWindowManager {
        void showLyric(boolean isPlay);

        void removeLyric();

        void updateText(String Msg);

        void updateImage(boolean isPlay);

        void lock();

        void unLock();

        void update();

        void setPresenter(IOverlayWindowPresenter presenter);

        void initData(Context context);
    }

    interface IOverlayWindowPresenter {
        void play();

        void next();

        void last();

        void close();

        void lock();
    }
}
