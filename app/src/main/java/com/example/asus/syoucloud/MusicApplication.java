package com.example.asus.syoucloud;

import android.app.Activity;
import android.app.Application;
import android.content.Intent;
import android.os.Bundle;

import com.example.asus.syoucloud.util.Constant;

import org.litepal.LitePal;

public class MusicApplication extends Application {

    private static int activeActivity = 0;

    public static int getActiveActivity() {
        return activeActivity;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        LitePal.initialize(this);

        Intent backIntent = new Intent(Constant.BACKGROUND);
        Intent foreIntent = new Intent(Constant.FOREGROUND);
        registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(Activity activity, Bundle savedInstanceState) {

            }

            @Override
            public void onActivityStarted(Activity activity) {
                activeActivity++;
                sendBroadcast(foreIntent);
            }

            @Override
            public void onActivityResumed(Activity activity) {

            }

            @Override
            public void onActivityPaused(Activity activity) {

            }

            @Override
            public void onActivityStopped(Activity activity) {
                activeActivity--;
                if (activeActivity == 0)
                    sendBroadcast(backIntent);
            }

            @Override
            public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

            }

            @Override
            public void onActivityDestroyed(Activity activity) {

            }
        });
    }
}
