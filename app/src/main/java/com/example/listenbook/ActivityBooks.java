package com.example.listenbook;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

public class ActivityBooks extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_library);

        ImageButton backToMainActivity = findViewById(R.id.library_back_button);
        backToMainActivity.setOnClickListener(v -> {
            Intent intent = new Intent(ActivityBooks.this, MainActivity.class);
            startActivity(intent);
        });
    }
}
