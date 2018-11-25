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
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RemoteViews;
import android.widget.TextView;

import com.example.asus.syoucloud.MusicApplication;
import com.example.asus.syoucloud.MusicPlayActivity;
import com.example.asus.syoucloud.R;
import com.example.asus.syoucloud.util.Constant;
import com.example.asus.syoucloud.util.HttpUtil;
import com.example.asus.syoucloud.util.LrcHandle;

import org.litepal.LitePal;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.example.asus.syoucloud.util.Constant.LIST_LOOP;
import static com.example.asus.syoucloud.util.Constant.SHUFFLE;

public class MusicService extends Service {

    private static final String TAG = "MusicService";

    private NotificationManager notificationManager;
    private MusicPlayer musicPlayer;
    private NotificationReceiver notificationReceiver;
    private OverlayWindowManager overlayWindowManager;
    private RemoteViews remoteViews;
    private Notification notification;
    private WindowManager.LayoutParams params;
    private WindowManager windowManager;
    private View overlayNormal;
    private View overlayClick;

    private boolean hasForeground = false;
    private boolean isLyricShow = false;
    private boolean isLyricClick = false;

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

        new Thread(() -> {
            MusicLoader musicLoader = MusicLoader.getInstance(getContentResolver(), this);
            musicPlayer.setMusicList(musicLoader.getMusicList());
            musicPlayer.initMediaPlayer();
            musicPlayer.initLyricList();
            initWindow();
        }).start();
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

    private void initWindow() {
        params = new WindowManager.LayoutParams();
        windowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            params.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        else params.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
        params.format = PixelFormat.TRANSPARENT;
        params.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        params.gravity = Gravity.TOP | Gravity.START;
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;

        LayoutInflater inflater = LayoutInflater.from(this);
        overlayNormal = inflater.inflate(R.layout.normal_overlay_window, null);
        overlayClick = inflater.inflate(R.layout.click_overlay_window, null);

        overlayWindowManager = new OverlayWindowManager();
    }

    public void getLyricAddress() {
        MusicInfo music = musicPlayer.getMusic();
        String address = Constant.DOWNLOADAPI + music.getTitle();
        HttpUtil.sendHttpUtil(address, new HttpUtil.HttpCallBackListener() {
            @Override
            public void onFinish(String response) {
                String lyricAddress = HttpUtil.parseJsonWithGSON(response);
                if (lyricAddress == null) {
                    Log.i(TAG, "onFinish: 找不到歌词");
                    return;
                }
                LrcHandle lrcHandle = new LrcHandle();
                lrcHandle.readLrcFromInternet(lyricAddress, new LrcHandle.DownloadCallback() {
                    @Override
                    public void onFinish() {
                        Log.i(TAG, "onFinish: success");
                        musicPlayer.setLyricList(lrcHandle.getLyricList());
                    }

                    @Override
                    public void onError() {
                        Log.i(TAG, "onError: ");
                    }
                });
            }

            @Override
            public void onError() {
                Log.i(TAG, "onError: getAddress error");
            }
        });
    }

    public class MusicPlayer extends Binder implements onLyricSeekToListener {
        private static final String TAG = "MusicPlayer";
        private boolean isPlay = false;
        private int playStyle = LIST_LOOP;
        private MediaPlayer mediaPlayer;
        private int id;
        private int albumId;
        private List<MusicInfo> musicList;
        private List<Lyric> lyricList;
        private onMusicListener musicPlayListener;
        private onMusicListener bottomPlayListener;
        private int bottomPlayListenerType;

        private MusicPlayer() {
            mediaPlayer = new MediaPlayer();
            id = 0;
            albumId = -1;
            mediaPlayer.setOnCompletionListener(mp -> next());
        }

        public boolean isPlay() {
            return isPlay;
        }

        public int getDuration() {
            return mediaPlayer.getDuration();
        }

        public int getCurrentProgress() {
            return mediaPlayer.getCurrentPosition();
        }

        public int getPlayStyle() {
            return playStyle;
        }

        public void setPlayStyle(int style) {
            playStyle = style;
        }

        public List<Lyric> getLyricList() {
            return lyricList;
        }

        void setLyricList(List<Lyric> lyricList) {
            this.lyricList = lyricList;
            updateLyricList();
        }

        public MusicInfo getMusic() {
            return musicList.get(id);
        }

        void setMusicList(List<MusicInfo> musicList) {
            this.musicList = musicList;
        }

        private void updateLyricList() {
            if (musicPlayListener != null) musicPlayListener.onUpdateLyric();
            if (overlayWindowManager != null) overlayWindowManager.updateLyric();
        }

        public void setMusicPlayListener(onMusicListener listener) {
            musicPlayListener = listener;
        }

        public void setBottomPlayListener(onMusicListener listener, int type) {
            bottomPlayListener = listener;
            this.bottomPlayListenerType = type;
        }

