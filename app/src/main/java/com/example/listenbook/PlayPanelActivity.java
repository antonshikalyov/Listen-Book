package com.example.listenbook;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import java.io.IOException;

public class PlayPanelActivity {

    static boolean playButton = false;
    private static boolean isUserSeeking = false;
    private static boolean isUserSeekingVolume = false;

    private static LinearLayout fullSongInformation;
    private static LinearLayout smallSongInformation;
    private static LinearLayout playButtonsLayout;
    static MediaPlayer mediaPlayer = new MediaPlayer();
    static int duration = 0;

    static TimingThread x;
    public static MediaPlayer getMediaPlayer() {
        return mediaPlayer;
    }
    public boolean playStatus() {
        return playButton;
    }

    public void playMusic(Uri uri, Context context) {
        x = (TimingThread) TimingThread.getThread();
        try {
            mediaPlayer.reset();
            mediaPlayer.setDataSource(context.getApplicationContext(), uri);
            mediaPlayer.prepare();
            mediaPlayer.start();
            playButton = true;
            x.runThread();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public static MediaPlayer myPlayer() {
        return mediaPlayer;
    }
    public static void resumeMusic(ImageButton resumeButton) {
        if (!playButton) {
            mediaPlayer.start();
            resumeButton.setImageResource(R.drawable.ic_pause);
            playButton = true;
            x.runThread();
        } else {
            mediaPlayer.pause();
            resumeButton.setImageResource(R.drawable.ic_play);
            playButton = false;
            x.stopThread();
        }
    }

    public static void seekBarDuration(SeekBar seekBar) {
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            private void updateSeekBar() {
                if (mediaPlayer != null && !isUserSeeking) {
                    int currentPosition = mediaPlayer.getCurrentPosition();
                    seekBar.setProgress(currentPosition);
                    updateInfo();
                }
            }
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if (b) {
                    mediaPlayer.seekTo(i);
                    Log.i("Progress: ", String.valueOf(i));
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                isUserSeeking = true;
                updateSeekBar();
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                isUserSeeking = false;
                updateSeekBar();
            }

        });

        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                seekBar.setProgress(0);
            }
        });

    }

    public static void seekBarVolume(SeekBar seekBar) {
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                float volume = (float) i / seekBar.getMax();
                mediaPlayer.setVolume(volume, volume);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    public static String convertMils(int totalDuration) {
        int hours = totalDuration / (1000 * 60 * 60);
        int minutes = (totalDuration % (1000 * 60 * 60)) / (1000 * 60);
        int seconds = ((totalDuration % (1000 * 60 * 60)) % (1000 * 60)) / 1000;
        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }
    public static void aboutSong(LinearLayout fullSongInformation,
                                 LinearLayout smallSongInformation,
                                 LinearLayout playButtonsLayout) {
        PlayPanelActivity.fullSongInformation = fullSongInformation;
        PlayPanelActivity.smallSongInformation = smallSongInformation;
        PlayPanelActivity.playButtonsLayout = playButtonsLayout;
    }
    public static void updateInfo() {
        TextView durationFullInfo = fullSongInformation.findViewById(R.id.duration_time);
        TextView fullSongInfoReaded = fullSongInformation.findViewById(R.id.readed_teme);
        TextView durationSmallInfo = smallSongInformation.findViewById(R.id.small_duration_time);
        TextView smallSongInfoReaded = smallSongInformation.findViewById(R.id.small_readed_teme);
        TextView playSongInfoReaded = playButtonsLayout.findViewById(R.id.playReaded);
        TextView playSongInfoSurplus = playButtonsLayout.findViewById(R.id.playSurplus);
        SeekBar seekBar = playButtonsLayout.findViewById(R.id.simpleSeekBar);

        seekBar.setMin(0);
        seekBar.setMax(PlayPanelActivity.getMediaPlayer().getDuration());
        seekBar.setProgress(PlayPanelActivity.getMediaPlayer().getCurrentPosition());
        PlayPanelActivity.seekBarDuration(seekBar);

        durationFullInfo.setText(convertMils(PlayPanelActivity.getMediaPlayer().getDuration()));
        durationSmallInfo.setText(convertMils(PlayPanelActivity.getMediaPlayer().getDuration()));
        smallSongInfoReaded.setText(convertMils(PlayPanelActivity.getMediaPlayer().getCurrentPosition()));
        fullSongInfoReaded.setText(convertMils(PlayPanelActivity.getMediaPlayer().getCurrentPosition()));
        playSongInfoReaded.setText(convertMils(PlayPanelActivity.getMediaPlayer().getCurrentPosition()));
        playSongInfoSurplus.setText(convertMils(PlayPanelActivity.getMediaPlayer().getDuration()));
    }

}



