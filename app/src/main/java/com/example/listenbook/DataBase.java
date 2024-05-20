package com.example.listenbook;
import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.List;

public class DataBase extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION = 1;
    public static String DATABASE_NAME = "audioBooks.db";
    public static String TABLE_NAME = "books";

    private static final String COLUMN_ID = "id";
    private static final String COLUMN_TITLE = "title";
    private static final String COLUMN_IMAGE = "image";
    private static final String COLUMN_SIZE = "size";
    private static final String DIRECTORY_URI = "directory_uri";
    private static final String COLUMN_CHAPTERS = "chapters";


    public DataBase (Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
        @Override
        public void onCreate(SQLiteDatabase sqLiteDatabase) {
            String createTableQuery = "CREATE TABLE " + TABLE_NAME + " (" +
                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    COLUMN_TITLE + " TEXT," +
                    COLUMN_IMAGE + " BLOB," +
                    DIRECTORY_URI + " TEXT," +
                    COLUMN_CHAPTERS + " TEXT)";
            sqLiteDatabase.execSQL(createTableQuery);
        }


        @Override
        public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

        }


    public void onDelete(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME, COLUMN_ID + " = ?", new String[]{String.valueOf(id)});
        db.close();
    }

    public boolean haveNotes() {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT COUNT(*) FROM " + TABLE_NAME;
        Cursor cursor = db.rawQuery(query, null);
        boolean hasNotes = false;

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                int count = cursor.getInt(0);
                hasNotes = (count > 0);
            }
            cursor.close();
        }
        db.close();

        return hasNotes;
    }

    public void insertAudioTrack(AudioTrack audioTrack) {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(COLUMN_TITLE, audioTrack.title);
            values.put(COLUMN_IMAGE, audioTrack.image);
            values.put(DIRECTORY_URI, audioTrack.directory_uri);
            values.put(COLUMN_CHAPTERS, new Gson().toJson(audioTrack.chapters));
            db.insert(TABLE_NAME, null, values);
            db.close();
        }

        public boolean isUriExists(String directoryUri) {
            SQLiteDatabase db = this.getReadableDatabase();
            String[] projection = {COLUMN_ID};
            String selection = DIRECTORY_URI + " = ?";
            String[] selectionArgs = {directoryUri};

            Cursor cursor = db.query(TABLE_NAME, projection, selection, selectionArgs, null, null, null);
            int count = cursor.getCount();
            cursor.close();
            return count > 0;
        }

        public List<AudioTrack> getAllAudioTracks() {
            List<AudioTrack> audioTracks = new ArrayList<>();
            String selectQuery = "SELECT * FROM " + TABLE_NAME;

            SQLiteDatabase db = this.getReadableDatabase();
            Cursor cursor = db.rawQuery(selectQuery, null);

            if (cursor.moveToFirst()) {
                do {
                    @SuppressLint("Range") String id = cursor.getString(cursor.getColumnIndex(COLUMN_ID));
                    @SuppressLint("Range") String title = cursor.getString(cursor.getColumnIndex(COLUMN_TITLE));
                    @SuppressLint("Range") byte[] image = cursor.getBlob(cursor.getColumnIndex(COLUMN_IMAGE));
                    @SuppressLint("Range") String directoryUri = cursor.getString(cursor.getColumnIndex(DIRECTORY_URI));
                    @SuppressLint("Range") String chaptersJson = cursor.getString(cursor.getColumnIndex(COLUMN_CHAPTERS));
                    String chapters = new Gson().fromJson(chaptersJson, String.class);

                    AudioTrack audioTrack = new AudioTrack(id, title, image, directoryUri, chaptersJson);
                    audioTracks.add(audioTrack);
                } while (cursor.moveToNext());
            }
            cursor.close();
            return audioTracks;
        }

    }

class AudioTrack {
    public String id;
    public String title;
    public byte[] image;
    public String directory_uri;
    public String chapters;

    public AudioTrack(String id, String title, byte[] image, String directory_uri, String chapters) {
        this.id = id;
        this.title = title;
        this.image = image;
        this.directory_uri = directory_uri;
        this.chapters = chapters;
    }

    public AudioTrack(String title, byte[] image, String directory_uri, String chapters) {
        this.id = id;
        this.title = title;
        this.image = image;
        this.directory_uri = directory_uri;
        this.chapters = chapters;
    }
}
