package com.example.asus.syoucloud.tomatoClock;

import android.os.Handler;

import com.example.asus.syoucloud.base.BasePresenter;
import com.example.asus.syoucloud.data.DataRepository;

public class TomatoClockPresenter extends BasePresenter<tomatoClockContract.ITomatoClockActivity>
        implements tomatoClockContract.ITomatoClockPresenter {

    private int maxProgress;
    private int progress;
    private long beginTime;
    private long currentTime;
    private boolean isPlay = false;

    private Handler handler = new Handler();

    private Runnable updProgress = new Runnable() {
        @Override
        public void run() {
            currentTime = System.currentTimeMillis();
            int mProgress = (int) ((currentTime - beginTime) / 1000);
            if (mProgress == progress) {
                handler.postDelayed(this, 200);
                return;
            } else progress = mProgress;
            if (progress < maxProgress) {
                mViewRef.get().update(progress);
                handler.postDelayed(this, 200);
            } else {
                isPlay = false;
                DataRepository.getInstance().saveBeginTime(0);
                mViewRef.get().update(maxProgress);
                mViewRef.get().changeType(isPlay);
                mViewRef.get().end();
            }
        }
    };

    @Override
    public void detachView() {
        super.detachView();
        if (isPlay)
        handler.removeCallbacks(updProgress);
    }

    @Override
    public void start(int maxProgress) {
        this.maxProgress = maxProgress;
        beginTime = DataRepository.getInstance().getBeginTime();
        if (beginTime == 0) {
            isPlay = false;
            progress = 0;
        } else if (beginTime + maxProgress * 1000 <= System.currentTimeMillis()) {
            beginTime = 0;
            progress = maxProgress;
            isPlay = false;
            DataRepository.getInstance().saveBeginTime(beginTime);
            mViewRef.get().changeType(isPlay);
            mViewRef.get().end();
            return;
        } else {
            isPlay = true;
            currentTime = System.currentTimeMillis();
            progress = (int) ((currentTime - beginTime) / 1000);
        }
        mViewRef.get().update(progress);
        mViewRef.get().changeType(isPlay);
        if (beginTime > 0) handler.post(updProgress);
    }

    @Override
    public void reStart() {
        progress = 0;
        mViewRef.get().update(progress);
    }

    @Override
    public void imageClick() {
        isPlay = !isPlay;
        mViewRef.get().changeType(isPlay);
        if (isPlay) {
            handler.post(updProgress);
            beginTime = System.currentTimeMillis();
            DataRepository.getInstance().saveBeginTime(beginTime);
            mViewRef.get().sendBro();
        } else {
            handler.removeCallbacks(updProgress);
            DataRepository.getInstance().saveBeginTime(0);
            reStart();
            mViewRef.get().cancelBro();
        }
    }
}