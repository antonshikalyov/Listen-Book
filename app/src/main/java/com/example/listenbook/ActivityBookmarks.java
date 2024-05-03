package com.example.listenbook;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

public class ActivityBookmarks  extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bookmarks);

        ImageButton backToMainActivity = findViewById(R.id.back_bookmarks_button);
        backToMainActivity.setOnClickListener(v -> {
            Intent intent = new Intent(ActivityBookmarks.this, MainActivity.class);
            startActivity(intent);
        });
    }
}