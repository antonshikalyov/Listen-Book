package com.example.listenbook;

import android.app.Activity;
import android.content.Context;
import android.media.MediaMetadataRetriever;
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

    private static LinearLayout fullSongInformation;
    private static LinearLayout smallSongInformation;
    private static LinearLayout playButtonsLayout;
    static MediaPlayer mediaPlayer = new MediaPlayer();
    static AudioItem currentChapter;
    static String[] chapters;

    private static TimingThread timingThread;
    public static MediaPlayer getMediaPlayer() {
        return mediaPlayer;
    }
    public boolean playStatus() {
        return playButton;
    }
    
    public static void playMusic(String path) {
        if (timingThread == null) {
            timingThread = new TimingThread();
            timingThread.setDaemon(true);
            timingThread.start();
        }
        try {
            mediaPlayer.reset();
            mediaPlayer.setDataSource(path);
            mediaPlayer.prepareAsync();
            mediaPlayer.setOnPreparedListener(mp -> {
                mediaPlayer.start();
                playButton = false;
                PlayPanelActivity.resumeMusic();
            });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public static void resumeMusic() {
        ImageButton resumeButton = playButtonsLayout.findViewById(R.id.resumeButton);
        if (mediaPlayer != null) {
            if (!playButton) {
                mediaPlayer.start();
                resumeButton.setImageResource(R.drawable.ic_pause);
                playButton = true;
            } else {
                mediaPlayer.pause();
                resumeButton.setImageResource(R.drawable.ic_play);
                playButton = false;
            }
        }
    }


    public static void nextChapter() {
        int id = currentChapter.id + 1;
        if (id < chapters.length) {
            if (playButton) {
                resumeMusic();
            }
            mediaPlayer.stop();
            currentChapter = new AudioItem(id, chapters[id]);
            Activity mainActivity = new MainActivity();
            SongActivity.getInfoFromManifest(currentChapter.uri, mainActivity);
            playMusic(chapters[id]);
            MediaMetadataRetriever retriever = new MediaMetadataRetriever();
            retriever.setDataSource(currentChapter.uri);
            TextView t = fullSongInformation.findViewById(R.id.sample_text);
            t.setText(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE));
        }
    }

    public static void previousChapter() {
        int id = currentChapter.id - 1;
        if (id >= 0) {
            if (playButton) {
                resumeMusic();
            }
            mediaPlayer.stop();
            currentChapter = new AudioItem(id, chapters[id]);
            Activity mainActivity = new MainActivity();
            SongActivity.getInfoFromManifest(currentChapter.uri, mainActivity);
            playMusic(chapters[id]);
            MediaMetadataRetriever retriever = new MediaMetadataRetriever();
            retriever.setDataSource(currentChapter.uri);
            TextView t = fullSongInformation.findViewById(R.id.sample_text);
            t.setText(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE));
        }
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
                int id = currentChapter.id + 1;
                if (id < chapters.length) {
                    PlayPanelActivity.nextChapter();
                }
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
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });
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



