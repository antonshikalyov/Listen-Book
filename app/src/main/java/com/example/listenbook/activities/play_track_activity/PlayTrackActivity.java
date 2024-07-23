package com.example.listenbook.activities.play_track_activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.ActivityCompat;
import androidx.core.os.LocaleListCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.app.RecoverableSecurityException;
import android.content.ContentUris;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.listenbook.AnimationMoreButton;
import com.example.listenbook.R;
import com.example.listenbook.activities.ActivityBookSettings;
import com.example.listenbook.activities.ActivityHistory;
import com.example.listenbook.activities.ActivitySettings;
import com.example.listenbook.activities.ActivityStatistics;
import com.example.listenbook.activities.MainActivity;
import com.example.listenbook.activities.activity_bookmarks.ActivityBookmarks;
import com.example.listenbook.activities.activity_books.ActivityBooks;
import com.example.listenbook.databinding.ActivityPlayTrackBinding;
import com.example.listenbook.entities.AudioItem;
import com.example.listenbook.services.DataBase;
import com.example.listenbook.services.MediaPlaybackService;
import com.example.listenbook.services.Permission;
import com.example.listenbook.services.PermissionsCode;
import com.example.listenbook.services.UserLearning;
import com.google.gson.Gson;

import java.io.File;
import java.util.ArrayList;
import java.util.Locale;

public class PlayTrackActivity extends AppCompatActivity {
    // Used to load the 'listenbook' library on application startup.
    static {
        System.loadLibrary("listenbook");
    }
    public PlayListAdapter playListAdapter;
    SharedPreferences.Editor editor;

    @SuppressLint({"CutPasteId", "ResourceAsColor"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        com.example.listenbook.databinding.ActivityPlayTrackBinding binding = ActivityPlayTrackBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        if (PlayPanelActivity.chapters != null) {
            RecyclerView trackList = findViewById(R.id.tracks);

            playListAdapter = new PlayListAdapter(this, PlayPanelActivity.chapters, trackList);
            trackList.setLayoutManager(new LinearLayoutManager(this));
            trackList.setAdapter(playListAdapter);
        }


        PlayPanelActivity.aboutSong(findViewById(R.id.full_song_information),
                                    findViewById(R.id.small_song_information),
                                    findViewById(R.id.playButtonsLayout),
                                    playListAdapter);
        Intent serviceIntent = new Intent(this, MediaPlaybackService.class);
        startService(serviceIntent);

        PlayPanelActivity.seekBarVolume(findViewById(R.id.simpleSeekBarSound));

        SharedPreferences sharedPreferences = getSharedPreferences("settings", MODE_PRIVATE);
        editor = sharedPreferences.edit();

        String savedLanguage = sharedPreferences.getString("LANGUAGE", "defaultLanguage");
        LocaleListCompat appLocale;
        if(!savedLanguage.isEmpty()) {
            appLocale = LocaleListCompat.forLanguageTags(savedLanguage);
        } else {
            appLocale = LocaleListCompat.forLanguageTags(Locale.getDefault().getLanguage());
        }
        AppCompatDelegate.setApplicationLocales(appLocale);

        if (sharedPreferences.getInt("ONBOARDING", 0) == 0) {
            Intent intent = new Intent( PlayTrackActivity.this, MainActivity.class);
            startActivity(intent);
        }


        ImageButton burgerButton = findViewById(R.id.burger_button);
        ImageButton toolbarBook = findViewById(R.id.toolbar_library);
        int learningUser = sharedPreferences.getInt("MAIN_USER_LEARNING", 0);

        if (learningUser == 0) {
            UserLearning.learningUserMain(this);
        }

        PlayPanelActivity playPanelActivity = new PlayPanelActivity();
        if(playPanelActivity.playStatus()) {
            ImageButton resumeButton = findViewById(R.id.resumeButton);
            resumeButton.setImageResource(R.drawable.ic_pause);
        }


        int theme = sharedPreferences.getInt("THEME", 1);
        if(theme ==1) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        } else if(theme ==2) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
        }

        ImageButton moreButton = findViewById(R.id.more_button);
        moreButton.setOnClickListener(v -> {
            AnimationMoreButton animationMoreButton = new AnimationMoreButton();
            animationMoreButton.animationMoreAboutSong (findViewById(R.id.full_song_information),
                                                        findViewById(R.id.small_song_information),
                                                        findViewById(R.id.more_button_block));
        });

        ImageButton moreButtonPlay = findViewById(R.id.more_button_play);
        moreButtonPlay.setOnClickListener(v -> {
            AnimationMoreButton animationMoreButton = new AnimationMoreButton();
            animationMoreButton.animationMoreAboutPlay (findViewById(R.id.small_control_panel),
                                                        findViewById(R.id.more_button_play_block));
        });


        DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);
        ImageButton activityBookSettings = findViewById(R.id.speed_control_panel);


        LinearLayout sideBarLibrary = findViewById(R.id.sidebar_library);
        LinearLayout sideBarBookmarks = findViewById(R.id.sidebar_bookmarks);
        LinearLayout sideBarHistory = findViewById(R.id.sidebar_history);
        LinearLayout sideBarStatistics = findViewById(R.id.sidebar_statistics);
        LinearLayout sideBarSettings = findViewById(R.id.sidebar_settings);

        activityBookSettings.setOnClickListener(v -> {
            Intent intent = new Intent(PlayTrackActivity.this, ActivityBookSettings.class);
            startActivity(intent);
        });

        sideBarLibrary.setOnClickListener(v -> {
            Intent intent = new Intent(PlayTrackActivity.this, ActivityBooks.class);
            startActivity(intent);
        });

        sideBarBookmarks.setOnClickListener(v -> {
            Intent intent = new Intent(PlayTrackActivity.this, ActivityBookmarks.class);
            startActivity(intent);
        });

        sideBarHistory.setOnClickListener(v -> {
            Intent intent = new Intent(PlayTrackActivity.this, ActivityHistory.class);
            startActivity(intent);
        });

        sideBarStatistics.setOnClickListener(v -> {
            Intent intent = new Intent(PlayTrackActivity.this, ActivityStatistics.class);
            startActivity(intent);
        });

        sideBarSettings.setOnClickListener(v -> {
            Intent intent = new Intent(PlayTrackActivity.this, ActivitySettings.class);
            startActivity(intent);
        });

        toolbarBook.setOnClickListener(view -> {
            Intent intent = new Intent(PlayTrackActivity.this, ActivityBooks.class);
            startActivity(intent);
        });

        burgerButton.setOnClickListener(view -> {
            if (!drawerLayout.isDrawerOpen(GravityCompat.START)) {
                drawerLayout.openDrawer(GravityCompat.START);
            } else {
                drawerLayout.closeDrawer(GravityCompat.START);
            }
        });

// Example of a call to a native method
//        TextView tv = binding.sampleText;
//        tv.setText(stringFromJNI());
    }
    /**
     * A native method that is implemented by the 'listenbook' native library,
     * which is packaged with this application.
     */
//    public native String stringFromJNI();


    public void resumeMusic(View view) {
            PlayPanelActivity.resumeMusic();
    }

    public void nextChapter(View view) {
            MediaPlaybackService.mediaController.seekToNext();

    }
    public void previousChapter(View view) {
            MediaPlaybackService.mediaController.seekToPrevious();

    }


    public void addBookmark(View view) {
        if (MediaPlaybackService.getMediaController().getMediaItemCount() > 0) {
            PlayPanelActivity.addBookmark();
        }
    }

    public void smForward(View view) {
        SharedPreferences sharedPreferences = getSharedPreferences("settings", MODE_PRIVATE);
        long stepForwardSettings = sharedPreferences.getLong("STEP_REWIND_SETTINGS", 10000);
        AudioItem currentAudioItem = PlayPanelActivity.chapters.get(MediaPlaybackService.getMediaController().getCurrentMediaItemIndex());
        long currentPosition = MediaPlaybackService.getMediaController().getCurrentPosition();
        long neededTime = currentAudioItem.positionInBook + currentPosition + stepForwardSettings;
        int neededChapterIndex = PlayPanelActivity.binarySearchChapter(neededTime);
        AudioItem neededAudioItem = PlayPanelActivity.chapters.get(neededChapterIndex);
        long newLocalTime = neededTime - neededAudioItem.positionInBook;
        MediaPlaybackService.getMediaController().seekTo(neededChapterIndex, newLocalTime);
    }

    public void smBackward(View view) {
        SharedPreferences sharedPreferences = getSharedPreferences("settings", MODE_PRIVATE);
        long stepBackwardSettings = sharedPreferences.getLong("STEP_REWIND_SETTINGS", 10000);
        Log.i("ячсясч", String.valueOf(stepBackwardSettings));

        AudioItem currentAudioItem = PlayPanelActivity.chapters.get(MediaPlaybackService.getMediaController().getCurrentMediaItemIndex());
        long currentPosition = MediaPlaybackService.getMediaController().getCurrentPosition();

        long neededTime = currentAudioItem.positionInBook + currentPosition - stepBackwardSettings;
        if (neededTime < 0) {
            neededTime = 0;
        }

        int neededChapterIndex = PlayPanelActivity.binarySearchChapter(neededTime);
        AudioItem neededAudioItem = PlayPanelActivity.chapters.get(neededChapterIndex);
        long newLocalTime = neededTime - neededAudioItem.positionInBook;
        MediaPlaybackService.getMediaController().seekTo(neededChapterIndex, newLocalTime);
    }

