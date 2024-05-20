package com.example.listenbook;

import static androidx.core.app.ActivityCompat.startActivityForResult;
import static androidx.core.content.ContentProviderCompat.requireContext;

import static com.google.android.material.internal.ContextUtils.getActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.Objects;

public class BookAdapter extends ArrayAdapter {
    private final LayoutInflater inflater;
    private final int resource;
    private static final int PICK_FILE_REQUEST_CODE = 2;

    private final ArrayList<AudioTrack> audioTracks;
    private Context context;
    ActivityBooks activityBooks = new ActivityBooks();

    public BookAdapter(@NonNull Context context, int resource, @NonNull ArrayList<AudioTrack> audioTracks) {
        super(context, resource, audioTracks);
        this.context = context;
        this.audioTracks = audioTracks;
        this.resource = resource;
        this.inflater = LayoutInflater.from(context);

    }

    @NonNull
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder viewHolder;

        if(convertView==null){
            convertView = inflater.inflate(this.resource, parent, false);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        }
        else{
            viewHolder = (ViewHolder) convertView.getTag();
        }
        final AudioTrack audioTrack = audioTracks.get(position);
        viewHolder.textView.setText(audioTrack.title);
        byte[] imageBytes = audioTrack.image;
        Bitmap bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
        viewHolder.imageView.setImageBitmap(bitmap);

// DELETE BOOK
        viewHolder.imageButton.setOnClickListener(view -> {
            DataBase dataBase = new DataBase(this.getContext());
            dataBase.onDelete(Integer.parseInt(audioTrack.id));
            activityBooks.updateAdapter();
        });

// RUN BOOK
        viewHolder.imageView.setOnClickListener(v -> {
            Log.i("chapters", audioTrack.chapters);
            Log.i("directory_uri", audioTrack.directory_uri);
            Log.i("title", audioTrack.title);

            String chapters = audioTrack.chapters.replaceAll("[\"\\[\\]]", "");
            String [] c = chapters.split(", ");
            int i = 0;
            Log.i("chapters", audioTrack.chapters);
            Log.i("chapters", String.valueOf(chapters.length()));
            for (String x: c) {
                i++;
                Log.i("chapter", String.valueOf(i));
            }


//            String stringWithoutSlashes =  audioTrack.chapters.replaceAll("\\\\", "");
//            System.out.println(json);
//            Type type = new TypeToken<Map<Integer, String>>(){}.getType();
//            Map<Integer, String> map = gson.fromJson(stringWithoutSlashes, type);
//
//            for (Map.Entry<Integer, String> entry : map.entrySet()) {
//                System.out.println(entry.getKey() + " : " + entry.getValue());
//            }
//            ArrayList<AudioItem> newList = gson.fromJson(stringWithoutSlashes, new TypeToken<ArrayList<AudioItem>>(){}.getType());
//
//            for (int i = 0; i < newList.size(); i++) {
//                Log.i("id", String.valueOf(newList.get(i).id));
//                Log.i("uri", newList.get(i).uri);
//            }

//            MediaMetadataRetriever retriever = new MediaMetadataRetriever();
//            try {
//                retriever.setDataSource(newList.get(0).uri);
//                String title = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);
//                byte[] albumArt = retriever.getEmbeddedPicture();
//                if (title != null) {
//                    Log.i("ttttt", title);
//                }
//                if (albumArt != null) {
//                    Log.i("aaaaaa", "aaaaaa");
//                }
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
            PlayPanelActivity.currentChapter = new AudioItem(0, c[0]);
            PlayPanelActivity.chapters = c;
            PlayPanelActivity.playMusic(c[0]);
            SongActivity.getInfoFromManifest(c[0], (Activity) context);
        });
        return convertView;
    }





    private static class ViewHolder {
        final ImageView imageView;
        final ImageButton imageButton;
        final TextView textView;
        ViewHolder(View view){
            imageView = view.findViewById(R.id.card_image);
            imageButton = view.findViewById(R.id.card_imageBtn);
            textView = view.findViewById(R.id.card_text_below);
        }
    }
}


