package com.example.listenbook;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import java.io.ByteArrayOutputStream;

public class SongActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_library);
    }

    public static void getInfoFromManifest(String uri, Activity activity) {
        String title = "";
        byte[] albumArt;
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        Intent intent = new Intent(activity, MainActivity.class);
        try {
            retriever.setDataSource(uri);
            title = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);
            albumArt = retriever.getEmbeddedPicture();
            if (title != null) {
                intent.putExtra("title", title);
            }
            if (albumArt != null) {
                Bitmap bitmap = BitmapFactory.decodeByteArray(albumArt, 0, albumArt.length);
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 50, stream); // Здесь 50 - качество сжатия (от 0 до 100)
                byte[] compressedAlbumArt = stream.toByteArray();
                intent.putExtra("albumArt", compressedAlbumArt);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (activity.getClass() != MainActivity.class) {
            activity.startActivity(intent);
        }
    }
}
