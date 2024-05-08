package com.example.listenbook;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.Manifest;
import android.content.ClipData;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import java.io.File;
import java.io.IOException;

public class ActivityBooks extends AppCompatActivity {
    private static final int READ_AND_WRITE_STORAGE = 1;
    private static final int PICK_FILE_REQUEST_CODE = 2;
    MyPermission permission = new MyPermission();
    MediaPlayer mp = new MediaPlayer();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_library);

        ImageButton backToMainActivity = findViewById(R.id.library_back_button);
        backToMainActivity.setOnClickListener(v -> {
            Intent intent = new Intent(ActivityBooks.this, MainActivity.class);
            startActivity(intent);
        });

        ImageButton addBook = findViewById(R.id.add_book);
        addBook.setOnClickListener(v -> {
            permission.setContext(this);
            permission.CallPermission();
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.setType("*/*");
            startActivityForResult(intent, PICK_FILE_REQUEST_CODE);
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Uri singleUri = data.getData();

//
//        String title = "";
//        String artist;
//        String album;
//        byte[] albumArt = new byte[0];
//        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
//        Intent intent = new Intent(ActivityBooks.this, MainActivity.class);
//
//        try {
//            retriever.setDataSource(getApplicationContext(), singleUri);
//            title = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);
//            albumArt = retriever.getEmbeddedPicture();
//            if (title != null) {
//                intent.putExtra("albumArt", albumArt);
//                intent.putExtra("title", title);
////                Log.i(TAG, "Title: " + title);
////                Toast.makeText(this, "Title: " + title, Toast.LENGTH_LONG).show();
//            } else {
//                Log.i(TAG, "Title: Unknown");
//                Toast.makeText(this, "Title: Unknown", Toast.LENGTH_LONG).show();
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
        if (requestCode == PICK_FILE_REQUEST_CODE && resultCode == RESULT_OK) {

            SongActivity songActivity = new SongActivity();
            MainActivity b = new MainActivity();
            songActivity.getInfoFromManifest(singleUri, this);
            PlayPanelActivity x = new PlayPanelActivity();
            x.playMusic(singleUri, this);

//                intent.putExtra("albumArt", albumArt);
//                intent.putExtra("title", title);
//                Toast.makeText(this, title , Toast.LENGTH_LONG).show();
//
//                startActivity(intent);
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == READ_AND_WRITE_STORAGE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            }
            else if ((ActivityCompat.shouldShowRequestPermissionRationale(
                    this, android.Manifest.permission.READ_EXTERNAL_STORAGE)
                    && ActivityCompat.shouldShowRequestPermissionRationale(
                    this, Manifest.permission.WRITE_EXTERNAL_STORAGE))){
                Toast.makeText(this, "This permishen need", Toast.LENGTH_LONG).show();
            }
            else {
                permission.showSettingsDialog();
            }

        }
    }
}
