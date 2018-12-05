package com.example.asus.syoucloud;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.example.asus.syoucloud.data.DataRepository;
import com.example.asus.syoucloud.util.BitmapHelper;
import com.example.asus.syoucloud.util.ThreadPool;

public class AdvertiseActivity extends AppCompatActivity {

    public static final int sleepTime = 1000;
    private static final String TAG = "AdvertiseActivity";
    private int time = 4;

    private TextView timeText;
    private Handler handler = new Handler();

    private Runnable run = new Runnable() {
        @Override
        public void run() {
            timeText.setText(String.valueOf(time));
            time--;
            if (time == 0) {
                Intent intent = new Intent(AdvertiseActivity.this, MainActivity.class);
                startActivity(intent);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                finish();
            } else handler.postDelayed(this, sleepTime);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_advertise);
        timeText = findViewById(R.id.advertise_time);
        requestReadPermission();
    }

    // add prepare operation here
    private void start() {
        ThreadPool.getInstance().execute(() -> DataRepository.getInstance().initDatabase(getApplicationContext()));
        BitmapHelper.init(getResources().getDisplayMetrics().density, getContentResolver());
        handler.post(run);
    }

    private void requestReadPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        } else requestOverlayWindowPermission();
    }

    private void requestOverlayWindowPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(this)) {
                Toast.makeText(this, "Please allow draw overlay permission",
                        Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:" + getPackageName()));
                startActivityForResult(intent, 1);
                return;
            }
        }
        start();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    requestOverlayWindowPermission();
                    Log.i(TAG, "onRequestPermissionsResult: succeed");
                } else {
                    Log.i(TAG, "onRequestPermissionsResult: false");
                    Toast.makeText(this, "If we can not acquire this permission, "
                            + "the app can not work", Toast.LENGTH_SHORT).show();
                    finish();
                }
            default:
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        switch (requestCode) {
            case 1:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (!Settings.canDrawOverlays(this)) {
                        Toast.makeText(this, "If we can not acquire this permission, "
                                + "the app can not work", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                }
                start();
                break;
            default:
        }
    }
}