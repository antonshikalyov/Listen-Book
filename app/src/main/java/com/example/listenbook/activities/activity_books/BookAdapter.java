package com.example.listenbook.activities.activity_books;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.listenbook.R;
import com.example.listenbook.activities.PlayPanelActivity;
import com.example.listenbook.activities.PlayTrackActivity;
import com.example.listenbook.entities.AudioItem;
import com.example.listenbook.entities.Book;
import com.example.listenbook.services.DataBase;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class BookAdapter extends ArrayAdapter<Book> {
    private final LayoutInflater inflater;
    private final int resource;
    private final ArrayList<Book> audioTracks;
    private static Context context;

    public BookAdapter(@NonNull Context context, int resource, @NonNull ArrayList<Book> audioTracks) {
        super(context, resource, audioTracks);
        this.context = context;
        this.audioTracks = audioTracks;
        this.resource = resource;
        this.inflater = LayoutInflater.from(context);
    }

    @NonNull
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder viewHolder;

        if (convertView == null) {
            convertView = inflater.inflate(this.resource, parent, false);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        final Book audioTrack = audioTracks.get(position);
        viewHolder.textView.setText(audioTrack.title);
        byte[] imageBytes = audioTrack.image;

        if (imageBytes != null && imageBytes.length != 0) {
            Bitmap bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
            viewHolder.imageView.setImageBitmap(bitmap);
        }

        // DELETE BOOK
        viewHolder.imageButton.setOnClickListener(view -> {
            DataBase dataBase = new DataBase(this.getContext());
            dataBase.onDeleteBook(audioTrack.id);
            remove(audioTrack);
            notifyDataSetChanged();
        });

        // RUN BOOK
        viewHolder.imageView.setOnClickListener(v -> {
            setBook(audioTrack);
            Intent intent = new Intent(context, PlayTrackActivity.class);
            context.startActivity(intent);
        });

        return convertView;
    }

    public static void setBook (Book audioTrack) {
        ArrayList<AudioItem> audioItemList;
        Gson gson = new Gson();
        Type type = new TypeToken<ArrayList<AudioItem>>() {}.getType();
        audioItemList = gson.fromJson(audioTrack.chapters, type);

        PlayPanelActivity.currentChapter = audioItemList.get(0);
        PlayPanelActivity.chapters = audioItemList;
        PlayPanelActivity.currentBook = audioTrack;
        PlayPanelActivity playPanelActivity = new PlayPanelActivity();
        playPanelActivity.setAttributes(false, 0, 0L);
    }

    private static class ViewHolder {
        final ImageView imageView;
        final ImageButton imageButton;
        final TextView textView;

        ViewHolder(View view) {
            imageView = view.findViewById(R.id.card_image);
            imageButton = view.findViewById(R.id.card_imageBtn);
            textView = view.findViewById(R.id.card_text_below);
        }
    }
}





