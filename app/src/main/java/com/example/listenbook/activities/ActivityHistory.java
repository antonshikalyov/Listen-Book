package com.example.listenbook.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

import com.example.listenbook.R;

public class ActivityHistory extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        ImageButton backToMainActivity = findViewById(R.id.back_history_button);
        backToMainActivity.setOnClickListener(v -> {
            Intent intent = new Intent(ActivityHistory.this, PlayTrackActivity.class);
            startActivity(intent);
        });
    }
}