//        public static void x(Uri uri, ContentResolver contentResolver, Context context) {
//            try {
//
//                contentResolver.delete(uri, null, null);
//            } catch (SecurityException e) {
//                PendingIntent pendingIntent = null;
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
//                    // Use MediaStore.createDeleteRequest for API 30 and above
//                    ArrayList<Uri> uris = new ArrayList<>();
//                    uris.add(uri);
//                    try {
//                        pendingIntent = MediaStore.createDeleteRequest(contentResolver, uris);
//                    } catch (IllegalArgumentException ex) {
//                        ex.printStackTrace();
//                        // Handle the case where URI might not be valid
//                        Toast.makeText(context, "Invalid URI for deletion", Toast.LENGTH_SHORT).show();
//                    }
//                } else {
//                    // For earlier versions
//                    if (e instanceof RecoverableSecurityException) {
//                        RecoverableSecurityException exception = (RecoverableSecurityException) e;
//                        pendingIntent = exception.getUserAction().getActionIntent();
//                    }
//                }
//                if (pendingIntent != null) {
//                    IntentSender intentSender = pendingIntent.getIntentSender();
//                    try {
//                        startIntentSenderForResult(intentSender, 100, null, 0, 0, 0);
//                    } catch (IntentSender.SendIntentException ex) {
//                        ex.printStackTrace();
//                    }
//                }
//            }
//        }



    public void deleteChapter(Uri uri) {
        if (uri != null) {
            try {
                int rowsDeleted = getContentResolver().delete(uri, null, null);
                if (rowsDeleted > 0) {
                    Toast.makeText(this, "Item deleted successfully", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Failed to delete item", Toast.LENGTH_SHORT).show();
                }
            } catch (SecurityException e) {
                Log.e("PlayTrackActivity", "SecurityException during deletion", e);
                PendingIntent pendingIntent = null;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    ArrayList<Uri> uris = new ArrayList<>();
                    uris.add(uri);
                    try {
                        pendingIntent = MediaStore.createDeleteRequest(getContentResolver(), uris);
                    } catch (IllegalArgumentException ex) {
                        ex.printStackTrace();
                        Toast.makeText(this, "Invalid URI for deletion", Toast.LENGTH_SHORT).show();
                    }
                } else {
//                    File file = new File(PlayPanelActivity.chapters.get(PlayListAdapter.deletingChapter).uri);
//                    file.delete();
                    if (e instanceof RecoverableSecurityException) {
                        RecoverableSecurityException exception = (RecoverableSecurityException) e;
                        pendingIntent = exception.getUserAction().getActionIntent();
                    }
                }
                if (pendingIntent != null) {
                    IntentSender intentSender = pendingIntent.getIntentSender();
                    try {
                        startIntentSenderForResult(intentSender, 100, null, 0, 0, 0);
                    } catch (IntentSender.SendIntentException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        } else {
            Toast.makeText(this, "URI is null", Toast.LENGTH_SHORT).show();
        }
    }


    public Uri getMediaUri(String filePath) {
        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        String[] projection = new String[]{MediaStore.MediaColumns._ID};
        String selection = MediaStore.MediaColumns.DATA + "=?";
        String[] selectionArgs = new String[]{filePath};

        Cursor cursor = getContentResolver().query(uri, projection, selection, selectionArgs, null);
        if (cursor != null && cursor.moveToFirst()) {
            long id = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.MediaColumns._ID));
            cursor.close();
            return ContentUris.withAppendedId(uri, id);
        }
        if (cursor != null) {
            cursor.close();
        }
        return null;
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && resultCode == RESULT_OK) {
            MediaPlaybackService.mediaController.removeMediaItem(PlayListAdapter.deletingChapter);
            ArrayList<AudioItem> x = PlayPanelActivity.chapters;
            x.remove(PlayListAdapter.deletingChapter);
            PlayPanelActivity.chapters = x;
            Gson gson = new Gson();
            String chapters = gson.toJson(x);
            DataBase dataBase = new DataBase(this);
            dataBase.updateChapters(PlayPanelActivity.currentBook.id, chapters);
            MediaPlaybackService.playBookmark(MediaPlaybackService.mediaController.getCurrentMediaItemIndex(), MediaPlaybackService.mediaController.getCurrentPosition());
            if (playListAdapter != null) {
                playListAdapter.updateData(PlayPanelActivity.chapters);
            }

        } else {
            Toast.makeText(this, "Deletion cancelled or failed", Toast.LENGTH_LONG).show();
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PermissionsCode.REQUEST_CODE_PERMISSIONS_DELETE_CHAPTER) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Uri uri = getMediaUri(PlayPanelActivity.chapters.get(PlayListAdapter.deletingChapter).uri);
                if (uri != null) {
                    deleteChapter(uri);
                } else {
                    Toast.makeText(this, "Failed to retrieve URI", Toast.LENGTH_SHORT).show();
                }
            } else if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                && ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            Toast.makeText(this, "This permission is needed", Toast.LENGTH_LONG).show();
            } else {
                Permission permission = new Permission();
                permission.showSettingsDialog(this);
            }
        }
    }

    }
