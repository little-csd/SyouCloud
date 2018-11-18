package com.example.asus.syoucloud.musicManager;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.RemoteViews;

import com.example.asus.syoucloud.Constant;
import com.example.asus.syoucloud.MusicPlayActivity;
import com.example.asus.syoucloud.R;

import java.io.IOException;
import java.util.List;

import static com.example.asus.syoucloud.Constant.LIST_LOOP;
import static com.example.asus.syoucloud.Constant.SHUFFLE;
import static com.example.asus.syoucloud.Constant.SINGLE_LOOP;

public class MusicService extends Service {
    private NotificationManager notificationManager;
    private MusicPlayer musicPlayer;
    private NotificationReceiver notificationReceiver;
    private RemoteViews remoteViews;
    private Notification notification;
    private boolean hasForeground = false;
    private boolean hasLyric = false;

    public MusicService() {
        super();
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

    @Override
    public void onCreate() {
        super.onCreate();
        musicPlayer = new MusicPlayer();
        MusicLoader musicLoader = MusicLoader.getInstance(getContentResolver());
        musicPlayer.setMusicList(musicLoader.getMusicList());
        musicPlayer.initMediaPlayer();
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

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (notificationReceiver != null) unregisterReceiver(notificationReceiver);
    }

    public class MusicPlayer extends Binder implements onLyricSeekToListener {
        private static final String TAG = "MusicPlayer";
        private boolean isPlay = false;
        private int playStyle = LIST_LOOP;
        private MediaPlayer mediaPlayer;
        private int id;
        private List<MusicInfo> musicList;
        private onMusicListener musicPlayListener;
        private onMusicListener bottomPlayListener;

        private MusicPlayer() {
            mediaPlayer = new MediaPlayer();
            id = 165;
            mediaPlayer.setOnCompletionListener(mp -> next());
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

        public void setMusicList(List<MusicInfo> musicList) {
            this.musicList = musicList;
        }

        public void setMusicPlayListener(onMusicListener listener) {
            musicPlayListener = listener;
        }

        public void deleteMusicPlayListener() {
            musicPlayListener = null;
        }

        public void setBottomPlayListener(onMusicListener listener) {
            bottomPlayListener = listener;
        }

        public void deleteBottomPlayListener() {
            bottomPlayListener = null;
        }

        private void initMediaPlayer() {
            try {
                mediaPlayer.setDataSource(musicList.get(id).getUrl());
                mediaPlayer.prepare();
            } catch (IOException e) {
                Log.i(TAG, "initMediaPlayer: error");
            }
        }

        public void playOrPause() {
            isPlay = !isPlay;
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.pause();
                remoteViews.setImageViewResource(R.id.notification_play, R.drawable.notification_play);
                notificationManager.notify(1, notification);
            } else {
                mediaPlayer.start();
                if (!hasForeground) {
                    hasForeground = true;
                    sendCustomNotification();
                } else {
                    remoteViews.setImageViewResource(R.id.notification_play, R.drawable.notification_pause);
                    notificationManager.notify(1, notification);
                }
            }
            if (musicPlayListener != null) musicPlayListener.onMusicPlayOrPause();
            if (bottomPlayListener != null) bottomPlayListener.onMusicPlayOrPause();
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
            if (musicPlayListener != null) musicPlayListener.onStopUpd();
            mediaPlayer.reset();
            if (playStyle != SINGLE_LOOP) nextId();
            else {
                id++;
                if (id >= musicList.size()) id = 0;
            }
            initMediaPlayer();
            mediaPlayer.start();
            isPlay = true;
            if (musicPlayListener != null) musicPlayListener.onMusicNext();
            if (bottomPlayListener != null) bottomPlayListener.onMusicNext();
            if (!hasForeground) {
                sendCustomNotification();
                hasForeground = true;
            } else updateNotification();
        }

        public void last() {
            mediaPlayer.reset();
            if (playStyle != SHUFFLE) {
                id--;
                if (id < 0) id = musicList.size() - 1;
            } else nextId();
            initMediaPlayer();
            mediaPlayer.start();
            isPlay = true;
            if (musicPlayListener != null) musicPlayListener.onMusicLast();
            if (bottomPlayListener != null) bottomPlayListener.onMusicLast();
            if (!hasForeground) {
                sendCustomNotification();
                hasForeground = true;
            } else updateNotification();
        }

        private void sendCustomNotification() {
            notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationChannel channel = new NotificationChannel("music", "test for notice",
                        NotificationManager.IMPORTANCE_HIGH);
                notificationManager.createNotificationChannel(channel);
            }

            MusicInfo music = musicPlayer.getMusic();
            remoteViews = new RemoteViews(getPackageName(), R.layout.custom_notification);
            remoteViews.setImageViewBitmap(R.id.notification_image, music.getBitmap());
            remoteViews.setTextViewText(R.id.notification_title, music.getTitle());
            remoteViews.setTextViewText(R.id.notification_artist, music.getArtist());

            Intent intentPlay = new Intent(Constant.PLAY);
            PendingIntent pendingPlay = PendingIntent.getBroadcast(MusicService.this,
                    0, intentPlay, 0);
            remoteViews.setOnClickPendingIntent(R.id.notification_play, pendingPlay);

            Intent intentNext = new Intent(Constant.NEXT);
            PendingIntent pendingNext = PendingIntent.getBroadcast(MusicService.this,
                    0, intentNext, 0);
            remoteViews.setOnClickPendingIntent(R.id.notification_next, pendingNext);

            Intent intentLast = new Intent(Constant.LAST);
            PendingIntent pendingLast = PendingIntent.getBroadcast(MusicService.this,
                    0, intentLast, 0);
            remoteViews.setOnClickPendingIntent(R.id.notification_last, pendingLast);

            Intent intentCancel = new Intent(Constant.CANCEL);
            PendingIntent pendingCancel = PendingIntent.getBroadcast(MusicService.this,
                    0, intentCancel, 0);
            remoteViews.setOnClickPendingIntent(R.id.notification_cancel, pendingCancel);

            Intent intentLyric = new Intent(Constant.LYRIC);
            PendingIntent pendingLyric = PendingIntent.getBroadcast(MusicService.this,
                    0, intentLyric, 0);
            remoteViews.setOnClickPendingIntent(R.id.notification_lyric, pendingLyric);

            Intent intentMusicPlay = new Intent(MusicService.this, MusicPlayActivity.class);
            PendingIntent pendingToActivity = PendingIntent.getActivity(MusicService.this,
                    0, intentMusicPlay, 0);
            remoteViews.setOnClickPendingIntent(R.id.notification_image, pendingToActivity);

            if (notificationReceiver == null) {
                IntentFilter filter = new IntentFilter();
                filter.addAction(Constant.PLAY);
                filter.addAction(Constant.NEXT);
                filter.addAction(Constant.LAST);
                filter.addAction(Constant.CANCEL);
                filter.addAction(Constant.LYRIC);
                notificationReceiver = new NotificationReceiver();
                registerReceiver(notificationReceiver, filter);
            }

            notification = new NotificationCompat
                    .Builder(MusicService.this, "music")
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContent(remoteViews)
                    .build();
            notification.flags |= Notification.FLAG_NO_CLEAR;
            notificationManager.notify(1, notification);
            hasForeground = true;
        }

        private void updateNotification() {
            MusicInfo music = getMusic();
            remoteViews.setImageViewBitmap(R.id.notification_image, music.getBitmap());
            remoteViews.setTextViewText(R.id.notification_title, music.getTitle());
            remoteViews.setTextViewText(R.id.notification_artist, music.getArtist());
            remoteViews.setImageViewResource(R.id.notification_play, R.drawable.notification_pause);
            notificationManager.notify(1, notification);
        }

        private void cancelNotification() {
            notificationManager.cancel(1);
            hasForeground = false;
            if (!isPlay) return;
            mediaPlayer.seekTo(0);
            mediaPlayer.pause();
            isPlay = false;
            if (bottomPlayListener != null) bottomPlayListener.onMusicStop();
            if (musicPlayListener != null) musicPlayListener.onMusicStop();
        }

        //todo add desktop lyric
        public void showLyric() {

        }

        @Override
        public boolean onSeekTo(int time) {
            mediaPlayer.seekTo(time);
            return true;
        }
    }

    private class NotificationReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String actionCode = intent.getAction();
            if (actionCode == null) return;
            switch (actionCode) {
                case Constant.PLAY:
                    musicPlayer.playOrPause();
                    break;
                case Constant.NEXT:
                    musicPlayer.next();
                    break;
                case Constant.LAST:
                    musicPlayer.last();
                    break;
                case Constant.CANCEL:
                    musicPlayer.cancelNotification();
                    break;
                case Constant.LYRIC:
                    if (!hasLyric) remoteViews.setTextColor(R.id.notification_lyric, Color.RED);
                    else remoteViews.setTextColor(R.id.notification_lyric, Color.BLACK);
                    hasLyric = !hasLyric;
                    notificationManager.notify(1, notification);
                    musicPlayer.showLyric();
                    break;
                default:
            }
        }
    }
}