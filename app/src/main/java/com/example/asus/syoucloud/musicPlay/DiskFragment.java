package com.example.asus.syoucloud.musicPlay;

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
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;

import com.example.asus.syoucloud.MusicService;
import com.example.asus.syoucloud.R;
import com.example.asus.syoucloud.base.BaseFragment;
import com.example.asus.syoucloud.data.DataRepository;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

import static android.content.Context.BIND_AUTO_CREATE;

public class DiskFragment extends BaseFragment<musicPlayContract.IDiskLayoutFragment, DiskFragmentPresenter>
        implements musicPlayContract.IDiskLayoutFragment {

    private static final String TAG = "DiskFragment";
    @BindView(R.id.album_image)
    ImageView albumImage;
    @BindView(R.id.disk_download_lyric)
    ImageView diskDownloadLyric;
    @BindView(R.id.disk_add_album)
    ImageView diskAddAlbum;
    Unbinder unbinder;

    private ObjectAnimator albumAnim;

    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            if (getActivity() == null) {
                Log.i(TAG, "onServiceConnected: getActivity error");
                return;
            }
            Context context = getActivity();
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
        unbinder = ButterKnife.bind(this, view);
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
        unbinder.unbind();
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

    public void mkAddToAlbumDialog() {
        Context context = getContext();
        if (context == null) {
            Log.i(TAG, "mkAddToAlbumDialog: get context fail");
            return;
        }
        mPresenter.noticeAdd();
        String[] items = DataRepository.getInstance().getMixTitleItems();
        new AlertDialog.Builder(context)
                .setTitle("Save to mix")
                .setItems(items, (dialog, which) -> mPresenter.addToDatabase(which))
                .show();
    }

    @OnClick({R.id.disk_download_lyric, R.id.disk_add_album})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.disk_download_lyric:

                break;
            case R.id.disk_add_album:
                mkAddToAlbumDialog();
                break;
        }
    }
}
