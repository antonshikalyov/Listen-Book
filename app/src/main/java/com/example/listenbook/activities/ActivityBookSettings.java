package com.example.listenbook.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.listenbook.R;
import com.example.listenbook.activities.play_track_activity.PlayPanelActivity;
import com.example.listenbook.activities.play_track_activity.PlayTrackActivity;
import com.example.listenbook.services.DataBase;
import com.example.listenbook.services.MediaPlaybackService;

public class ActivityBookSettings extends AppCompatActivity {

    private SeekBar customSeekBar;
    TextView indicateSpeed;
    private float value;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_settings);
        DataBase dataBase = new DataBase(this);

        customSeekBar = findViewById(R.id.audio_speed_seekbar);
        indicateSpeed = findViewById(R.id.indicate_speed);
        value = dataBase.getBookSpeedByForeignKey(PlayPanelActivity.currentBook.id);
        float oldSpeed = value;
        indicateSpeed.setText(String.format("( %.2f x )", value));
        customSeekBar.setProgress((int) value * 100);

        ImageButton backToMainActivity = findViewById(R.id.back_audio_settings_button);
        backToMainActivity.setOnClickListener(v -> {
            MediaPlaybackService.getMediaController().setPlaybackSpeed(oldSpeed);
            Intent intent = new Intent(this, PlayTrackActivity.class);
            startActivity(intent);
        });

        ImageButton saveSettings = findViewById(R.id.save_speed_book_button);
        saveSettings.setOnClickListener(v -> {
            if (dataBase.getBookSettingsIdByBookId(PlayPanelActivity.currentBook.id) > -1) {
                dataBase.updateBookSettingsSpeed(PlayPanelActivity.currentBook.id, value);
            } else {
                dataBase.insertBookSettings(PlayPanelActivity.currentBook.id, value);
            }
            Intent intent = new Intent(this, PlayTrackActivity.class);
            startActivity(intent);
        });


        customSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                value = (float) (progress / 100.0);
                Log.i("SeekBar", String.valueOf((float) (progress / 100.0)));
                MediaPlaybackService.getMediaController().setPlaybackSpeed(value);
                indicateSpeed.setText(String.format("( %.2f x )", value));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // Необязательно: можно добавить код, когда пользователь начинает взаимодействие с SeekBar
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // Необязательно: можно добавить код, когда пользователь заканчивает взаимодействие с SeekBar
            }
        });
    }

    public void increaseSpeed(View view) {
        customSeekBar.setProgress(customSeekBar.getProgress() + 5);
    }

    public void decreaseSpeed(View view) {
        customSeekBar.setProgress(customSeekBar.getProgress()  - 5);
    }
}