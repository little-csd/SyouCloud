package com.example.asus.syoucloud.tomatoClock;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.asus.syoucloud.R;
import com.example.asus.syoucloud.base.BaseActivity;
import com.example.asus.syoucloud.customView.CircleBar;
import com.example.asus.syoucloud.util.Constant;
import com.example.asus.syoucloud.util.TimeUtil;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class TomatoClockActivity
        extends BaseActivity<tomatoClockContract.ITomatoClockActivity, TomatoClockPresenter>
        implements tomatoClockContract.ITomatoClockActivity {

    @BindView(R.id.tomato_bar)
    CircleBar tomatoBar;
    @BindView(R.id.tomato_time)
    TextView tomatoTime;
    @BindView(R.id.tomato_play)
    ImageView tomatoPlay;

    private int maxProgress;
    private PendingIntent alarm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tomato_clock);
        ButterKnife.bind(this);

        initToolbar();
        initIntent();
        maxProgress = tomatoBar.getMaxProgress();
        mPresenter.start(maxProgress);
    }

    @Override
    protected TomatoClockPresenter createPresenter() {
        return new TomatoClockPresenter();
    }

    private void initToolbar() {
        Toolbar toolbar = findViewById(R.id.tomato_toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null)
            actionBar.setDisplayHomeAsUpEnabled(true);
    }

    private void initIntent() {
        Intent intent = new Intent(getApplicationContext(), TomatoClockActivity.class);
        intent.setPackage(getPackageName());
        alarm = PendingIntent.getActivity(this, 0, intent, 0);
    }

    @Override
    public void update(int progress) {
        tomatoBar.setProgress(progress);
        tomatoTime.setText(TimeUtil.parseToString(maxProgress - progress));
        tomatoBar.invalidate();
    }

    @Override
    public void changeType(boolean isPlay) {
        if (isPlay) tomatoPlay.setImageResource(R.drawable.tomato_stop_selector);
        else tomatoPlay.setImageResource(R.drawable.play_button_selector);
    }

    @Override
    public void end() {
        Toast.makeText(this, "It is time to relax", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(Constant.TIME_OUT);
        sendBroadcast(intent);
        mPresenter.reStart();
    }

    @Override
    public void sendBro() {
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        if (alarmManager == null) return;
        long beginTime = System.currentTimeMillis() + maxProgress * 1000;
        alarmManager.set(AlarmManager.RTC_WAKEUP, beginTime, alarm);
    }

    @Override
    public void cancelBro() {
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        if (alarmManager == null) return;
        if (alarm != null) alarmManager.cancel(alarm);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home)
            finish();
        return false;
    }

    @OnClick(R.id.tomato_play)
    public void onViewClicked() {
        mPresenter.imageClick();
    }
}