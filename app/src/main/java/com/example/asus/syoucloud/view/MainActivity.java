package com.example.asus.syoucloud.view;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.asus.syoucloud.R;
import com.example.asus.syoucloud.bean.MixItem;
import com.example.asus.syoucloud.util.ActivityUtils;
import com.example.asus.syoucloud.util.Constant;
import com.example.asus.syoucloud.util.RecyclerDivider;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private DrawerLayout mDrawerLayout;
    private MixListAdapter adapter;

    private int mixId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        requestReadPermission();

        initToolbar();
        initView();
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addCategory(Intent.CATEGORY_HOME);
        startActivity(intent);
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
        ActivityUtils.addFragmentToActivity(getSupportFragmentManager(),
                BottomLayoutFragment.getInstance(), R.id.bottom_layout);
    }

    private void initToolbar() {
        Toolbar toolbar = findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.menu_open_icon);
        }
    }

    private void initView() {
        mDrawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener((item) -> {
            mDrawerLayout.closeDrawers();
            return true;
        });

        ImageView mixAddImage = findViewById(R.id.mix_add);
        mixAddImage.setOnClickListener(v -> mkMixAddDialog());

        LinearLayout localMusic = findViewById(R.id.main_local_music);
        localMusic.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, MusicShowActivity.class);
            startActivity(intent);
        });

        RecyclerView mixRecycler = findViewById(R.id.mix_recycler);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mixRecycler.setLayoutManager(layoutManager);
        adapter = new MixListAdapter(getApplicationContext());
        mixRecycler.setAdapter(adapter);
        mixRecycler.addItemDecoration(new RecyclerDivider(getApplicationContext(),
                LinearLayoutManager.VERTICAL, 1, Constant.ITEM_DECORATION));
    }

    public void mkMixAddDialog() {
        LayoutInflater factory = LayoutInflater.from(this);
        final View view = factory.inflate(R.layout.add_mix_layout, null);
        final EditText editTitle = view.findViewById(R.id.add_mix_title);
        final EditText editPassword = view.findViewById(R.id.add_mix_password);
        final EditText mEditPassword = view.findViewById(R.id.add_mix_password_again);
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setIcon(R.drawable.mix_add_dialog_icon)
                .setPositiveButton("OK", null)
                .setTitle("New Mix")
                .setView(view)
                .show();
        dialog.getButton(AlertDialog.BUTTON_POSITIVE)
                .setOnClickListener(v1 -> {
                    String title = editTitle.getText().toString();
                    String password = editPassword.getText().toString();
                    String mPassword = mEditPassword.getText().toString();
                    if (title.equals("") || !mPassword.equals(password)) {
                        Toast.makeText(MainActivity.this, "Please input title",
                                Toast.LENGTH_SHORT).show();
                    } else {
                        addMix(title, editPassword.getText().toString());
                        dialog.dismiss();
                    }
                });
    }

    private void addMix(String title, String password) {
        MixItem item = new MixItem();
        item.setTitle(title);
        item.setPassword(password);
        item.setAlbumId(mixId++);
        item.save();
        adapter.add(item);

        SharedPreferences.Editor editor =
                PreferenceManager.getDefaultSharedPreferences(this).edit();
        editor.putInt("MixMax", mixId);
        editor.apply();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                break;
            default:
        }
        return true;
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
                ActivityUtils.addFragmentToActivity(getSupportFragmentManager(),
                        BottomLayoutFragment.getInstance(), R.id.bottom_layout);
                break;
            default:
        }
    }
}