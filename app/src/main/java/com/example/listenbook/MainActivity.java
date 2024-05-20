package com.example.listenbook;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.os.LocaleListCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.listenbook.databinding.ActivityMainBinding;

import java.util.Locale;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    // Used to load the 'listenbook' library on application startup.
    static {
        System.loadLibrary("listenbook");
    }

    private ActivityMainBinding binding;
    private LocaleListCompat appLocale;

    @SuppressLint("CutPasteId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        PlayPanelActivity.aboutSong(findViewById(R.id.full_song_information),
                               findViewById(R.id.small_song_information),
                               findViewById(R.id.playButtonsLayout));
        seekProgress(null);

        PlayPanelActivity.seekBarVolume(findViewById(R.id.simpleSeekBarSound));
        Bundle arguments = getIntent().getExtras();
        if (arguments != null) {
            String title = Objects.requireNonNull(arguments.get("title")).toString();
            byte[] albumArt = (byte[]) getIntent().getExtras().get("albumArt");
            if (albumArt != null) {
                ImageView songImage = findViewById(R.id.imageView);
                ImageView smallSongImage = findViewById(R.id.small_imageView);
                Bitmap bitmap = BitmapFactory.decodeByteArray(albumArt, 0, albumArt.length);
                smallSongImage.setImageBitmap(bitmap);
                songImage.setImageBitmap(bitmap);
            }
            if (!title.isEmpty()) {
                TextView songName = findViewById(R.id.sample_text);
                TextView smallSongName = findViewById(R.id.small_sample_text);
                songName.setText(title);
                smallSongName.setText(title);
            }
    }

    PlayPanelActivity playPanelActivity = new PlayPanelActivity();
        if(playPanelActivity.playStatus())

    {
        ImageButton resumeButton = findViewById(R.id.resumeButton);
        resumeButton.setImageResource(R.drawable.ic_pause);
    }

    SharedPreferences sharedPreferences = getSharedPreferences("settings", MODE_PRIVATE);
    String savedLanguage = sharedPreferences.getString("LANGUAGE", "defaultLanguage");

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

//        PlayPanelActivity.mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
//            @Override
//            public void onCompletion(MediaPlayer mp) {
//                Log.i("FINISH", "FFFFFFFF");
//                PlayPanelActivity.nextChapter();
//            }
//        });

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
    ImageButton burgerButton = findViewById(R.id.burger_button);

    LinearLayout sideBarLibrary = findViewById(R.id.sidebar_library);
    LinearLayout sideBarBookmarks = findViewById(R.id.sidebar_bookmarks);
    LinearLayout sideBarHistory = findViewById(R.id.sidebar_history);
    LinearLayout sideBarStatistics = findViewById(R.id.sidebar_statistics);
    LinearLayout sideBarSettings = findViewById(R.id.sidebar_settings);

    sideBarLibrary.setOnClickListener(v -> {
        Intent intent = new Intent(MainActivity.this, ActivityBooks.class);
        startActivity(intent);
    });

    sideBarBookmarks.setOnClickListener(v -> {
        Intent intent = new Intent(MainActivity.this, ActivityBookmarks.class);
        startActivity(intent);
    });

    sideBarHistory.setOnClickListener(v -> {
        Intent intent = new Intent(MainActivity.this, ActivityHistory.class);
        startActivity(intent);
    });

    sideBarStatistics.setOnClickListener(v -> {
        Intent intent = new Intent(MainActivity.this, ActivityStatistics.class);
        startActivity(intent);
    });

    sideBarSettings.setOnClickListener(v -> {
        Intent intent = new Intent(MainActivity.this, ActivitySettings.class);
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
        PlayPanelActivity.nextChapter();
    }
    public void previousChapter(View view) {
        PlayPanelActivity.previousChapter();
    }


    public void seekProgress(View view) {
        SeekBar seekBar = findViewById(R.id.simpleSeekBar);
    }

}
