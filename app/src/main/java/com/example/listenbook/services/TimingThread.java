package com.example.listenbook.services;

import android.os.Handler;
import android.os.Looper;

import com.example.listenbook.activities.PlayPanelActivity;

public class TimingThread extends Thread {
    private volatile boolean isPlaying = true;

    @Override
    public void run() {

        while (isPlaying) {
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                new Handler(Looper.getMainLooper()).post(PlayPanelActivity::updateInfo);
        }
    }

}
