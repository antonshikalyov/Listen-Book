package com.example.listenbook.activities.play_track_activity;

import static android.content.Context.MODE_PRIVATE;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioAttributes;
import android.media.AudioFocusRequest;
import android.media.AudioManager;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import androidx.appcompat.app.AlertDialog;
import androidx.media3.common.MediaItem;
import androidx.media3.common.MediaMetadata;
import androidx.media3.session.MediaController;

import com.example.listenbook.R;
import com.example.listenbook.entities.AudioItem;
import com.example.listenbook.entities.Book;
import com.example.listenbook.entities.Bookmark;
import com.example.listenbook.services.DataBase;
import com.example.listenbook.services.MediaPlaybackService;
import com.example.listenbook.services.TimingThread;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicLong;

public class PlayPanelActivity implements AudioManager.OnAudioFocusChangeListener {
    public static boolean playButton = false;
    private static boolean isUserSeeking = false;
    @SuppressLint("StaticFieldLeak")
    private static LinearLayout fullSongInformation;
    @SuppressLint("StaticFieldLeak")
    private static LinearLayout smallSongInformation;
    @SuppressLint("StaticFieldLeak")
    public static LinearLayout playButtonsLayout;
    @SuppressLint("StaticFieldLeak")
    public static PlayListAdapter itemTrackList;
    private static boolean mPlaybackDelayed;
    public static AudioManager mAudioManager;
    private static final Object mFocusLock = new Object();
    private static boolean mResumeOnFocusGain;
    private static float volumeSeekBar = -1;
    private static final Handler mMyHandler = new Handler(Looper.getMainLooper());
    private static AudioFocusRequest mFocusRequest;
    public static AudioItem currentChapter;
    public static Book currentBook;
    public static ArrayList<AudioItem> chapters;

    private static TimingThread timingThread;
    public boolean playStatus() {
        return playButton;
    }

    public static void playMusic() {
        if (timingThread == null) {
            timingThread = new TimingThread();
            timingThread.setDaemon(true);
            timingThread.start();
        }

    }

    public static void setInfo() {
        MediaController mediaController = MediaPlaybackService.getMediaController();
        MediaItem mediaItem1 = mediaController.getMediaItemAt(mediaController.getCurrentMediaItemIndex());
        MediaMetadata mediaItem = mediaItem1.mediaMetadata;
        currentChapter = chapters.get(mediaController.getCurrentMediaItemIndex());
        Handler mainHandler = new Handler(Looper.getMainLooper());
        mainHandler.post(() -> {
            CharSequence title = currentBook.title;
            byte[] artworkData = mediaItem.artworkData;

            ImageView songImage = fullSongInformation.findViewById(R.id.imageView);
            ImageView smallSongImage = smallSongInformation.findViewById(R.id.small_imageView);
            TextView songName = fullSongInformation.findViewById(R.id.sample_text);
            TextView smallSongName = smallSongInformation.findViewById(R.id.small_sample_text);
            TextView songChapterPlayPanel = playButtonsLayout.findViewById(R.id.song_info_play_panel);

            if (artworkData != null) {
                Bitmap artworkBitmap = BitmapFactory.decodeByteArray(artworkData, 0, artworkData.length);
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                artworkBitmap.compress(Bitmap.CompressFormat.JPEG, 50, stream);

                songImage.setImageBitmap(artworkBitmap);
                smallSongImage.setImageBitmap(artworkBitmap);
            } else {
                songImage.setImageResource(R.drawable.carplay_book);
                smallSongImage.setImageResource(R.drawable.carplay_book);
            }

            if (title != null) {
                songName.setText(title);
                smallSongName.setText(title);
            }
            if (currentChapter != null) {
                songChapterPlayPanel.setText(currentChapter.title);
            }
        });
    }

    public static void resumeMusic() {
        if (MediaPlaybackService.getMediaController() != null) {
            if (!playButton) {
                MediaPlaybackService.getMediaController().play();
            } else {
                MediaPlaybackService.getMediaController().pause();
            }
        }
    }

