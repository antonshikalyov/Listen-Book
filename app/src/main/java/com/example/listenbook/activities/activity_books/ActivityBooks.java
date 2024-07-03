package com.example.listenbook.activities.activity_books;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.documentfile.provider.DocumentFile;

import com.example.listenbook.R;
import com.example.listenbook.activities.PlayTrackActivity;
import com.example.listenbook.entities.AudioItem;
import com.example.listenbook.entities.Book;
import com.example.listenbook.services.DataBase;
import com.example.listenbook.services.Permission;
import com.example.listenbook.services.UserLearning;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Comparator;

public class ActivityBooks extends AppCompatActivity {
    private static final int PICK_FILE_REQUEST_CODE = 2;
    private BookAdapter adapter;
    private DataBase dataBase;
    Permission permission = new Permission();
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_library);

        SharedPreferences sharedPreferences = getSharedPreferences("settings", MODE_PRIVATE);
        editor = sharedPreferences.edit();

        int learningUser = sharedPreferences.getInt("LIBRARY_USER_LEARNING", 0);
        if (learningUser <= 0) {
            editor.putInt("LIBRARY_USER_LEARNING",  UserLearning.learningUserLibrary(this));
            editor.apply();
        }

        dataBase = new DataBase(this);
        ArrayList<Book> audioList = (ArrayList<Book>) dataBase.getAllBooks();

        GridView gridViewList = findViewById(R.id.gridView);
        adapter = new BookAdapter(this, R.layout.item_book, audioList);
        gridViewList.setAdapter(adapter);

        ImageButton backToMainActivity = findViewById(R.id.library_back_button);
        backToMainActivity.setOnClickListener(v -> {
            Intent intent = new Intent(ActivityBooks.this, PlayTrackActivity.class);
            startActivity(intent);
        });

        ImageButton addBook = findViewById(R.id.add_book);
        addBook.setOnClickListener(v -> {
            permission.checkPermissions(this);
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
            startActivityForResult(intent, PICK_FILE_REQUEST_CODE);
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 123) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted
            } else if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.READ_EXTERNAL_STORAGE)
                    && ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                Toast.makeText(this, "This permission is needed", Toast.LENGTH_LONG).show();
            } else {
                permission.showSettingsDialog();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_FILE_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            MediaMetadataRetriever retriever = new MediaMetadataRetriever();
            Uri singleUri = data.getData();
            DocumentFile pickedDir = DocumentFile.fromTreeUri(this, singleUri);

            String internalStorageRoot = String.valueOf(Environment.getExternalStorageDirectory());
            if (pickedDir != null && pickedDir.isDirectory()) {
                DocumentFile[] files = pickedDir.listFiles();
                if (!dataBase.isUriExists(pickedDir.getUri().toString())) {
                    ArrayList<AudioItem> list = new ArrayList<>();
                    for (DocumentFile file : files) {
                        String uri = file.getUri().getPathSegments().get(3);
                        retriever.setDataSource(this, file.getUri());
                        if (uri.contains("primary:")) {
                            uri = uri.replace("primary:", internalStorageRoot + "/");
                        } else {
                            String[] parts = uri.split(":");
                            uri = "/storage/" + parts[0] + "/" + parts[1];
                        }
                        list.add(new AudioItem(
                                Integer.parseInt(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_CD_TRACK_NUMBER)),
                                retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE), uri,
                                    Long.valueOf(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)),
                                                0L));
                    }

                    list.sort(Comparator.comparingInt(AudioItem::getId));
                    long bookDuration =0;
                    for (AudioItem item: list) {
                        item.positionInBook = bookDuration;
                        bookDuration += item.duration;
                    }

                    Gson gson = new Gson();
                    String chapters = gson.toJson(list);
                    System.out.println(chapters);
                    Book audioTrack = new Book(
                            retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM),
                            retriever.getEmbeddedPicture(),
                            bookDuration,
                            pickedDir.getUri().toString(),
                            chapters);
                    dataBase.insertBook(audioTrack);
                }
            }
            updateAdapter();
        }
    }


    public void updateAdapter() {
        ArrayList<Book> newList = (ArrayList<Book>) dataBase.getAllBooks();
        adapter.clear();
        adapter.addAll(newList);
        adapter.notifyDataSetChanged();
    }
}