        public void deleteMusicPlayListener() {
            musicPlayListener = null;
        }

        public void deleteBottomPlayListener(int type) {
            if (type == bottomPlayListenerType)
                bottomPlayListener = null;
        }

        private void initLyricList() {
            LrcHandle lrcHandle = new LrcHandle();
            lrcHandle.readLrcFromFile("/storage/emulated/0/Download/鳥の詩.lrc");
            lyricList = lrcHandle.getLyricList();
            /*if (musicList.size() == 0) return;
            LrcText text = LitePal
                    .where("songId=?", String.valueOf(musicList.get(id).getId()))
                    .findFirst(LrcText.class);
            if (text != null) lyricList = text.getLyricList();
            else {
                lyricList = new ArrayList<>();
                lyricList.add(new Lyric(0, getString(R.string.lyric_hint), null));
            }*/
        }

        private void initMediaPlayer() {
            if (id >= musicList.size()) return;
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
                overlayWindowManager.stopUpd();
            } else {
                mediaPlayer.start();
                overlayWindowManager.startUpd();
                if (!hasForeground) {
                    hasForeground = true;
                    sendCustomNotification();
                } else {
                    remoteViews.setImageViewResource(R.id.notification_play, R.drawable.notification_pause);
                    notificationManager.notify(1, notification);
                }
            }
            if (musicPlayListener != null) musicPlayListener.onMusicPlayOrPause();
            else if (bottomPlayListener != null) bottomPlayListener.onMusicPlayOrPause();
        }

