package com.example.asus.syoucloud.util;

import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

public class ActivityUtils {
    public static void addFragmentToActivity(@NonNull FragmentManager fragmentManager,
                                             @NonNull Fragment fragment, int frameId) {
        fragmentManager.beginTransaction()
                .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
                .add(frameId, fragment)
                .commit();
    }

    public static void changeFragmentInActivity(@NonNull FragmentManager fragmentManager,
                                                @NonNull Fragment fragment,
                                                @NonNull Fragment mFragment) {
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
        transaction.hide(fragment);
        transaction.show(mFragment);
        transaction.commit();
    }
}
