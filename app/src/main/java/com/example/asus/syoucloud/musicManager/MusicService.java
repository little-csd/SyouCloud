package com.example.asus.syoucloud.musicManager;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.RemoteViews;

import com.example.asus.syoucloud.Constant;
import com.example.asus.syoucloud.R;

import java.io.IOException;
import java.util.List;

import static com.example.asus.syoucloud.Constant.LIST_LOOP;
import static com.example.asus.syoucloud.Constant.SHUFFLE;

public class MusicService extends Service {
    private NotificationManager notificationManager;
    private MusicPlayer musicPlayer;
    private NotificationReceiver notificationReceiver;

    public MusicService() {
        musicPlayer = new MusicPlayer();
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return musicPlayer;
    }

    private void sendCustomNotification() {
        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("music", "test for notice",
                    NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(channel);
        }

        MusicInfo music = musicPlayer.getMusic();
        RemoteViews remoteViews = new RemoteViews(getPackageName(), R.layout.custom_notification);
        remoteViews.setImageViewBitmap(R.id.notification_image, music.getBitmap());
        remoteViews.setTextViewText(R.id.notification_title, music.getTitle());
        remoteViews.setTextViewText(R.id.notification_artist, music.getArtist());

        Intent intentPlay = new Intent(Constant.PLAY);
        PendingIntent pendingPlay = PendingIntent.getBroadcast(this,
                0, intentPlay, 0);
        remoteViews.setOnClickPendingIntent(R.id.notification_play, pendingPlay);

        Intent intentNext = new Intent(Constant.NEXT);
        PendingIntent pendingNext = PendingIntent.getBroadcast(this,
                0, intentNext, 0);
        remoteViews.setOnClickPendingIntent(R.id.notification_next, pendingNext);

        Intent intentLast = new Intent(Constant.LAST);
        PendingIntent pendingLast = PendingIntent.getBroadcast(this,
                0, intentLast, 0);
        remoteViews.setOnClickPendingIntent(R.id.notification_last, pendingLast);

        Intent intentCancel = new Intent(Constant.CANCEL);
        PendingIntent pendingCancel = PendingIntent.getBroadcast(this,
                0, intentCancel, 0);
        remoteViews.setOnClickPendingIntent(R.id.notification_cancel, pendingCancel);

        Intent intentLyric = new Intent(Constant.LYRIC);
        PendingIntent pendingLyric = PendingIntent.getBroadcast(this,
                0, intentLyric, 0);
        remoteViews.setOnClickPendingIntent(R.id.notification_lyric, pendingLyric);

        IntentFilter filter = new IntentFilter();
        filter.addAction(Constant.PLAY);
        filter.addAction(Constant.NEXT);
        filter.addAction(Constant.LAST);
        filter.addAction(Constant.CANCEL);
        filter.addAction(Constant.LYRIC);
        notificationReceiver = new NotificationReceiver();
        registerReceiver(notificationReceiver, filter);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this, "music");
        mBuilder.setSmallIcon(R.mipmap.ic_launcher)
                .setContent(remoteViews);

        Notification notification = mBuilder.build();
        notification.flags |= Notification.FLAG_NO_CLEAR;
        notificationManager.notify(1, notification);
    }

    public static String parseToString(int time) {
        StringBuilder mTime = new StringBuilder();
        int minute = time / 60, second = time % 60;
        if (minute >= 10) mTime.append(minute).append(":");
        else mTime.append("0").append(minute).append(":");
        if (second >= 10) mTime.append(second);
        else mTime.append("0").append(second);
        return mTime.toString();
    }

    public void cancelNotification() {

    }

    public void showLyric() {

    }

    public class MusicPlayer extends Binder {
        private static final String TAG = "MusicPlayer";
        private boolean isPlay = false;
        private int playStyle = LIST_LOOP;
        private MediaPlayer mediaPlayer;
        private int id;
        private List<MusicInfo> musicList;
        private onMusicListener musicPlayListener;

        private MusicPlayer() {
            mediaPlayer = new MediaPlayer();
            id = 0;
            mediaPlayer.setOnCompletionListener(mp -> {
                mp.reset();
                nextId();
                initMediaPlayer();
                mp.start();
                if (musicPlayListener != null)
                    musicPlayListener.onMusicCompletion();
            });
        }

        public int getDuration() {
            return mediaPlayer.getDuration();
        }

        public int getCurrentProgress() {
            return mediaPlayer.getCurrentPosition();
        }

        public boolean isPlay() {
            return isPlay;
        }

        public int getPlayStyle() {
            return playStyle;
        }

        public void setPlayStyle(int style) {
            playStyle = style;
        }

        public MusicInfo getMusic() {
            return musicList.get(id);
        }

        public List<MusicInfo> getMusicList() {
            return musicList;
        }

        public void setMusicList(List<MusicInfo> musicList) {
            this.musicList = musicList;
        }

        public void initMediaPlayer() {
            try {
                mediaPlayer.setDataSource(musicList.get(id).getUrl());
                mediaPlayer.prepare();
            } catch (IOException e) {
                Log.i(TAG, "initMediaPlayer: error");
            }
        }

        public void setMusicPlayListener(onMusicListener listener) {
            musicPlayListener = listener;
        }

        public void deleteMusicPlayListener() {
            musicPlayListener = null;
        }

        public void playOrPause() {
            isPlay = !isPlay;
            if (mediaPlayer.isPlaying()) mediaPlayer.pause();
            else mediaPlayer.start();
            if (musicPlayListener != null) musicPlayListener.onMusicPlayOrPause();
        }

        public void seekTo(int progress) {
            mediaPlayer.seekTo(progress * 1000);
        }

        private void nextId() {
            switch (playStyle) {
                case LIST_LOOP:
                    id++;
                    if (id >= musicList.size()) id = 0;
                    return;
                case SHUFFLE:
                    id = (int) (Math.random() * musicList.size() + 0.5) - 1;
                    return;
                default:
            }
        }

        public void next() {
            mediaPlayer.reset();
            nextId();
            initMediaPlayer();
            mediaPlayer.start();
            isPlay = true;
            if (musicPlayListener != null) musicPlayListener.onMusicNext();
        }

        public void last() {
            mediaPlayer.reset();
            if (playStyle != SHUFFLE) {
                id--;
                if (id < 0) id = musicList.size() - 1;
            }
            initMediaPlayer();
            mediaPlayer.start();
            isPlay = true;
            if (musicPlayListener != null) musicPlayListener.onMusicLast();
        }
    }
}