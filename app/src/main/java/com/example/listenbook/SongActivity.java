package com.example.listenbook;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

public class SongActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_library);
    }

    public void getInfoFromManifest(Uri uri, Activity activity) {
        String title = "";
        String artist;
        String album;
        byte[] albumArt = new byte[0];
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        Intent intent = new Intent(activity, MainActivity.class);

        try {
            retriever.setDataSource(activity, uri);
            title = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);
            albumArt = retriever.getEmbeddedPicture();
            if (title != null) {
                intent.putExtra("title", title);
            }
            if (albumArt != null) {
                intent.putExtra("albumArt", albumArt);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        activity.startActivity(intent);
    }
}
