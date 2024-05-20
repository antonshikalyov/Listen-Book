package com.example.listenbook;

import android.content.Intent;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.widget.GridView;
import android.widget.ImageButton;
import androidx.appcompat.app.AppCompatActivity;
import androidx.documentfile.provider.DocumentFile;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.util.ArrayList;
import java.util.Comparator;


public class ActivityBooks extends AppCompatActivity {
    private static final int PICK_FILE_REQUEST_CODE = 2;

    Permission permission = new Permission();
    BookAdapter adapter;
    DataBase dataBase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_library);

        dataBase = new DataBase(this);
        ArrayList <AudioTrack> audioList = (ArrayList<AudioTrack>) dataBase.getAllAudioTracks();

        GridView gridViewList = findViewById(R.id.gridView);
        adapter = new BookAdapter(this, R.layout.item_book, audioList);
        gridViewList.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        ImageButton backToMainActivity = findViewById(R.id.library_back_button);
        backToMainActivity.setOnClickListener(v -> {
            Intent intent = new Intent(ActivityBooks.this, MainActivity.class);
            startActivity(intent);
        });

//        ImageButton addBook = findViewById(R.id.add_book);
//        addBook.setOnClickListener(v -> {
//            permission.setContext(this);
//            permission.CallPermission();
//            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
//            intent.setType("*/*");
//            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
//            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
//            startActivityForResult(intent, PICK_FILE_REQUEST_CODE);
//        });
        ImageButton addBook = findViewById(R.id.add_book);
        addBook.setOnClickListener(v -> {
            permission.setContext(this);
            permission.checkPermissions();
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
            startActivityForResult(intent, PICK_FILE_REQUEST_CODE);
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        DataBase dataBase = new DataBase(this);

        if (requestCode == PICK_FILE_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            adapter.notifyDataSetChanged();
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
                            list.add(new AudioItem(Integer.parseInt(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_CD_TRACK_NUMBER)), uri));
                    }

                    list.sort(Comparator.comparingInt(AudioItem::getId));
                    Gson gson = new Gson();
                    String jsonString = gson.toJson(list);

                    ArrayList<AudioItem> newList = gson.fromJson(jsonString, new TypeToken<ArrayList<AudioItem>>(){}.getType());
                    ArrayList<String> x = new ArrayList<>();
                    for (int i = 0; i < newList.size(); i++) {
                        x.add(newList.get(i).uri);
                    }

                    AudioTrack audioTrack = new AudioTrack(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM),
                            retriever.getEmbeddedPicture(),
                            pickedDir.getUri().toString(),
                            x.toString());
                    dataBase.insertAudioTrack(audioTrack);
                }
            }
        }
      updateAdapter();
    }
    public void updateAdapter() {
        if (dataBase.haveNotes()) {}
        adapter.clear();
        adapter.addAll(dataBase.getAllAudioTracks());
        adapter.notifyDataSetChanged();
    }
}

class AudioItem {
    int id;
    String uri;

    public AudioItem(int id, String uri) {
        this.id = id;
        this.uri = uri;
    }
    public int getId() {
        return id;
    }

}