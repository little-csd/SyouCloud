package com.example.asus.syoucloud.view;

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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.asus.syoucloud.Contract;
import com.example.asus.syoucloud.MusicService;
import com.example.asus.syoucloud.R;
import com.example.asus.syoucloud.base.BaseFragment;
import com.example.asus.syoucloud.presenter.BottomLayoutPresenter;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

import static android.content.Context.BIND_AUTO_CREATE;

public class BottomLayoutFragment extends BaseFragment<Contract.IBottomLayoutFragment,
        BottomLayoutPresenter> implements Contract.IBottomLayoutFragment {

    private static final String TAG = "BottomLayoutFragment";
    private int type;

    @BindView(R.id.bottom_bitmap)
    ImageView bottomBitmap;
    @BindView(R.id.bottom_title)
    TextView bottomTitle;
    @BindView(R.id.bottom_artist)
    TextView bottomArtist;
    @BindView(R.id.bottom_text)
    LinearLayout bottomText;
    @BindView(R.id.bottom_play)
    ImageView bottomPlay;
    Unbinder unbinder;

    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            if (getActivity() == null) {
                Log.i(TAG, "onServiceConnected: getActivity error");
                return;
            }
            Context context = getActivity().getApplicationContext();
            BottomLayoutPresenter presenter =
                    new BottomLayoutPresenter(context, (MusicService.MusicPlayer) service, type);
            presenter.attachView(BottomLayoutFragment.this);
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
        View view = inflater.inflate(R.layout.bottom_layout_fragment, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        bindService();
    }

    public void setType(int type) {
        this.type = type;
    }

    public void bindService() {
        if (getActivity() != null) {
            Context context = getActivity().getApplicationContext();
            Intent bindIntent = new Intent(context, MusicService.class);
            context.bindService(bindIntent, connection, BIND_AUTO_CREATE);
        }
    }

    @Override
    public void setTitle(String Msg) {
        bottomTitle.setText(Msg);
    }

    @Override
    public void setArtist(String Msg) {
        bottomArtist.setText(Msg);
    }

    @Override
    public ImageView getIgvView() {
        return bottomBitmap;
    }

    @Override
    public void play() {
        bottomPlay.setImageResource(R.drawable.notification_play);
    }

    @Override
    public void pause() {
        bottomPlay.setImageResource(R.drawable.notification_pause);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
        if (getActivity() != null)
        getActivity().getApplicationContext().unbindService(connection);
    }

    @OnClick({R.id.bottom_text, R.id.bottom_play})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.bottom_text:
                mPresenter.click();
                break;
            case R.id.bottom_play:
                mPresenter.play();
                break;
        }
    }
}