        public void seekTo(int progress) {
            mediaPlayer.seekTo(progress * 1000);
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
            if (musicPlayListener != null) musicPlayListener.onStopUpd();
            if (overlayWindowManager != null) overlayWindowManager.stopUpd();
            mediaPlayer.reset();

            initMediaPlayer();
            mediaPlayer.start();
            isPlay = true;
            if (hasForeground) updateNotification();
            else sendCustomNotification();

            initLyricList();
            if (overlayWindowManager != null) overlayWindowManager.startUpd();
            if (musicPlayListener != null) musicPlayListener.onMusicNext();
            else if (bottomPlayListener != null) bottomPlayListener.onMusicNext();
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
                filter.addAction(Constant.DOWNLOAD);
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
            notificationManager.cancelAll();
            hasForeground = false;
            if (!isPlay) return;
            mediaPlayer.seekTo(0);
            mediaPlayer.pause();
            isPlay = false;

            overlayWindowManager.removeLyric();
            isLyricClick = isLyricShow = false;

            if (musicPlayListener != null) musicPlayListener.onMusicStop();
            else if (bottomPlayListener != null) bottomPlayListener.onMusicStop();
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

    private class OverlayWindowManager implements View.OnTouchListener, View.OnClickListener {

        List<Lyric> lyricList;
        Handler handler = new Handler(getMainLooper());
        private TextView clickLyric;
        private TextView normalLyric;
        private ImageView playImage;
        private int normalLine = -1;
        private int clickLine = -1;
        private int statusBarHeight = -1;
        private boolean isClick = false;
        private boolean isLock = false;
        private float x, y;
        private float touchStartX, touchStartY;

        private Runnable updLyric = new Runnable() {
            @Override
            public void run() {
                handler.postDelayed(this, 400);
                int mLine = findCurrentLine(musicPlayer.getCurrentProgress());
                if (isClick && clickLine == mLine) return;
                if (!isClick && normalLine == mLine) return;
                Lyric lyric = lyricList.get(mLine);
                String text = lyric.getText();
                if (lyric.getTranslate() != null)
                    text = text + "\n" + lyric.getTranslate();
                if (isClick) {
                    clickLine = mLine;
                    clickLyric.setText(text);
                } else {
                    normalLine = mLine;
                    normalLyric.setText(text);
                }
            }
        };

        OverlayWindowManager() {
            lyricList = musicPlayer.getLyricList();
            if (lyricList == null) lyricList = new ArrayList<>();
            normalLyric = overlayNormal.findViewById(R.id.normal_overlay_lyric);
            clickLyric = overlayClick.findViewById(R.id.click_overlay_lyric);
            int resourceId = getResources().getIdentifier("status_bar_height",
                    "dimen", "android");
            if (resourceId > 0) statusBarHeight = getResources().getDimensionPixelSize(resourceId);
            initView();
        }

        private void initView() {
            overlayNormal.setOnTouchListener(this);
            overlayNormal.setOnClickListener(this);
            overlayClick.setOnClickListener(this);
            overlayClick.setOnTouchListener(this);
            playImage = overlayClick.findViewById(R.id.overlay_play);
            ImageView lastImage = overlayClick.findViewById(R.id.overlay_last);
            ImageView nextImage = overlayClick.findViewById(R.id.overlay_next);
            ImageView closeImage = overlayClick.findViewById(R.id.overlay_close);
            ImageView lockImage = overlayClick.findViewById(R.id.overlay_lock);

            Intent closeIntent = new Intent(Constant.LYRIC);
            closeImage.setOnClickListener(v -> sendBroadcast(closeIntent));

            Intent nextIntent = new Intent(Constant.NEXT);
            nextImage.setOnClickListener(v -> sendBroadcast(nextIntent));

            Intent lastIntent = new Intent(Constant.LAST);
            lastImage.setOnClickListener(v -> sendBroadcast(lastIntent));

            Intent playIntent = new Intent(Constant.PLAY);
            playImage.setOnClickListener(v -> sendBroadcast(playIntent));

            lockImage.setOnClickListener(v -> lockLyric());
        }

        public void showLyric() {
            windowManager.addView(overlayNormal, params);
            if (musicPlayer.isPlay) playImage.setImageResource(R.drawable.pause_button);
            else playImage.setImageResource(R.drawable.play_button);
            handler.post(updLyric);
        }

        public void updateLyric() {
            handler.removeCallbacks(updLyric);
            lyricList = musicPlayer.getLyricList();
            handler.post(updLyric);
        }

        public void removeLyric() {
            if (isClick) windowManager.removeView(overlayClick);
            else windowManager.removeView(overlayNormal);
            isClick = false;
        }

        public void stopUpd() {
            if (!isLyricShow) return;
            playImage.setImageResource(R.drawable.play_button);
            handler.removeCallbacks(updLyric);
        }

        public void startUpd() {
            if (!isLyricShow) return;
            playImage.setImageResource(R.drawable.pause_button);
            handler.post(updLyric);
        }

        private void lockLyric() {
            isLock = true;
            params.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE;
            onClick(clickLyric);

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

        private int findCurrentLine(int time) {
            int l = 0, r = lyricList.size() - 1;
            while (l <= r) {
                int mid = (l + r) / 2;
                if (lyricList.get(mid).getTime() <= time) l = mid + 1;
                else r = mid - 1;
            }
            return r;
        }

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            x = event.getRawX();
            y = event.getRawY() - statusBarHeight;
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    touchStartX = event.getX();
                    touchStartY = event.getY();
                    break;
                case MotionEvent.ACTION_UP:
                    updateViewPosition();
                    touchStartX = touchStartY = 0;
                    break;
                case MotionEvent.ACTION_MOVE:
                    updateViewPosition();
                    break;
                default:
            }
            return false;
        }

        @Override
        public void onClick(View v) {
            if (isClick) {
                windowManager.addView(overlayNormal, params);
                windowManager.removeView(overlayClick);
            } else {
                windowManager.addView(overlayClick, params);
                windowManager.removeView(overlayNormal);
            }
            isClick = !isClick;
            handler.removeCallbacks(updLyric);
            handler.post(updLyric);
        }

        private void updateViewPosition() {
            params.x = (int) (x - touchStartX);
            params.y = (int) (y - touchStartY);
            if (isClick) windowManager.updateViewLayout(overlayClick, params);
            else windowManager.updateViewLayout(overlayNormal, params);
        }

        public void unlock() {
            isLock = false;
            params.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
            windowManager.updateViewLayout(overlayNormal, params);
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
                    isLyricClick = !isLyricClick;
                    if (isLyricClick) {
                        remoteViews.setTextColor(R.id.notification_lyric, Color.RED);
                        if (MusicApplication.getActiveActivity() == 0) {
                            overlayWindowManager.showLyric();
                            isLyricShow = true;
                        }
                    } else {
                        remoteViews.setTextColor(R.id.notification_lyric, Color.BLACK);
                        if (isLyricShow) {
                            overlayWindowManager.removeLyric();
                            if (overlayWindowManager.isLock) {
                                overlayWindowManager.isLock = false;
                                params.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
                                notificationManager.cancel(2);
                            }
                            isLyricShow = false;
                        }
                    }
                    notificationManager.notify(1, notification);
                    break;
                case Constant.BACKGROUND:
                    if (!isLyricClick) return;
                    overlayWindowManager.showLyric();
                    isLyricShow = true;
                    break;
                case Constant.FOREGROUND:
                    if (!isLyricClick || !isLyricShow) return;
                    overlayWindowManager.removeLyric();
                    isLyricShow = false;
                    break;
                case Constant.UNLOCK:
                    overlayWindowManager.unlock();
                    break;
                case Constant.HEADSET:
                    if (intent.getIntExtra("state", 0) == 0 && musicPlayer.isPlay)
                        musicPlayer.playOrPause();
                    break;
                case Constant.DOWNLOAD:
//                    if (musicPlayer.getLyricList().size() <= 1)
//                        getLyricAddress();
                    break;
                default:
            }
        }
    }
}