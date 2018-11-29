package com.example.asus.syoucloud;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.RemoteViews;

import com.example.asus.syoucloud.bean.MusicInfo;
import com.example.asus.syoucloud.presenter.OverlayWindowPresenter;
import com.example.asus.syoucloud.util.Constant;
import com.example.asus.syoucloud.util.MusicLoader;
import com.example.asus.syoucloud.view.MusicPlayActivity;

import java.io.IOException;
import java.util.List;

import static com.example.asus.syoucloud.util.Constant.LIST_LOOP;
import static com.example.asus.syoucloud.util.Constant.SHUFFLE;

public class MusicService extends Service {

    private NotificationManager notificationManager;
    private MusicPlayer musicPlayer;
    private NotificationReceiver notificationReceiver;
    private RemoteViews remoteViews;
    private Notification notification;
    private OverlayWindowPresenter overlayWindowPresenter;

    private boolean hasForeground = false;
    private boolean isLyricClick = false;

    public MusicService() {
        super();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        musicPlayer = new MusicPlayer();

        MusicLoader musicLoader = MusicLoader.getInstance(getContentResolver(), this);
        musicPlayer.setMusicList(musicLoader.getMusicList());
        musicPlayer.initMediaPlayer();
        overlayWindowPresenter = new OverlayWindowPresenter(musicPlayer, getApplicationContext());
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
        private int albumId;
        private List<MusicInfo> musicList;
        private onMusicListener[] listeners = new onMusicListener[6];

        private MusicPlayer() {
            mediaPlayer = new MediaPlayer();
            id = 61;
            albumId = -1;
            mediaPlayer.setOnCompletionListener(mp -> next());
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

        void setMusicList(List<MusicInfo> musicList) {
            this.musicList = musicList;
        }

        public void addListener(onMusicListener listener, int pos) {
            listeners[pos] = listener;
        }

        public void deleteListener(int pos) {
            listeners[pos] = null;
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
            for (int i = 0; i < Constant.MAX_TYPE; i++)
                if (listeners[i] != null) listeners[i].onMusicPlayOrPause();
        }

        public void seekTo(int progress) {
            mediaPlayer.seekTo(progress);
        }

        public void next() {
            if (playStyle == LIST_LOOP) {
                id++;
                if (id >= musicList.size()) id = 0;
            } else if (playStyle == SHUFFLE)
                id = (int) (Math.random() * musicList.size() + 0.5) - 1;
            playMusic();
        }

        public void last() {
            if (playStyle == LIST_LOOP) {
                id--;
                if (id < 0) id = musicList.size() - 1;
            } else if (playStyle == SHUFFLE)
                id = (int) (Math.random() * musicList.size() + 0.5) - 1;
            playMusic();
        }

        private void playMusic() {
            for (int i = 0; i < Constant.MAX_TYPE; i++)
                if (listeners[i] != null) listeners[i].onStopUpd();

            mediaPlayer.reset();
            initMediaPlayer();
            mediaPlayer.start();
            isPlay = true;
            if (hasForeground) updateNotification();
            else sendCustomNotification();

            for (int i = 0; i < Constant.MAX_TYPE; i++)
                if (listeners[i] != null) listeners[i].onMusicCompletion();
        }

        private void sendCustomNotification() {
            notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationChannel channel = new NotificationChannel("music",
                        "create music notification", NotificationManager.IMPORTANCE_HIGH);
                notificationManager.createNotificationChannel(channel);
            }

            MusicInfo music = musicPlayer.getMusic();
            remoteViews = new RemoteViews(getPackageName(), R.layout.custom_notification);
            remoteViews.setImageViewBitmap(R.id.notification_image, MusicLoader.getBitmap(
                    getApplicationContext(), music.getAlbumId(), 160, 110));
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
                filter.addAction(Constant.UNLOCK);
                filter.addAction(Constant.BACKGROUND);
                filter.addAction(Constant.FOREGROUND);
                filter.addAction(Constant.HEADSET);
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
            remoteViews.setImageViewBitmap(R.id.notification_image, MusicLoader.getBitmap(
                    getApplicationContext(), music.getAlbumId(), 160, 110));
            remoteViews.setTextViewText(R.id.notification_title, music.getTitle());
            remoteViews.setTextViewText(R.id.notification_artist, music.getArtist());
            remoteViews.setImageViewResource(R.id.notification_play, R.drawable.notification_pause);
            notificationManager.notify(1, notification);
        }

        private void cancelNotification() {
            if (isLyricClick) {
                isLyricClick = false;
                remoteViews.setTextColor(R.id.notification_lyric, Color.BLACK);
                if (MusicApplication.getActiveActivity() == 0)
                    overlayWindowPresenter.removeLyric();
                overlayWindowPresenter.cancel();
            }

            notificationManager.cancelAll();
            hasForeground = false;
            if (!isPlay) return;
            mediaPlayer.seekTo(0);
            mediaPlayer.pause();
            isPlay = false;

            for (int i = 0; i < Constant.MAX_TYPE; i++)
                if (listeners[i] != null) listeners[i].onMusicStop();
        }

        public void sendUnlockNotification() {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationChannel channel = new NotificationChannel("unlock",
                        "unlock lyric", NotificationManager.IMPORTANCE_HIGH);
                notificationManager.createNotificationChannel(channel);
            }

            Intent intent = new Intent(Constant.UNLOCK);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(MusicService.this,
                    0, intent, 0);
            Notification unlockNotification = new NotificationCompat
                    .Builder(MusicService.this, "unlock")
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.notification_large))
                    .setContentTitle("Syoucloud")
                    .setContentText("Click to unlock the lyric")
                    .setContentIntent(pendingIntent)
                    .setAutoCancel(true)
                    .build();
            unlockNotification.flags |= Notification.FLAG_NO_CLEAR;
            notificationManager.notify(2, unlockNotification);
        }

        public void lyricClick() {
            isLyricClick = !isLyricClick;
            if (isLyricClick) {
                remoteViews.setTextColor(R.id.notification_lyric, Color.RED);
                if (MusicApplication.getActiveActivity() == 0)
                    overlayWindowPresenter.showLyric();
            } else {
                remoteViews.setTextColor(R.id.notification_lyric, Color.BLACK);
                if (overlayWindowPresenter.isLock()) {
                    overlayWindowPresenter.unLock();
                    notificationManager.cancel(2);
                }
                if (MusicApplication.getActiveActivity() == 0)
                    overlayWindowPresenter.removeLyric();
            }
            notificationManager.notify(1, notification);
        }

        @Override
        public boolean onSeekTo(int time) {
            mediaPlayer.seekTo(time);
            if (!isPlay) playOrPause();
            return true;
        }

        public void changeAlbum(int mAlbumId, int position, List<MusicInfo> mList) {
            if (mAlbumId == albumId) {
                id = position;
                playMusic();
            } else {
                albumId = mAlbumId;
                musicList = mList;
                id = position;
                playMusic();
            }
        }
    }

    private class NotificationReceiver extends BroadcastReceiver {

        private boolean hasPlug = false;

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
                    musicPlayer.lyricClick();
                    break;
                case Constant.BACKGROUND:
                    if (isLyricClick) overlayWindowPresenter.showLyric();
                    break;
                case Constant.FOREGROUND:
                    if (isLyricClick) overlayWindowPresenter.removeLyric();
                    break;
                case Constant.UNLOCK:
                    overlayWindowPresenter.unLock();
                    break;
                case Constant.HEADSET:
                    if (!hasPlug) {
                        hasPlug = true;
                        return;
                    }
                    if (intent.getIntExtra("state", 0) == 0 && musicPlayer.isPlay)
                        musicPlayer.playOrPause();
                default:
            }
        }
    }
}