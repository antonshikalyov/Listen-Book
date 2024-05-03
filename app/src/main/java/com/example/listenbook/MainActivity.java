package com.example.listenbook;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.ActivityCompat;
import androidx.core.os.LocaleListCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.listenbook.databinding.ActivityMainBinding;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private static final int READ_AND_WRITE_STORAGE = 1;
    // Used to load the 'listenbook' library on application startup.
    static {
        System.loadLibrary("listenbook");
    }

    private ActivityMainBinding binding;
    private LocaleListCompat appLocale;

//    Intent i = new Intent(this, SongActivity.class);
//    startActivity(i)

    MyPermission permission = new MyPermission();

    @SuppressLint("CutPasteId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        SharedPreferences sharedPreferences = getSharedPreferences("settings", MODE_PRIVATE);
        String savedLanguage = sharedPreferences.getString("LANGUAGE", "defaultLanguage");

        if (!savedLanguage.isEmpty()) {
            appLocale = LocaleListCompat.forLanguageTags(savedLanguage);
        } else {
            appLocale = LocaleListCompat.forLanguageTags(Locale.getDefault().getLanguage());
        }
        AppCompatDelegate.setApplicationLocales(appLocale);
        int theme = sharedPreferences.getInt("THEME", 1);

        if (theme == 1) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        } else if (theme == 2) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
        }
//        permission.setContext(this);
//        permission.CallPermission();


//        try {
//            if (permission.CallPermission() == 2) {
//                ActivityCompat.requestPermissions(this,new String[] {
//                        Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE},
//                        READ_AND_WRITE_STORAGE);
//            }
//        } catch (IllegalAccessException e) {
//            throw new RuntimeException(e);
//        } catch (InstantiationException e) {
//            throw new RuntimeException(e);
//        }

        ImageButton moreButton = findViewById(R.id.more_button);
        moreButton.setOnClickListener(v -> {
            permission.setContext(this);
            permission.CallPermission();
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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == READ_AND_WRITE_STORAGE) {
            // Проверяем, предоставлено ли разрешение пользователем
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Разрешение предоставлено, выполните необходимые действия
                Toast.makeText(this, "11111 GRANTED", Toast.LENGTH_LONG).show();

            }
            else if ((ActivityCompat.shouldShowRequestPermissionRationale(
                    this, Manifest.permission.READ_EXTERNAL_STORAGE)
                    && ActivityCompat.shouldShowRequestPermissionRationale(
                     this, Manifest.permission.WRITE_EXTERNAL_STORAGE))){
                Toast.makeText(this, "This permishen need", Toast.LENGTH_LONG).show();
          }
            else {
                permission.showSettingsDialog();
            }

        }
    }

    // Метод показа диалога для перехода в настройки приложения


    /**
     * A native method that is implemented by the 'listenbook' native library,
     * which is packaged with this application.
     */
    public native String stringFromJNI();
}
