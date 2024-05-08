package com.example.listenbook;
import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

    public class DataBase extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION = 1;
    public static String DATABASE_NAME = "audioBooks.db";
    public static String TABLE_NAME = "books";

    private static final String COLUMN_ID = "id";
    private static final String COLUMN_TITLE = "title";
    private static final String COLUMN_IMAGE = "image";
    private static final String COLUMN_TIME = "time";
    private static final String COLUMN_SIZE = "size";
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
                    COLUMN_TIME + " TEXT," +
                    COLUMN_SIZE + " INTEGER," +
                    COLUMN_CHAPTERS + " TEXT)";
            sqLiteDatabase.execSQL(createTableQuery);
        }

        @Override
        public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

        }

        public void insertAudioTrack(AudioTrack audioTrack) {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(COLUMN_TITLE, audioTrack.title);
            values.put(COLUMN_IMAGE, audioTrack.image);
            values.put(COLUMN_TIME, audioTrack.time);
            values.put(COLUMN_SIZE, audioTrack.size);
            values.put(COLUMN_CHAPTERS, new Gson().toJson(audioTrack.chapters));
            db.insert(TABLE_NAME, null, values);
            db.close();
        }

    }

class AudioTrack {
    public int id;
    public String title;
    public byte[] image;
    public String time;
    public int size;
    public String[] chapters;

}
