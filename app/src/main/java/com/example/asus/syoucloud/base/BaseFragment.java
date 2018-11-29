package com.example.asus.syoucloud.base;

import android.support.v4.app.Fragment;

public abstract class BaseFragment<V, T extends BasePresenter<V>> extends Fragment {

    protected T mPresenter;

    public void setPresenter(T presenter) {
        mPresenter = presenter;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mPresenter.isViewAttached())
            mPresenter.detachView();
    }
}