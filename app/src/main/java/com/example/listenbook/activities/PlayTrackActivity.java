package com.example.listenbook.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.os.LocaleListCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.example.listenbook.AnimationMoreButton;
import com.example.listenbook.R;
import com.example.listenbook.activities.activity_bookmarks.ActivityBookmarks;
import com.example.listenbook.activities.activity_books.ActivityBooks;
import com.example.listenbook.databinding.ActivityPlayTrackBinding;
import com.example.listenbook.entities.AudioItem;
import com.example.listenbook.services.MediaPlaybackService;
import com.example.listenbook.services.UserLearning;

import java.util.Locale;

public class PlayTrackActivity extends AppCompatActivity {

    // Used to load the 'listenbook' library on application startup.
    static {
        System.loadLibrary("listenbook");
    }
    SharedPreferences.Editor editor;
    private ActivityPlayTrackBinding binding;
    private LocaleListCompat appLocale;

    @SuppressLint({"CutPasteId", "ResourceAsColor"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPlayTrackBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        PlayPanelActivity.aboutSong(findViewById(R.id.full_song_information),
                findViewById(R.id.small_song_information),
                findViewById(R.id.playButtonsLayout));

        Intent serviceIntent = new Intent(this, MediaPlaybackService.class);
        startService(serviceIntent);

        PlayPanelActivity.seekBarVolume(findViewById(R.id.simpleSeekBarSound));

        SharedPreferences sharedPreferences = getSharedPreferences("settings", MODE_PRIVATE);
        editor = sharedPreferences.edit();

        if (sharedPreferences.getInt("ONBOARDING", 0) == 0) {
            Intent intent = new Intent( PlayTrackActivity.this, MainActivity.class);
            startActivity(intent);
        }


    PlayPanelActivity playPanelActivity = new PlayPanelActivity();
        if(playPanelActivity.playStatus())

    {
        ImageButton resumeButton = findViewById(R.id.resumeButton);
        resumeButton.setImageResource(R.drawable.ic_pause);
    }

    String savedLanguage = sharedPreferences.getString("LANGUAGE", "defaultLanguage");
    int learningUser = sharedPreferences.getInt("MAIN_USER_LEARNING", 0);

    ImageButton burgerButton = findViewById(R.id.burger_button);
    ImageButton toolbarBook = findViewById(R.id.toolbar_library);

    if (learningUser <= 0) {
        editor.putInt("MAIN_USER_LEARNING",  UserLearning.learningUserMain(this));
        editor.apply();
    }


    if(!savedLanguage.isEmpty()) {
        appLocale = LocaleListCompat.forLanguageTags(savedLanguage);
    } else {
        appLocale = LocaleListCompat.forLanguageTags(Locale.getDefault().getLanguage());
    }
    AppCompatDelegate.setApplicationLocales(appLocale);

    int theme = sharedPreferences.getInt("THEME", 1);
    if(theme ==1) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
    } else if(theme ==2) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
    } else {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
    }

    ImageButton moreButton = findViewById(R.id.more_button);
    moreButton.setOnClickListener(v -> {
        AnimationMoreButton animationMoreButton = new AnimationMoreButton();
        animationMoreButton.animationMoreAboutSong (findViewById(R.id.full_song_information),
                                                    findViewById(R.id.small_song_information),
                                                    findViewById(R.id.more_button_block));
    });

    ImageButton moreButtonPlay = findViewById(R.id.more_button_play);
    moreButtonPlay.setOnClickListener(v -> {
        AnimationMoreButton animationMoreButton = new AnimationMoreButton();
        animationMoreButton.animationMoreAboutPlay (findViewById(R.id.small_control_panel),
                                                    findViewById(R.id.more_button_play_block));
    });


    DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);
    ImageButton activityBookSettings = findViewById(R.id.speed_control_panel);


    LinearLayout sideBarLibrary = findViewById(R.id.sidebar_library);
    LinearLayout sideBarBookmarks = findViewById(R.id.sidebar_bookmarks);
    LinearLayout sideBarHistory = findViewById(R.id.sidebar_history);
    LinearLayout sideBarStatistics = findViewById(R.id.sidebar_statistics);
    LinearLayout sideBarSettings = findViewById(R.id.sidebar_settings);

    activityBookSettings.setOnClickListener(v -> {
        Intent intent = new Intent(PlayTrackActivity.this, ActivityBookSettings.class);
        startActivity(intent);
    });

    sideBarLibrary.setOnClickListener(v -> {
        Intent intent = new Intent(PlayTrackActivity.this, ActivityBooks.class);
        startActivity(intent);
    });

    sideBarBookmarks.setOnClickListener(v -> {
        Intent intent = new Intent(PlayTrackActivity.this, ActivityBookmarks.class);
        startActivity(intent);
    });

    sideBarHistory.setOnClickListener(v -> {
        Intent intent = new Intent(PlayTrackActivity.this, ActivityHistory.class);
        startActivity(intent);
    });

    sideBarStatistics.setOnClickListener(v -> {
        Intent intent = new Intent(PlayTrackActivity.this, ActivityStatistics.class);
        startActivity(intent);
    });

    sideBarSettings.setOnClickListener(v -> {
        Intent intent = new Intent(PlayTrackActivity.this, ActivitySettings.class);
        startActivity(intent);
    });

    toolbarBook.setOnClickListener(view -> {
        Intent intent = new Intent(PlayTrackActivity.this, ActivityBooks.class);
        startActivity(intent);
    });

    burgerButton.setOnClickListener(view -> {
        if (!drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.openDrawer(GravityCompat.START);
        } else {
            drawerLayout.closeDrawer(GravityCompat.START);
        }
    });

// Example of a call to a native method
//        TextView tv = binding.sampleText;
//        tv.setText(stringFromJNI());
    }
    /**
     * A native method that is implemented by the 'listenbook' native library,
     * which is packaged with this application.
     */
    public native String stringFromJNI();


    public void resumeMusic(View view) {
            PlayPanelActivity.resumeMusic();
    }

    public void nextChapter(View view) {
            MediaPlaybackService.mediaController.seekToNext();

    }
    public void previousChapter(View view) {
            MediaPlaybackService.mediaController.seekToPrevious();

    }


    public void addBookmark(View view) {
        if (MediaPlaybackService.getMediaController().getMediaItemCount() > 0) {
            PlayPanelActivity.addBookmark();
        }
    }

    public void smForward(View view) {
        SharedPreferences sharedPreferences = getSharedPreferences("settings", MODE_PRIVATE);
        long stepForwardSettings = sharedPreferences.getLong("STEP_REWIND_SETTINGS", 10000);

        // Получаем текущий и следующий элементы
        AudioItem currentAudioItem = PlayPanelActivity.chapters.get(MediaPlaybackService.getMediaController().getCurrentMediaItemIndex());
        long currentPosition = MediaPlaybackService.getMediaController().getCurrentPosition();

        // Вычисляем нужное глобальное время
        long neededTime = currentAudioItem.positionInBook + currentPosition + stepForwardSettings;

        // Находим нужную главу, используя бинарный поиск
        int neededChapterIndex = PlayPanelActivity.binarySearchChapter(neededTime);
        AudioItem neededAudioItem = PlayPanelActivity.chapters.get(neededChapterIndex);

        // Вычисляем новую позицию внутри найденной главы
        long newLocalTime = neededTime - neededAudioItem.positionInBook;

        // Устанавливаем новую позицию в плеере
        MediaPlaybackService.getMediaController().seekTo(neededChapterIndex, newLocalTime);
    }

    public void smBackward(View view) {
        SharedPreferences sharedPreferences = getSharedPreferences("settings", MODE_PRIVATE);
        long stepBackwardSettings = sharedPreferences.getLong("STEP_REWIND_SETTINGS", 10000);
        Log.i("ячсясч", String.valueOf(stepBackwardSettings));

        // Получаем текущий элемент
        AudioItem currentAudioItem = PlayPanelActivity.chapters.get(MediaPlaybackService.getMediaController().getCurrentMediaItemIndex());
        long currentPosition = MediaPlaybackService.getMediaController().getCurrentPosition();

        // Вычисляем нужное глобальное время
        long neededTime = currentAudioItem.positionInBook + currentPosition - stepBackwardSettings;

        // Проверяем, что нужное время не выходит за пределы первой главы
        if (neededTime < 0) {
            neededTime = 0;
        }

        // Находим нужную главу, используя бинарный поиск
        int neededChapterIndex = PlayPanelActivity.binarySearchChapter(neededTime);
        AudioItem neededAudioItem = PlayPanelActivity.chapters.get(neededChapterIndex);

        // Вычисляем новую позицию внутри найденной главы
        long newLocalTime = neededTime - neededAudioItem.positionInBook;

        // Устанавливаем новую позицию в плеере
        MediaPlaybackService.getMediaController().seekTo(neededChapterIndex, newLocalTime);

    }
}
