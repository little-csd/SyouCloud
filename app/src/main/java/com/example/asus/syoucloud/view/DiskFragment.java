package com.example.asus.syoucloud.view;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;

import com.example.asus.syoucloud.Contract;
import com.example.asus.syoucloud.MusicService;
import com.example.asus.syoucloud.R;
import com.example.asus.syoucloud.base.BaseFragment;
import com.example.asus.syoucloud.presenter.DiskFragmentPresenter;

import static android.content.Context.BIND_AUTO_CREATE;

public class DiskFragment extends BaseFragment<Contract.IDiskLayoutFragment, DiskFragmentPresenter>
        implements Contract.IDiskLayoutFragment {

    private static final String TAG = "DiskFragment";

    private ObjectAnimator albumAnim;
    private ImageView albumImage;

    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            if (getActivity() == null) {
                Log.i(TAG, "onServiceConnected: getActivity error");
                return;
            }
            Context context = getActivity().getApplicationContext();
            DiskFragmentPresenter presenter =
                    new DiskFragmentPresenter(context, (MusicService.MusicPlayer) service);
            presenter.attachView(DiskFragment.this);
            setPresenter(presenter);
            mPresenter.start();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
        }
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.disk_fragment, container, false);
        albumImage = view.findViewById(R.id.album_image);
        initAnim();
        bindService();
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        albumAnim.cancel();
        if (getActivity() != null)
            getActivity().getApplicationContext().unbindService(connection);
    }

    public void bindService() {
        if (getActivity() != null) {
            Context context = getActivity().getApplicationContext();
            Intent bindIntent = new Intent(context, MusicService.class);
            context.bindService(bindIntent, connection, BIND_AUTO_CREATE);
        }
    }

    public void initAnim() {
        albumAnim = ObjectAnimator.ofFloat(albumImage,
                "rotation", 0f, 360f);
        albumAnim.setDuration(25 * 1000);
        albumAnim.setInterpolator(new LinearInterpolator());
        albumAnim.setRepeatCount(ValueAnimator.INFINITE);
        albumAnim.setRepeatMode(ValueAnimator.RESTART);
    }

    @Override
    public ImageView getIgvView() {
        return albumImage;
    }

    @Override
    public void startAnim() {
        if (albumAnim.isStarted()) albumAnim.resume();
        else albumAnim.start();
    }

    @Override
    public void pauseAnim() {
        albumAnim.pause();
    }
}
