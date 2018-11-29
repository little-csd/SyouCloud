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

import com.example.asus.syoucloud.Contract;
import com.example.asus.syoucloud.LyricView;
import com.example.asus.syoucloud.MusicService;
import com.example.asus.syoucloud.R;
import com.example.asus.syoucloud.base.BaseFragment;
import com.example.asus.syoucloud.bean.Lyric;
import com.example.asus.syoucloud.presenter.LyricFragmentPresenter;

import java.util.List;

import static android.content.Context.BIND_AUTO_CREATE;

public class LyricFragment extends BaseFragment<Contract.ILyricFragment, LyricFragmentPresenter>
        implements Contract.ILyricFragment {

    private static final String TAG = "LyricFragment";

    private LyricView lyricView;

    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            if (getActivity() == null) {
                Log.i(TAG, "onServiceConnected: getActivity error");
                return;
            }
            LyricFragmentPresenter presenter =
                    new LyricFragmentPresenter((MusicService.MusicPlayer) service);
            presenter.attachView(LyricFragment.this);
            setPresenter(presenter);
            lyricView.setSeekToListener(presenter);
            mPresenter.start();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
        }
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.lyric_fragment, container, false);
        lyricView = view.findViewById(R.id.lyric_view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        bindService();
    }

    public void bindService() {
        if (getActivity() != null) {
            Context context = getActivity().getApplicationContext();
            Intent bindIntent = new Intent(context, MusicService.class);
            context.bindService(bindIntent, connection, BIND_AUTO_CREATE);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (getActivity() != null)
            getActivity().getApplicationContext().unbindService(connection);
        Log.i(TAG, "onDestroyView: LyricFragment");
    }

    @Override
    public void seekTo(int time) {
        lyricView.updateTime(time);
    }

    @Override
    public void setLyricList(List<Lyric> list) {
        lyricView.setLyricList(list);
    }
}