    @SuppressLint("SetTextI18n")
    public static void addBookmark() {
        AlertDialog.Builder builder = new AlertDialog.Builder(playButtonsLayout.getContext(), R.style.NoBackgroundDialog);

        LayoutInflater layoutInflater = (LayoutInflater) playButtonsLayout.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View customLayout = layoutInflater.inflate(R.layout.dialog_add_bookmark, null);
        EditText editText = customLayout.findViewById(R.id.dialog_add_bookmark_editText);
        TextView time = customLayout.findViewById(R.id.dialog_add_bookmark);
        AtomicLong readedGlobalDuration = new AtomicLong(currentChapter.positionInBook + MediaPlaybackService.getMediaController().getCurrentPosition());
        builder.setView(customLayout);

        String chapterString = playButtonsLayout.getContext().getString(R.string.chapter);
        editText.setText(chapterString + ": " + currentChapter.title);
        time.setText(convertMils(Math.toIntExact(readedGlobalDuration.get())));

        AlertDialog dialog = builder.create();

        TextView songName = customLayout.findViewById(R.id.dialog_add_bookmark_title);
        songName.setText(PlayPanelActivity.currentBook.title);

        customLayout.findViewById(R.id.dialog_add_bookmark_plus_30sec).setOnClickListener(v -> {
            if ((readedGlobalDuration.get() + 30000L) < currentBook.book_duration) {
                readedGlobalDuration.addAndGet(30000L);
                updateCurrentChapter(readedGlobalDuration.get(), editText, time, chapterString);
            }
        });

        customLayout.findViewById(R.id.dialog_add_bookmark_minus_30sec).setOnClickListener(v -> {
            if ((readedGlobalDuration.get() - 30000L) >= 0) {
                readedGlobalDuration.addAndGet(-30000L);
                updateCurrentChapter(readedGlobalDuration.get(), editText, time, chapterString);
            }
        });

        customLayout.findViewById(R.id.dialog_add_bookmark_save).setOnClickListener(v -> {
            String text = String.valueOf(editText.getText());
            if (!text.isEmpty()) {
                dialog.dismiss();
            }
        });

        customLayout.findViewById(R.id.dialog_add_bookmark_cancel).setOnClickListener(v -> {
            dialog.dismiss();
        });

        customLayout.findViewById(R.id.dialog_add_bookmark_save).setOnClickListener(v -> {
            DataBase dataBase = new DataBase(playButtonsLayout.getContext());
            Bookmark bookmark = new Bookmark(currentBook.id, currentBook.title, editText.getText().toString(), readedGlobalDuration.get());
            dataBase.insertBookmark(bookmark);
            dialog.dismiss();
        });

        dialog.show();
    }

    @SuppressLint("SetTextI18n")
    private static void updateCurrentChapter(long newDuration, EditText editText, TextView time, String chapterString) {
        int newChapterIndex = binarySearchChapter(newDuration);
        currentChapter = chapters.get(newChapterIndex);
        editText.setText(chapterString + ": " + currentChapter.title);
        time.setText(convertMils(Math.toIntExact(newDuration)));
    }

    public static int binarySearchChapter(long duration) {
        int low = 0;
        int high = chapters.size() - 1;

        while (low <= high) {
            int mid = (low + high) / 2;
            Log.i("defMid", String.valueOf(mid));
            AudioItem midChapter = chapters.get(mid);
            Log.i("midChapter", String.valueOf(midChapter));
            if (midChapter.positionInBook == duration) {
                return mid;
            } else if (midChapter.positionInBook < duration) {
                low = mid + 1;
            } else {
                high = mid - 1;
            }
        }
        return high;
    }



    public static void play() {
        ImageButton resumeButton = playButtonsLayout.findViewById(R.id.resumeButton);
        if (MediaPlaybackService.getMediaController() != null) {
            int res = mAudioManager.requestAudioFocus(mFocusRequest);
            synchronized (mFocusLock) {
                if (res == AudioManager.AUDIOFOCUS_REQUEST_FAILED) {
                    mPlaybackDelayed = false;
                } else if (res == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
                    resumeButton.setImageResource(R.drawable.ic_pause);
                    MediaPlaybackService.updateNotification(PlayPanelActivity.playButtonsLayout.getContext(), R.drawable.ic_pause);
                    playButton = true;
                } else if (res == AudioManager.AUDIOFOCUS_REQUEST_DELAYED) {
                    mPlaybackDelayed = true;
                }
            }
        }
    }

