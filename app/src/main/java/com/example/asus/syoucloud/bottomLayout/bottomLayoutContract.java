package com.example.asus.syoucloud.bottomLayout;

import android.widget.ImageView;

public interface bottomLayoutContract {

    interface IBottomLayoutFragment {
        void setTitle(String Msg);

        void setArtist(String Msg);

        ImageView getIgvView();

        void play();

        void pause();
    }

    interface IBottomLayoutPresenter {
        void start();

        void play();

        void click();
    }
}
