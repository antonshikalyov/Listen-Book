package com.example.listenbook.services;
import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.listenbook.entities.Book;
import com.example.listenbook.entities.Bookmark;

import java.util.ArrayList;
import java.util.List;

public class DataBase extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION = 2;
    public static String DATABASE_NAME = "Listen.db";
    public static String TABLE_BOOKS = "books";
    public static String TABLE_BOOKS_SETTINGS = "books_settings";
    public static String TABLE_BOOKMARKS = "bookmarks";


    private static final String COLUMN_ID = "id";
    private static final String COLUMN_TITLE = "title";
    private static final String COLUMN_IMAGE = "image";
    private static final String COLUMN_BOOK_DURATION = "book_duration";
    private static final String DIRECTORY_URI = "directory_uri";
    private static final String COLUMN_CHAPTERS = "chapters";

    private static final String COLUMN_BOOK_FOREIGN  = "book_key";
    private static final String COLUMN_BOOKMARK_FOREIGN  = "bookmark_key";
    private static final String COLUMN_BOOKMARK_DURATION = "bookmark_duration";
    private static final String COLUMN_NOTES = "notes";
    private static final String COLUMN_BOOK_SPEED  = "book_audio_speed";

    public DataBase (Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        try {
            String createTableBooks = "CREATE TABLE " + TABLE_BOOKS + " (" +
                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    COLUMN_TITLE + " TEXT," +
                    COLUMN_IMAGE + " BLOB," +
                    COLUMN_BOOK_DURATION + " INTEGER," +
                    DIRECTORY_URI + " TEXT," +
                    COLUMN_CHAPTERS + " TEXT)";
            sqLiteDatabase.execSQL(createTableBooks);

            String createTableSettingsBooks = "CREATE TABLE " + TABLE_BOOKS_SETTINGS + " (" +
                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    COLUMN_BOOK_SPEED + " REAL," +
                    COLUMN_BOOK_FOREIGN + " INTEGER UNIQUE," +  // Уникальный внешний ключ
                    "FOREIGN KEY(" + COLUMN_BOOK_FOREIGN + ") REFERENCES " + TABLE_BOOKS + "(" + COLUMN_ID + ") ON DELETE CASCADE" +
                    ")";
            sqLiteDatabase.execSQL(createTableSettingsBooks);

            String createTableBookmarks = "CREATE TABLE " + TABLE_BOOKMARKS + " (" +
                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    COLUMN_TITLE + " TEXT," +
                    COLUMN_NOTES + " TEXT," +
                    COLUMN_BOOKMARK_DURATION + " INTEGER, " +
                    COLUMN_BOOKMARK_FOREIGN + " INTEGER, " +
                    "FOREIGN KEY(" + COLUMN_BOOKMARK_FOREIGN + ") REFERENCES " + TABLE_BOOKS + "(" + COLUMN_ID + ") ON DELETE CASCADE)";
            sqLiteDatabase.execSQL(createTableBookmarks);
        } catch (SQLiteException e) {
            e.printStackTrace();
        }
    }

    @Override
        public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

        }


    public void onDeleteBook(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_BOOKMARKS, COLUMN_BOOKMARK_FOREIGN + " = ?", new String[]{String.valueOf(id)});
        db.delete(TABLE_BOOKS_SETTINGS, COLUMN_BOOK_FOREIGN + " = ?", new String[]{String.valueOf(id)});
        db.delete(TABLE_BOOKS, COLUMN_ID + " = ?", new String[]{String.valueOf(id)});
        db.close();
    }


    public void insertBook(Book audioTrack) {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(COLUMN_TITLE, audioTrack.title);
            values.put(COLUMN_IMAGE, audioTrack.image);
            values.put(COLUMN_BOOK_DURATION, audioTrack.book_duration);
            values.put(DIRECTORY_URI, audioTrack.directory_uri);
            values.put(COLUMN_CHAPTERS, audioTrack.chapters);
            db.insert(TABLE_BOOKS, null, values);
            db.close();
        }

    public void insertBookmark(Bookmark bookmark) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_TITLE, bookmark.title);
        values.put(COLUMN_NOTES, bookmark.notes);
        values.put(COLUMN_BOOKMARK_DURATION, bookmark.duration);
        values.put(COLUMN_BOOKMARK_FOREIGN, bookmark.fKeyBookId);
        db.insert(TABLE_BOOKMARKS, null, values);
        db.close();
    }

    public void updateBookmark(int bookmarkId, Bookmark updatedBookmark) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_TITLE, updatedBookmark.title);
        values.put(COLUMN_NOTES, updatedBookmark.notes);
        values.put(COLUMN_BOOKMARK_DURATION, updatedBookmark.duration);
        values.put(COLUMN_BOOKMARK_FOREIGN, updatedBookmark.fKeyBookId);

        String selection = COLUMN_ID + " = ?";
        String[] selectionArgs = { String.valueOf(bookmarkId) };

        db.update(TABLE_BOOKMARKS, values, selection, selectionArgs);
        db.close();
    }

    public void deleteBookmark(int bookmarkId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_BOOKMARKS, "ID = ?", new String[]{String.valueOf(bookmarkId)});
        db.close();
    }

    public void deleteBookmarksByForeignKey(int foreignKey) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_BOOKMARKS, COLUMN_BOOKMARK_FOREIGN + " = ?", new String[]{String.valueOf(foreignKey)});
        db.close();
    }


    public boolean isUriExists(String directoryUri) {
            SQLiteDatabase db = this.getReadableDatabase();
            String[] projection = {COLUMN_ID};
            String selection = DIRECTORY_URI + " = ?";
            String[] selectionArgs = {directoryUri};

            Cursor cursor = db.query(TABLE_BOOKS, projection, selection, selectionArgs, null, null, null);
            int count = cursor.getCount();
            cursor.close();
            return count > 0;
        }

    public Book selectBook(int ID) {
        String selectQuery = "SELECT * FROM " + TABLE_BOOKS + " WHERE " + COLUMN_ID + " = ?";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        Book audioTrack = null;

        try {
            cursor = db.rawQuery(selectQuery, new String[]{String.valueOf(ID)});
            if (cursor.moveToFirst()) {
                @SuppressLint("Range") int id = cursor.getInt(cursor.getColumnIndex(COLUMN_ID));
                @SuppressLint("Range") String title = cursor.getString(cursor.getColumnIndex(COLUMN_TITLE));
                @SuppressLint("Range") byte[] image = cursor.getBlob(cursor.getColumnIndex(COLUMN_IMAGE));
                @SuppressLint("Range") Long bookDuration = cursor.getLong(cursor.getColumnIndex(COLUMN_BOOK_DURATION));
                @SuppressLint("Range") String directoryUri = cursor.getString(cursor.getColumnIndex(DIRECTORY_URI));
                @SuppressLint("Range") String chaptersJson = cursor.getString(cursor.getColumnIndex(COLUMN_CHAPTERS));
                audioTrack = new Book(id, title, image, bookDuration, directoryUri, chaptersJson);
            }
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            db.close();
        }

        return audioTrack;
    }

    public void updateChapters(int bookId, String newChapters) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_CHAPTERS, newChapters);

        String selection = COLUMN_ID + " = ?";
        String[] selectionArgs = { String.valueOf(bookId) };

        int rowsAffected = db.update(TABLE_BOOKS, values, selection, selectionArgs);

        if (rowsAffected == 0) {
            System.out.println("No rows updated. Book ID might be incorrect.");
        }

        db.close();
    }


    public ArrayList<Bookmark> selectBookmarkByForeignKey(int ID) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        ArrayList<Bookmark> bookmarks = new ArrayList<>();

        try {
            String selectQuery = "SELECT * FROM " + TABLE_BOOKMARKS + " WHERE " + COLUMN_BOOKMARK_FOREIGN + " = ?";
            cursor = db.rawQuery(selectQuery, new String[]{String.valueOf(ID)});

            while (cursor != null && cursor.moveToNext()) {
                @SuppressLint("Range") int id = cursor.getInt(cursor.getColumnIndex(COLUMN_ID));
                @SuppressLint("Range") int foreignKey = cursor.getInt(cursor.getColumnIndex(COLUMN_BOOKMARK_FOREIGN));
                @SuppressLint("Range") String title = cursor.getString(cursor.getColumnIndex(COLUMN_TITLE));
                @SuppressLint("Range") String notes = cursor.getString(cursor.getColumnIndex(COLUMN_NOTES));
                @SuppressLint("Range") long bookDuration = cursor.getLong(cursor.getColumnIndex(COLUMN_BOOKMARK_DURATION));

                Bookmark bookmark = new Bookmark(id, foreignKey, title, notes, bookDuration);
                bookmarks.add(bookmark);
            }
        } catch (SQLiteException e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            db.close();
        }

        return bookmarks;
    }

    @SuppressLint("Range")
    public float getBookSpeedByForeignKey(int bookId) {
        SQLiteDatabase db = this.getReadableDatabase();
        float speed = 1;

        String[] columns = {COLUMN_BOOK_SPEED};
        String selection = COLUMN_BOOK_FOREIGN + " = ?";
        String[] selectionArgs = {String.valueOf(bookId)};

        Cursor cursor = db.query(TABLE_BOOKS_SETTINGS, columns, selection, selectionArgs,
                null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            speed = cursor.getFloat(cursor.getColumnIndex(COLUMN_BOOK_SPEED));
            cursor.close();
        }

        db.close();
        return speed;
    }
    public long insertBookSettings(int bookId, double speed) {
        SQLiteDatabase db = this.getWritableDatabase();

        if (getBookSettingsIdByBookId(bookId) != -1) {
            return -1;
        }

        ContentValues values = new ContentValues();
        values.put(COLUMN_BOOK_SPEED, speed);
        values.put(COLUMN_BOOK_FOREIGN, bookId);

        long id = db.insert(TABLE_BOOKS_SETTINGS, null, values);
        db.close();
        return id;
    }

    @SuppressLint("Range")
    public int getBookSettingsIdByBookId(int bookId) {
        SQLiteDatabase db = this.getReadableDatabase();
        int id = -1;

        Cursor cursor = db.query(TABLE_BOOKS_SETTINGS,
                new String[]{COLUMN_ID},
                COLUMN_BOOK_FOREIGN + " = ?",
                new String[]{String.valueOf(bookId)},
                null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            id = cursor.getInt(cursor.getColumnIndex(COLUMN_ID));
            cursor.close();
        }
        return id;
    }


    public void updateBookSettingsSpeed(int bookId, double newSpeed) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_BOOK_SPEED, newSpeed);

        int rowsAffected = db.update(TABLE_BOOKS_SETTINGS, values,
                COLUMN_BOOK_FOREIGN + " = ?",
                new String[]{String.valueOf(bookId)});

        db.close();
    }




    public ArrayList getUniqueBookmarksForeigenKeys() {
        SQLiteDatabase db = this.getReadableDatabase();
        String distinctQuery = "SELECT DISTINCT " + COLUMN_BOOKMARK_FOREIGN + " FROM " + TABLE_BOOKMARKS;
        Cursor cursor = db.rawQuery(distinctQuery, null);

        ArrayList<Integer> uniqueForeignKeys = new ArrayList<>();

        if (cursor.moveToFirst()) {
            do {
                @SuppressLint("Range") int foreignKey = cursor.getInt(cursor.getColumnIndex(COLUMN_BOOKMARK_FOREIGN));
                uniqueForeignKeys.add(foreignKey);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return uniqueForeignKeys;
    }



    public List<Book> getAllBooks() {
        List<Book> audioTracks = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_BOOKS;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;

        try {
            cursor = db.rawQuery(selectQuery, null);
            if (cursor.moveToFirst()) {
                do {
                    @SuppressLint("Range") int id = cursor.getInt(cursor.getColumnIndex(COLUMN_ID));
                    @SuppressLint("Range") String title = cursor.getString(cursor.getColumnIndex(COLUMN_TITLE));
                    @SuppressLint("Range") byte[] image = cursor.getBlob(cursor.getColumnIndex(COLUMN_IMAGE));
                    @SuppressLint("Range") Long bookDuration = cursor.getLong(cursor.getColumnIndex(COLUMN_BOOK_DURATION));
                    @SuppressLint("Range") String directoryUri = cursor.getString(cursor.getColumnIndex(DIRECTORY_URI));
                    @SuppressLint("Range") String chaptersJson = cursor.getString(cursor.getColumnIndex(COLUMN_CHAPTERS));

                    Book audioTrack = new Book(id, title, image, bookDuration, directoryUri, chaptersJson);
                    audioTracks.add(audioTrack);
                } while (cursor.moveToNext());
            }
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            db.close();
        }
        return audioTracks;
    }

}

