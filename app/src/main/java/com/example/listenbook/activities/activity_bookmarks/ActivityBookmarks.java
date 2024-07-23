package com.example.listenbook.activities.activity_bookmarks;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.listenbook.R;
import com.example.listenbook.activities.play_track_activity.PlayTrackActivity;
import com.example.listenbook.services.DataBase;

import java.util.ArrayList;

public class ActivityBookmarks extends AppCompatActivity {
    BookmarkAdapter bookmarkAdapter;
    private DataBase dataBase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bookmarks);

        dataBase = new DataBase(this);
        ArrayList<Integer> uniqueBookmarks;
        uniqueBookmarks = dataBase.getUniqueBookmarksForeigenKeys();
        LinearLayout description_bookmarks = findViewById(R.id.description_bookmarks);
        if (!uniqueBookmarks.isEmpty()) {
            ListView listView = findViewById(R.id.gridView1);
            description_bookmarks.setVisibility(View.GONE);
            bookmarkAdapter = new BookmarkAdapter(this, R.layout.item_bookmark, uniqueBookmarks);
            listView.setAdapter(bookmarkAdapter);
        }

        ImageButton backToMainActivity = findViewById(R.id.back_bookmarks_button);
        backToMainActivity.setOnClickListener(v -> {
            Intent intent = new Intent(ActivityBookmarks.this, PlayTrackActivity.class);
            startActivity(intent);
        });
    }


}