package com.example.asus.syoucloud.musicManager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.example.asus.syoucloud.Constant;

public class NotificationReceiver extends BroadcastReceiver{
    @Override
    public void onReceive(Context context, Intent intent) {
        String actionCode = intent.getAction();
        if (actionCode == null) return;
        switch (actionCode) {
            case Constant.PLAY:
                break;
            case Constant.NEXT:
                break;
            case Constant.LAST:
                break;
            case Constant.CANCEL:
                break;
            case Constant.LYRIC:
                break;
            default:
        }
    }
}