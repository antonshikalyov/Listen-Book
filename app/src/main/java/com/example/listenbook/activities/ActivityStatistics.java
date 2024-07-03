package com.example.listenbook.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;

import com.example.listenbook.R;

public class ActivityStatistics extends AppCompatActivity {
    ImageView imageView;
    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);


//        LinearLayout linearLayout = findViewById(R.id.progressBarsContainer);
//        for (int i = 0; i < 30; i++) {
//            // Отримати LayoutInflater
//            LayoutInflater inflater = LayoutInflater.from(this);
//
//            // Завантажити макет прогрес бара з ресурсів
//            View progressBarLayout = inflater.inflate(R.layout.xxx, null);
//
//            // Отримати прогрес бар з завантаженого макету
//            ProgressBar progressBar = progressBarLayout.findViewById(R.id.xxxx);
//
//            // Встановити необхідні параметри прогрес бара
//            progressBar.setId(i);
//            progressBar.setMax(100);
//            progressBar.setProgress(i * 4);
//            progressBar.setLayoutParams(new LinearLayout.LayoutParams(5, 100)); // Встановлення ширини і висоти
//
//            // Додати прогрес бар до батьківського контейнера
//            linearLayout.addView(progressBar);
//        }


        LinearLayout linearLayout = findViewById(R.id.progressBarsContainerRed);

        for (int i = 0; i < 30; i++) {
            ProgressBar progressBar = new ProgressBar(this, null, android.R.attr.progressBarStyleHorizontal); // Создаем новый ProgressBar
            progressBar.setId(View.generateViewId()); // Устанавливаем уникальный ID для ProgressBar
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 7); // Создаем параметры Layout
            params.setMargins(0, 0, 3, 0); // Устанавливаем отступы (лево, верх, право, низ)
            progressBar.setLayoutParams(params); // Устанавливаем параметры Layout
            progressBar.setMax(100); // Устанавливаем максимальное значение прогресса
            progressBar.setProgress(i*3); // Устанавливаем текущее значение прогресса
            progressBar.setProgressDrawable(getResources().getDrawable(R.drawable.progress_bar_red)); // Устанавливаем drawable для отображения прогресса

            linearLayout.addView(progressBar); // Добавляем ProgressBar в LinearLayout
        }

        LinearLayout linearLayoutBlue = findViewById(R.id.progressBarsContainerBlue);

        for (int i = 0; i < 30; i++) {
            ProgressBar progressBar = new ProgressBar(this, null, android.R.attr.progressBarStyleHorizontal); // Создаем новый ProgressBar
            progressBar.setId(View.generateViewId()); // Устанавливаем уникальный ID для ProgressBar
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 7); // Создаем параметры Layout
            params.setMargins(0, 0, 3, 0); // Устанавливаем отступы (лево, верх, право, низ)
            progressBar.setLayoutParams(params); // Устанавливаем параметры Layout
            progressBar.setMax(100); // Устанавливаем максимальное значение прогресса
            progressBar.setProgress(100-i*3); // Устанавливаем текущее значение прогресса
            progressBar.setProgressDrawable(getResources().getDrawable(R.drawable.verticalprogressbar_blue)); // Устанавливаем drawable для отображения прогресса

            linearLayoutBlue.addView(progressBar); // Добавляем ProgressBar в LinearLayout
        }


        ImageButton backToMainActivity = findViewById(R.id.back_statistics_button);
        backToMainActivity.setOnClickListener(v -> {
            Intent intent = new Intent(ActivityStatistics.this, PlayTrackActivity.class);
            startActivity(intent);
        });
    }
}
