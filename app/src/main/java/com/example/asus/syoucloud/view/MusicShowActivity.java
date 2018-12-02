package com.example.asus.syoucloud.view;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.example.asus.syoucloud.Contract;
import com.example.asus.syoucloud.MusicService;
import com.example.asus.syoucloud.R;
import com.example.asus.syoucloud.base.BaseActivity;
import com.example.asus.syoucloud.bean.MusicInfo;
import com.example.asus.syoucloud.data.DatabaseManager;
import com.example.asus.syoucloud.presenter.MusicShowPresenter;
import com.example.asus.syoucloud.util.ActivityUtils;
import com.example.asus.syoucloud.util.Constant;
import com.example.asus.syoucloud.util.RecyclerDivider;

import java.util.ArrayList;
import java.util.List;

public class MusicShowActivity extends BaseActivity<Contract.IMusicShowActivity, MusicShowPresenter>
        implements Contract.IMusicShowActivity {

    private MusicListAdapter adapter;
    private Toolbar toolbar;
    private int albumId;

    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            BottomLayoutFragment fragment = new BottomLayoutFragment();
            fragment.setType(Constant.BOTTOM_SHOW);
            ActivityUtils.addFragmentToActivity(getSupportFragmentManager(),
                    fragment, R.id.music_show_bottom);
            mPresenter.initData((MusicService.MusicPlayer) service, albumId);
            initView();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_show);

        initToolbar();
        Intent intent = getIntent();
        albumId = intent.getIntExtra("albumId", -1);

        Intent bindIntent = new Intent(this, MusicService.class);
        bindService(bindIntent, connection, BIND_AUTO_CREATE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(connection);
    }

    @Override
    protected MusicShowPresenter createPresenter() {
        return new MusicShowPresenter();
    }

    private void initToolbar() {
        toolbar = findViewById(R.id.music_show_toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null)
            actionBar.setDisplayHomeAsUpEnabled(true);
    }

    private void initView() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        RecyclerView recyclerView = findViewById(R.id.music_show_recycler);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addItemDecoration(new RecyclerDivider(getApplicationContext(),
                LinearLayoutManager.VERTICAL, 1, Constant.ITEM_DECORATION));
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            private Drawable mDrawable = ContextCompat.getDrawable(getApplicationContext(),
                    R.drawable.music_show_toolbar_background);

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                int offset = recyclerView.computeVerticalScrollOffset();
                int toolBarHeight = toolbar.getMeasuredHeight();
                if (layoutManager.findFirstVisibleItemPosition() == -1) return;

                if (offset >= toolBarHeight * 2.5 || layoutManager.findFirstVisibleItemPosition() > 0) {
                    mDrawable.setAlpha(255);
                    toolbar.setBackground(mDrawable);
                } else if (offset >= toolBarHeight) {
                    mDrawable.setAlpha((int) (255 * (offset - toolBarHeight) / (toolBarHeight * 1.5F)));
                    toolbar.setBackground(mDrawable);
                } else toolbar.setBackground(null);
            }
        });

        new Thread(() -> {
            List<MusicInfo> musicList;
            if (albumId == -1) musicList = DatabaseManager.getInstance().getMusicList();
            else {
                musicList = DatabaseManager.getInstance().getMusicList(albumId);
                if (musicList == null) musicList = new ArrayList<>();
            }
            adapter = new MusicListAdapter(musicList);
            adapter.setOnMusicClickListener(mPresenter);
            recyclerView.setAdapter(adapter);
        }).start();
    }

    @Override
    public void add(MusicInfo music) {
        adapter.add(music);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.music_show_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            case R.id.music_show_delete:
                break;
            case R.id.music_show_search:
                break;
            default:
        }
        return true;
    }
}
