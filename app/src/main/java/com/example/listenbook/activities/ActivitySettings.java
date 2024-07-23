package com.example.listenbook.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.os.LocaleListCompat;

import com.example.listenbook.R;
import com.example.listenbook.activities.play_track_activity.PlayTrackActivity;
import com.example.listenbook.dialogs.DialogLanguage;
import com.example.listenbook.dialogs.DialogThemes;


public class ActivitySettings extends AppCompatActivity {
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    DialogLanguage dialogLanguage = new DialogLanguage();
    DialogThemes dialogThemes = new DialogThemes();
    Button themeButton;
    Button stepForward;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        sharedPreferences = getSharedPreferences("settings", MODE_PRIVATE);
        editor = sharedPreferences.edit();

        int theme = sharedPreferences.getInt("THEME", 1);
        long stepForwardSettings = sharedPreferences.getLong("STEP_FORWARD_SETTINGS", 10000);


        themeButton = findViewById(R.id.theme_button);
        if (theme == 1) {
            themeButton.setText(R.string.light_theme);
        } else if (theme == 2){
            themeButton.setText(R.string.dark_theme);
        } else {
            themeButton.setText(R.string.auto_theme);
        }

        stepForward = findViewById(R.id.step_forward_button);
        stepForward.setText((int) (stepForwardSettings / 1000) + " sec.");

        ImageButton backToMainActivity = findViewById(R.id.back_settings_button);
        backToMainActivity.setOnClickListener(v -> {
            Intent intent = new Intent(ActivitySettings.this, PlayTrackActivity.class);
            startActivity(intent);
        });
    }

    public void showDialogLanguage (View view){
        dialogLanguage.show(getSupportFragmentManager(), "custom");
    }
    public void showDialogTheme(View view) {
        dialogThemes.show(getSupportFragmentManager(), "custom");
    }

    public void setUa (View view){
        LocaleListCompat appLocale = LocaleListCompat.forLanguageTags("uk");
        AppCompatDelegate.setApplicationLocales(appLocale);
        editor.putString("LANGUAGE", "uk");
        editor.apply();
        dialogLanguage.dismiss();
    }

    public void setRu (View view){
        LocaleListCompat appLocale = LocaleListCompat.forLanguageTags("ru");
        AppCompatDelegate.setApplicationLocales(appLocale);
        editor.putString("LANGUAGE", "ru");
        editor.apply();
        dialogLanguage.dismiss();
    }

    public void setDarkTheme(View view) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        editor = sharedPreferences.edit();
        themeButton.setText(R.string.dark_theme);
        editor.putInt("THEME", 2);
        editor.apply();
        dialogThemes.dismiss();
    }

    public void setLightTheme(View view) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        editor = sharedPreferences.edit();
        themeButton.setText(R.string.light_theme);
        editor.putInt("THEME", 1);
        editor.apply();
        dialogThemes.dismiss();
    }

    public void setAutoTheme (View view){
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
        editor = sharedPreferences.edit();
        themeButton.setText(R.string.auto_theme);
        editor.putInt("THEME", 3);
        editor.apply();
        dialogThemes.dismiss();
    }

    public void exitDialogLanguages(View view) {
        dialogLanguage.dismiss();
    }

    public void exitDialogThemes(View view) {
        dialogThemes.dismiss();
    }

    @SuppressLint({"MissingInflatedId", "SetTextI18n"})
    public void showDialogStepForward(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.NoBackgroundDialog);

        LayoutInflater inflater = LayoutInflater.from(this);
        final View customLayout = inflater.inflate(R.layout.dialog_step_forward, null);
        builder.setView(customLayout);
        AlertDialog dialog = builder.create();

        customLayout.findViewById(R.id.step_forward_5sec).setOnClickListener(v -> {
            editor = sharedPreferences.edit();
            themeButton.setText(R.string.dark_theme);
            editor.putLong("STEP_REWIND_SETTINGS", 5000);
            stepForward.setText("5 sec.");
            editor.apply();
            dialog.dismiss();
        });

        customLayout.findViewById(R.id.step_forward_10sec).setOnClickListener(v -> {
            editor = sharedPreferences.edit();
            themeButton.setText(R.string.dark_theme);
            editor.putLong("STEP_REWIND_SETTINGS", 10000);
            stepForward.setText("10 sec.");
            editor.apply();
            dialog.dismiss();
        });

        customLayout.findViewById(R.id.step_forward_15sec).setOnClickListener(v -> {
            editor = sharedPreferences.edit();
            themeButton.setText(R.string.dark_theme);
            editor.putLong("STEP_REWIND_SETTINGS", 15000);
            stepForward.setText("15 sec.");
            editor.apply();
            dialog.dismiss();
        });

        customLayout.findViewById(R.id.step_forward_30sec).setOnClickListener(v -> {
            editor = sharedPreferences.edit();
            themeButton.setText(R.string.dark_theme);
            editor.putLong("STEP_REWIND_SETTINGS", 30000);
            stepForward.setText("30 sec.");
            editor.apply();
            dialog.dismiss();
        });

        customLayout.findViewById(R.id.step_forward_60sec).setOnClickListener(v -> {
            editor = sharedPreferences.edit();
            themeButton.setText(R.string.dark_theme);
            editor.putLong("STEP_REWIND_SETTINGS", 60000);
            stepForward.setText("60 sec.");
            editor.apply();
            dialog.dismiss();
        });

        customLayout.findViewById(R.id.step_forward_120sec).setOnClickListener(v -> {
            editor = sharedPreferences.edit();
            themeButton.setText(R.string.dark_theme);
            editor.putLong("STEP_REWIND_SETTINGS", 120000);
            stepForward.setText("120 sec.");
            editor.apply();
            dialog.dismiss();
        });

        customLayout.findViewById(R.id.step_forward_300sec).setOnClickListener(v -> {
            editor = sharedPreferences.edit();
            themeButton.setText(R.string.dark_theme);
            editor.putLong("STEP_REWIND_SETTINGS", 300000);
            stepForward.setText("300 sec.");
            editor.apply();
            dialog.dismiss();
        });

        customLayout.findViewById(R.id.step_forward_600sec).setOnClickListener(v -> {
            editor = sharedPreferences.edit();
            themeButton.setText(R.string.dark_theme);
            editor.putLong("STEP_REWIND_SETTINGS", 600000);
            stepForward.setText("600 sec.");
            editor.apply();
            dialog.dismiss();
        });

        customLayout.findViewById(R.id.dialog_cancel).setOnClickListener(v -> {
            dialog.dismiss();
        });


        customLayout.findViewById(R.id.dialog_save).setOnClickListener(v -> {
            dialog.dismiss();
        });

        dialog.show();
    }
}