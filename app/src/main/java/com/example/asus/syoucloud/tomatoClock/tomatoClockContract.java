package com.example.asus.syoucloud.tomatoClock;

public interface tomatoClockContract {
    interface ITomatoClockPresenter {
        void start(int maxProgress);

        void reStart();

        void imageClick();
    }

    interface ITomatoClockActivity {
        void update(int progress);

        void changeType(boolean isPlay);

        void end();

        void sendBro();

        void cancelBro();
    }
}
