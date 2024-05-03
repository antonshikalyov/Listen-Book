package com.example.listenbook;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;

public class SongActivity extends AppCompatActivity {
    Uri uri;

    MediaPlayer mp = new MediaPlayer();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_song);

        ImageButton run = findViewById(R.id.runButton);
        run.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mp.isPlaying()) {
                    ImageButton imageButton = findViewById(R.id.runButton);
                    imageButton.setImageResource(android.R.drawable.ic_media_play);
                    mp.pause();
                }
                else if (!mp.isPlaying()) {
                    ImageButton imageButton = findViewById(R.id.runButton);
                    imageButton.setImageResource(android.R.drawable.ic_media_pause);
                    mp.start();
                }
            }
        });

        Intent intent = getIntent();
        // Получаем Uri из Intent
        uri = intent.getData();

        MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();
        mediaMetadataRetriever.setDataSource(SongActivity.this, uri);
        String title = mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_CAPTURE_FRAMERATE);
        String bitrate = mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_BITRATE);
        String album = mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM);
//
        Log.i(TAG, "MY LOG: " + mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_CAPTURE_FRAMERATE));
        Log.i(TAG, "MY LOG: " + mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_HAS_IMAGE));
        Log.i(TAG, "MY LOG: " + mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.OPTION_NEXT_SYNC));
        Log.i(TAG, "MY LOG: " + mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUMARTIST));
        Log.i(TAG, "MY LOG: " + mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_BITS_PER_SAMPLE));
        Log.i(TAG, "MY LOG: " + mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_CD_TRACK_NUMBER));
        Log.i(TAG, "MY LOG: " + mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_FRAME_COUNT));

        byte[] albumArtBytes = mediaMetadataRetriever.getEmbeddedPicture();
        if (albumArtBytes != null) {
            try {
                mediaMetadataRetriever.release();
                Bitmap albumArt = BitmapFactory.decodeByteArray(albumArtBytes, 0, albumArtBytes.length);

                ImageView imageView = findViewById(R.id.imageView);

                if (albumArt != null) {
                    imageView.setImageBitmap(albumArt);
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        TextView tv = findViewById(R.id.sample_text);
        tv.setText(title);

        try {
            mp.setDataSource(getApplicationContext(), uri);
            mp.prepare();
            mp.start();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