    public static void pause () {
        ImageButton resumeButton = playButtonsLayout.findViewById(R.id.resumeButton);
        resumeButton.setImageResource(R.drawable.ic_play);
        MediaPlaybackService.updateNotification(PlayPanelActivity.playButtonsLayout.getContext(), R.drawable.ic_play);
        playButton = false;
    }

    public static void seekBarDuration(SeekBar seekBar) {
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if (b) {
                    MediaPlaybackService.getMediaController().seekTo(i);
                    Log.i("Progress: ", String.valueOf(i));
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                isUserSeeking = true;
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                isUserSeeking = false;
            }

        });
    }

    public static void seekBarVolume(SeekBar seekBar) {
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                volumeSeekBar = (float) i / seekBar.getMax();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });
    }

    public static void updateInfo() {
        TextView durationFullInfo = fullSongInformation.findViewById(R.id.duration_time);
        TextView fullSongInfoReaded = fullSongInformation.findViewById(R.id.readed_time);
        TextView durationSmallInfo = smallSongInformation.findViewById(R.id.small_duration_time);
        TextView smallSongInfoReaded = smallSongInformation.findViewById(R.id.small_readed_teme);
        TextView playSongInfoReaded = playButtonsLayout.findViewById(R.id.playReaded);
        TextView playSongInfoSurplus = playButtonsLayout.findViewById(R.id.playSurplus);


        SeekBar seekBar1 = playButtonsLayout.findViewById(R.id.simpleSeekBar);
        int currentPosition = (int) MediaPlaybackService.getMediaController().getCurrentPosition();

        seekBar1.setMax((int) MediaPlaybackService.mediaController.getDuration());
        seekBar1.setProgress(currentPosition);

        if (itemTrackList != null) {
            int currentIndex = MediaPlaybackService.mediaController.getCurrentMediaItemIndex();
            itemTrackList.updateSeekBarProgress(currentIndex);
        }


////        View itemView = itemTrackList.getView(MediaPlaybackService.mediaController.getCurrentMediaItemIndex(), null, null);
        SeekBar seekBar =  playButtonsLayout.findViewById(R.id.simpleSeekBar);
        seekBar.setMin(0);
        seekBar.setMax((int) MediaPlaybackService.mediaController.getDuration());
        seekBar.setProgress((int) MediaPlaybackService.mediaController.getCurrentPosition());
        PlayPanelActivity.seekBarDuration(seekBar);



        durationFullInfo.setText(convertMils(Math.toIntExact(currentBook.book_duration)));
        durationSmallInfo.setText(convertMils(Math.toIntExact(currentBook.book_duration)));
        smallSongInfoReaded.setText(convertMils(Math.toIntExact(currentChapter.positionInBook + MediaPlaybackService.getMediaController().getCurrentPosition())));
        fullSongInfoReaded.setText(convertMils(Math.toIntExact(currentChapter.positionInBook + MediaPlaybackService.getMediaController().getCurrentPosition())));
        playSongInfoReaded.setText(convertMils((int) MediaPlaybackService.getMediaController().getCurrentPosition()));
        playSongInfoSurplus.setText(convertMils((int) MediaPlaybackService.getMediaController().getDuration()));
    }

    @SuppressLint("DefaultLocale")
    public static String convertMils(int totalDuration) {
        int hours = totalDuration / (1000 * 60 * 60);
        int minutes = (totalDuration % (1000 * 60 * 60)) / (1000 * 60);
        int seconds = ((totalDuration % (1000 * 60 * 60)) % (1000 * 60)) / 1000;
        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }
    @SuppressLint("SetTextI18n")
    public static void aboutSong(LinearLayout fullSongInformation,
                                 LinearLayout smallSongInformation,
                                 LinearLayout playButtonsLayout,
                                 PlayListAdapter itemTrackList) {

        PlayPanelActivity.fullSongInformation = fullSongInformation;
        PlayPanelActivity.smallSongInformation = smallSongInformation;
        PlayPanelActivity.playButtonsLayout = playButtonsLayout;
        PlayPanelActivity.itemTrackList = itemTrackList;

        TextView stepForwardText = playButtonsLayout.findViewById(R.id.forwardButtonText);
        TextView stepBackText = playButtonsLayout.findViewById(R.id.backButtonText);


        SharedPreferences sharedPreferences = playButtonsLayout.getContext().getSharedPreferences("settings", MODE_PRIVATE);
        long stepForwardSettings = sharedPreferences.getLong("STEP_REWIND_SETTINGS", 10000);

        switch (Math.toIntExact(stepForwardSettings)) {
            case  (5000):
                stepForwardText.setText("5s");
                stepBackText.setText("5s");
                break;
            case (15000):
                stepForwardText.setText("15s");
                stepBackText.setText("15s");
                break;
            case  (30000):
                stepForwardText.setText("30s");
                stepBackText.setText("30s");
                break;
            case (60000):
                stepForwardText.setText("1m");
                stepBackText.setText("1m");
                break;
            case (120000):
                stepForwardText.setText("2m");
                stepBackText.setText("2m");
                break;
            case (300000):
                stepForwardText.setText("5m");
                stepBackText.setText("5m");
                break;
            case (600000):
                stepForwardText.setText("10m");
                stepBackText.setText("10m");
                break;
            default:
                stepForwardText.setText("10s");
                stepBackText.setText("10s");
                break;
        }
    }

    public void setAttributes(boolean flag, int startIndex, long position) {
        mAudioManager = (AudioManager) playButtonsLayout.getContext().getSystemService(Context.AUDIO_SERVICE);
        AudioAttributes mPlaybackAttributes = new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_MEDIA)
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                .build();
        mFocusRequest = new AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN)
                .setAudioAttributes(mPlaybackAttributes)
                .setAcceptsDelayedFocusGain(true)
                .setWillPauseWhenDucked(true)
                .setOnAudioFocusChangeListener(this, mMyHandler)
                .build();

        int res = mAudioManager.requestAudioFocus(mFocusRequest);
        synchronized (mFocusLock) {
            if (res == AudioManager.AUDIOFOCUS_REQUEST_FAILED) {
                mPlaybackDelayed = false;
            } else if (res == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
                if (!flag) {
                    PlayPanelActivity.playMusic();
                    MediaPlaybackService.playTrack();
                } else {
                    PlayPanelActivity.playMusic();
                    MediaPlaybackService.playBookmark(startIndex, position);
                }
            } else if (res == AudioManager.AUDIOFOCUS_REQUEST_DELAYED) {
                mPlaybackDelayed = true;
            }
        }
    }

    @Override
    public void onAudioFocusChange(int focusChange) {
        ImageButton resumeButton = playButtonsLayout.findViewById(R.id.resumeButton);
        switch (focusChange) {
            case AudioManager.AUDIOFOCUS_GAIN:
                if (mPlaybackDelayed || mResumeOnFocusGain) {
                    synchronized (mFocusLock) {
                        mPlaybackDelayed = false;
                        mResumeOnFocusGain = false;
                        if (MediaPlaybackService.getMediaController().isPlaying()) {
                            if (volumeSeekBar == -1) {
                                MediaPlaybackService.getMediaController().setVolume(1.0f);
                            } else {
                                MediaPlaybackService.getMediaController().setVolume(volumeSeekBar);
                            }
                        } else {
                            resumeMusic();
                            playButton = false;
                        }
                    }
                }
                break;
            case AudioManager.AUDIOFOCUS_LOSS:
                synchronized (mFocusLock) {
                    mResumeOnFocusGain = false;
                    mPlaybackDelayed = false;
                    MediaPlaybackService.getMediaController().pause();
                    resumeButton.setImageResource(R.drawable.ic_play);
                    playButton = false;
                }
                break;
            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                synchronized (mFocusLock) {
                    mResumeOnFocusGain = MediaPlaybackService.getMediaController().isPlaying();
                    mPlaybackDelayed = false;
                    MediaPlaybackService.getMediaController().setVolume(0.05f);
                }
                break;
        }
    }
}



