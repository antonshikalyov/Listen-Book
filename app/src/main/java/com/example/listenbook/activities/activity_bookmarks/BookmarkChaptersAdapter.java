package com.example.listenbook.activities.activity_bookmarks;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import com.example.listenbook.R;
import com.example.listenbook.activities.PlayPanelActivity;
import com.example.listenbook.activities.PlayTrackActivity;
import com.example.listenbook.entities.AudioItem;
import com.example.listenbook.entities.Book;
import com.example.listenbook.entities.Bookmark;
import com.example.listenbook.services.DataBase;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicLong;

public class BookmarkChaptersAdapter extends ArrayAdapter<Bookmark> {
    private final ArrayList<Bookmark> bookmarks;
    private LayoutInflater inflater;
    private static Context context;
    private int layout;

    public BookmarkChaptersAdapter(@NonNull Context context, int resource, @NonNull ArrayList<Bookmark> bookmarks) {
        super(context, resource, bookmarks);
        this.bookmarks = bookmarks;
        this.layout = resource;
        this.inflater = LayoutInflater.from(context);
        this.context = context;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        ViewHolder viewHolder;

        if (convertView == null) {
            convertView = inflater.inflate(this.layout, parent, false);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        final Bookmark bookmark = bookmarks.get(position);
        Log.i("Dialog1", String.valueOf(bookmark.id));
        viewHolder.textView.setText(bookmark.notes);
        viewHolder.textView1.setText(PlayPanelActivity.convertMils((int) bookmark.duration));

        viewHolder.textView.setOnClickListener(v ->{
            showSettingsDialog(bookmark, position);
        });

        return convertView;
    }

    @SuppressLint("MissingInflatedId")
    public void showSettingsDialog(Bookmark bookmark, int pos) {
        Log.i("Dialog1", String.valueOf(bookmark.id));
        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.NoBackgroundDialog);

        LayoutInflater inflater = LayoutInflater.from(context);
        final View customLayout = inflater.inflate(R.layout.dialog_bookmarks, null);
        builder.setView(customLayout);
        AlertDialog dialog = builder.create();
        DataBase dataBase = new DataBase(context);

        customLayout.findViewById(R.id.run_bookmark).setOnClickListener(v -> {
            Book book = dataBase.selectBook(bookmark.fKeyBookId);
            ArrayList<AudioItem> audioItemList;
            Gson gson = new Gson();
            Type type = new TypeToken<ArrayList<AudioItem>>() {}.getType();
            audioItemList = gson.fromJson(book.chapters, type);
            PlayPanelActivity.chapters = audioItemList;
            PlayPanelActivity.currentBook = book;

            int bookmarkIndex = PlayPanelActivity.binarySearchChapter(bookmark.duration);
            if (bookmarkIndex >= 0 && bookmarkIndex < audioItemList.size()) {
                PlayPanelActivity.currentChapter = audioItemList.get(bookmarkIndex);
                PlayPanelActivity playPanelActivity = new PlayPanelActivity();
                playPanelActivity.setAttributes(true,bookmarkIndex, bookmark.duration - PlayPanelActivity.currentChapter.positionInBook);
            }
            Intent intent = new Intent(context, PlayTrackActivity.class);
            context.startActivity(intent);
        });

        customLayout.findViewById(R.id.button_edit).setOnClickListener(v -> {
            editBookmark(bookmark, pos);
            dialog.dismiss();
        });

        customLayout.findViewById(R.id.button_delete).setOnClickListener(v -> {
            dataBase.deleteBookmark(bookmark.id);
            this.bookmarks.remove(bookmark);
            notifyDataSetChanged();
            dialog.dismiss();
        });

        customLayout.findViewById(R.id.button_cancel).setOnClickListener(v -> {
            dialog.dismiss();
        });

        dialog.show();
    }

    @SuppressLint("SetTextI18n")
    public void editBookmark(Bookmark myBookmark, int pos) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.NoBackgroundDialog);

        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View customLayout = layoutInflater.inflate(R.layout.dialog_add_bookmark, null);
        EditText editText = customLayout.findViewById(R.id.dialog_add_bookmark_editText);
        TextView time = customLayout.findViewById(R.id.dialog_add_bookmark);
        DataBase dataBase = new DataBase(context);
        Book book = dataBase.selectBook(myBookmark.fKeyBookId);
        AtomicLong readedGlobalDuration = new AtomicLong(myBookmark.duration);

        editText.setText(myBookmark.notes);
        time.setText(PlayPanelActivity.convertMils((int) readedGlobalDuration.get()));

        builder.setView(customLayout);

        AlertDialog dialog = builder.create();

        TextView songName = customLayout.findViewById(R.id.dialog_add_bookmark_title);
        songName.setText(book.title);

        customLayout.findViewById(R.id.dialog_add_bookmark_plus_30sec).setOnClickListener(v -> {
            if ((readedGlobalDuration.get() + 30000L) < book.book_duration) {
                readedGlobalDuration.addAndGet(30000L);
            }
            time.setText(PlayPanelActivity.convertMils(Math.toIntExact(readedGlobalDuration.get())));
        });

        customLayout.findViewById(R.id.dialog_add_bookmark_minus_30sec).setOnClickListener(v -> {
            if ((readedGlobalDuration.get() - 30000L) >= 0) {
                readedGlobalDuration.addAndGet(-30000L);
            }
            time.setText(PlayPanelActivity.convertMils(Math.toIntExact(readedGlobalDuration.get())));
        });

        customLayout.findViewById(R.id.dialog_add_bookmark_save).setOnClickListener(v -> {
            String text = String.valueOf(editText.getText());
            if (!text.isEmpty()) {
                dialog.dismiss();
            }
        });

        customLayout.findViewById(R.id.dialog_add_bookmark_cancel).setOnClickListener(v -> {
            dialog.dismiss();
        });

        customLayout.findViewById(R.id.dialog_add_bookmark_save).setOnClickListener(v -> {
            Bookmark bookmark = new Bookmark( myBookmark.fKeyBookId, book.title, editText.getText().toString(), readedGlobalDuration.get());
            DataBase dataBase1 = new DataBase(context);
            dataBase1.updateBookmark(myBookmark.id, bookmark);
            this.bookmarks.set(pos, bookmark);
            notifyDataSetChanged();
            dialog.dismiss();
        });
        dialog.show();
    }

    private static class ViewHolder {
        final TextView textView1;
        final TextView textView;

        ViewHolder(View view) {
            textView1 = view.findViewById(R.id.rightTextView);
            textView = view.findViewById(R.id.leftTextView);
        }
    }
}