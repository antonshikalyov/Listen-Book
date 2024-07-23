package com.example.listenbook.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.example.listenbook.MainActivityAdapter;
import com.example.listenbook.R;
import com.example.listenbook.activities.play_track_activity.PlayTrackActivity;

public class MainActivity extends AppCompatActivity {
    private ImageView circle1;
    private ImageView circle2;
    private ImageView circle3;
    private ImageView circle4;
    private ImageView circle5;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SharedPreferences sharedPreferences = getSharedPreferences("settings", MODE_PRIVATE);
        editor = sharedPreferences.edit();

        circle1 = findViewById(R.id.circle1);
        circle2 = findViewById(R.id.circle2);
        circle3 = findViewById(R.id.circle3);
        circle4 = findViewById(R.id.circle4);
        circle5 = findViewById(R.id.circle5);

        ViewPager2 viewPager2 = findViewById(R.id.viewPager);
        viewPager2.setAdapter(new MainActivityAdapter());

        viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);

                switch (position) {
                    case 0:
                        defaultValues();
                        circle1.setImageResource(R.drawable.circle_select);
                        break;
                    case 1:
                        defaultValues();
                        circle2.setImageResource(R.drawable.circle_select);
                        break;
                    case 2:
                        defaultValues();
                        circle3.setImageResource(R.drawable.circle_select);
                        break;
                    case 3:
                        defaultValues();
                        circle4.setImageResource(R.drawable.circle_select);
                        break;
                    default:
                        defaultValues();
                        circle5.setImageResource(R.drawable.circle_select);
                        break;
                }
            }
        });
    }

    public void defaultValues() {
        circle1.setImageResource(R.drawable.circle_purple);
        circle2.setImageResource(R.drawable.circle_purple);
        circle3.setImageResource(R.drawable.circle_purple);
        circle4.setImageResource(R.drawable.circle_purple);
        circle5.setImageResource(R.drawable.circle_purple);
    }

    public void startApp(View view) {
        editor.putInt("ONBOARDING",  1);
        editor.apply();
        Intent intent = new Intent( MainActivity.this, PlayTrackActivity.class);
        startActivity(intent);
    }
}
