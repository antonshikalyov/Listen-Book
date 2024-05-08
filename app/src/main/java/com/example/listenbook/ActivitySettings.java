package com.example.listenbook;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.os.LocaleListCompat;


public class ActivitySettings extends AppCompatActivity {
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    DialogLanguage dialogLanguage = new DialogLanguage();
    DialogThemes dialogThemes = new DialogThemes();
    Resources resources;
    Button themeButtom;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        sharedPreferences = getSharedPreferences("settings", MODE_PRIVATE);
        editor = sharedPreferences.edit();

        int theme = sharedPreferences.getInt("THEME", 1);

        themeButtom = findViewById(R.id.theme_button);
        if (theme == 1) {
            themeButtom.setText(R.string.light_theme);
        } else if (theme == 2){
            themeButtom.setText(R.string.dark_theme);
        } else {
            themeButtom.setText(R.string.auto_theme);
        }

        ImageButton backToMainActivity = findViewById(R.id.back_settings_button);
        backToMainActivity.setOnClickListener(v -> {
            Intent intent = new Intent(ActivitySettings.this, MainActivity.class);
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
        themeButtom.setText(R.string.dark_theme);
        editor.putInt("THEME", 2);
        editor.apply();
        dialogThemes.dismiss();
    }

    public void setLightTheme(View view) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        editor = sharedPreferences.edit();
        themeButtom.setText(R.string.light_theme);
        editor.putInt("THEME", 1);
        editor.apply();
        dialogThemes.dismiss();
    }

    public void setAutoTheme (View view){
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
        editor = sharedPreferences.edit();
        themeButtom.setText(R.string.auto_theme);
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

}