package com.example.listenbook;

import android.os.Handler;
import android.os.Looper;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

public class TimingThread extends Thread {
    private static LinearLayout fullSongInformation;
    private static LinearLayout smallSongInformation;
    private static LinearLayout playButtonsLayout;
    private volatile boolean isPlaying = true;

    static Thread myThread = new TimingThread();

    public static Thread getThread() {
        return myThread;
    }
    public static void aboutSong(LinearLayout fullSongInformation,
                                 LinearLayout smallSongInformation,
                                 LinearLayout playButtonsLayout) {
        TimingThread.fullSongInformation = fullSongInformation;
        TimingThread.smallSongInformation = smallSongInformation;
        TimingThread.playButtonsLayout = playButtonsLayout;
    }

    public String convertMils(int totalDuration) {
        int hours = totalDuration / (1000 * 60 * 60);
        int minutes = (totalDuration % (1000 * 60 * 60)) / (1000 * 60);
        int seconds = ((totalDuration % (1000 * 60 * 60)) % (1000 * 60)) / 1000;
        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }
    @Override
    public void run() {
        while (isPlaying) {
            if (PlayPanelActivity.mediaPlayer.isPlaying() && PlayPanelActivity.playButton) {
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                new Handler(Looper.getMainLooper()).post(() -> {
                    PlayPanelActivity.updateInfo();
                });
            }
        }
    }
    public void stopThread() {
        isPlaying = false;
        try {
            join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    public void runThread() {
        stopThread();
        isPlaying = true;
        new TimingThread().start();
    }
}
