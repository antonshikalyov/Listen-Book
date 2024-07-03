package com.example.listenbook;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;

public class AppLifecycleObserver implements Application.ActivityLifecycleCallbacks {

    private int activityCount = 0;

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
    }

    @Override
    public void onActivityStarted(Activity activity) {
        activityCount++;
    }

    @Override
    public void onActivityResumed(Activity activity) {
    }

    @Override
    public void onActivityPaused(Activity activity) {
    }

    @Override
    public void onActivityStopped(Activity activity) {
        Log.i("closed1", "closed1");
        activityCount--;
        if (activityCount == 0) {
            Log.i("closed", "closed");
        }
    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
    }

    @Override
    public void onActivityDestroyed(@NonNull Activity activity) {

        activityCount--;
        if (activityCount == 0) {
            Log.i("closed", "closed");

        }
    }

    @Override
    public void onActivityPreStopped(@NonNull Activity activity) {
        Application.ActivityLifecycleCallbacks.super.onActivityPreStopped(activity);
    }

    @Override
    public void onActivityPostStopped(@NonNull Activity activity) {
        Application.ActivityLifecycleCallbacks.super.onActivityPostStopped(activity);

    }
}