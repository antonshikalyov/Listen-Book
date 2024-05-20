package com.example.listenbook;

import android.os.Handler;
import android.os.Looper;

public class TimingThread extends Thread {
    private volatile boolean isPlaying = true;

    @Override
    public void run() {

        while (isPlaying) {
            if (PlayPanelActivity.getMediaPlayer().isPlaying() && PlayPanelActivity.playButton) {
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                new Handler(Looper.getMainLooper()).post(PlayPanelActivity::updateInfo);
            }
        }
    }

}
