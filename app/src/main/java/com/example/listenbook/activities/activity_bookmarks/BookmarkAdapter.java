package com.example.listenbook.activities.activity_bookmarks;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import com.example.listenbook.R;
import com.example.listenbook.entities.Book;
import com.example.listenbook.entities.Bookmark;
import com.example.listenbook.services.DataBase;

import java.util.ArrayList;

public class BookmarkAdapter extends ArrayAdapter<Integer> {
    private final ArrayList<Integer> fKeys;
    private LayoutInflater inflater;
    BookmarkChaptersAdapter bookmarkAdapter;
    Context context;
    DataBase dataBase;

    private int layout;
    public BookmarkAdapter(@NonNull Context context, int resource, @NonNull ArrayList<Integer> bookmarks) {
        super(context, resource, bookmarks);
        this.fKeys = bookmarks;
        this.layout = resource;
        this.context = context;
        this.inflater = LayoutInflater.from(context);
    }

    @NonNull
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        ViewHolder viewHolder;
        dataBase = new DataBase(context);
        if (convertView == null) {
            convertView = inflater.inflate(this.layout, parent, false);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        final Integer fKeyBookmark = fKeys.get(position);
        final Book book = dataBase.selectBook(fKeyBookmark);

        ArrayList<Bookmark> bookmarks = dataBase.selectBookmarkByForeignKey(fKeyBookmark);
        BookmarkChaptersAdapter chaptersAdapter = new BookmarkChaptersAdapter(context, R.layout.item_bookmark_chapter, bookmarks);

        viewHolder.listView.setAdapter(chaptersAdapter);
        if (book != null) {
            viewHolder.textView.setText(book.title);
        }
        setListViewHeightBasedOnChildren(viewHolder.listView);

        viewHolder.imageButton.setOnClickListener(view -> {
            showDeleteBookmarksDialog(book, position);
        });

        return convertView;
    }

    @SuppressLint("MissingInflatedId")
    public void showDeleteBookmarksDialog(Book book, int pos) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.NoBackgroundDialog);

        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View customLayout = layoutInflater.inflate(R.layout.dialog_delete_bookmarks, null);
        TextView textView = customLayout.findViewById(R.id.title_edit_bookmarks);
        textView.setText(book.title);

        builder.setView(customLayout);
        AlertDialog dialog = builder.create();

        customLayout.findViewById(R.id.delete_bookmarks).setOnClickListener(view -> {
            dataBase.deleteBookmarksByForeignKey(book.id);
            this.fKeys.remove(pos);
            notifyDataSetChanged();
            dialog.dismiss();
            dialog.dismiss();
        });

        customLayout.findViewById(R.id.button_cancel).setOnClickListener(view -> {
            dialog.dismiss();
        });

        dialog.show();
    }

    private static void setListViewHeightBasedOnChildren(ListView listView) {
        ArrayAdapter adapter = (ArrayAdapter) listView.getAdapter();
        if (!adapter.isEmpty()) {
            int totalHeight = 0;
            for (int i = 0; i < adapter.getCount(); i++) {
                View listItem = adapter.getView(i, null, listView);
                listItem.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
                totalHeight += listItem.getMeasuredHeight();
            }

            ViewGroup.LayoutParams params = listView.getLayoutParams();
            params.height = totalHeight + (listView.getDividerHeight() * (adapter.getCount() - 1));
            listView.setLayoutParams(params);
            listView.requestLayout();
        }
    }

    private static class ViewHolder {
        final ImageButton imageButton;
        final TextView textView;
        final ListView listView;
        ViewHolder(View view){
            imageButton = view.findViewById(R.id.card_imageBtn_bookmark);
            textView = view.findViewById(R.id.item_bookmark_title);
            listView = view.findViewById(R.id.chaptersListView);
        }
    }
}
