package com.example.listenbook;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

public class ActivityHistory extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        ImageButton backToMainActivity = findViewById(R.id.back_history_button);
        backToMainActivity.setOnClickListener(v -> {
            Intent intent = new Intent(ActivityHistory.this, MainActivity.class);
            startActivity(intent);
        });
    }
}