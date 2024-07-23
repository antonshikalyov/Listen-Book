package com.example.listenbook.services;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.MediaMetadata;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import androidx.annotation.Nullable;
import androidx.annotation.OptIn;
import androidx.core.app.NotificationCompat;
import androidx.media3.common.ForwardingPlayer;
import androidx.media3.common.MediaItem;
import androidx.media3.common.Player;
import androidx.media3.common.util.UnstableApi;
import androidx.media3.exoplayer.ExoPlayer;
import androidx.media3.session.CommandButton;
import androidx.media3.session.MediaController;
import androidx.media3.session.MediaSession;
import androidx.media3.session.MediaSessionService;
import androidx.media3.session.SessionCommand;

import com.example.listenbook.activities.play_track_activity.PlayListAdapter;
import com.example.listenbook.activities.play_track_activity.PlayTrackActivity;
import com.example.listenbook.activities.play_track_activity.PlayPanelActivity;
import com.example.listenbook.R;
import com.example.listenbook.entities.AudioItem;
import com.google.common.collect.ImmutableList;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.MoreExecutors;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MediaPlaybackService extends MediaSessionService  {
    public static ExoPlayer player;
    public static MediaSession mediaSession = null;
    public static MediaController mediaController;
    public static NotificationManager notificationManager;
    public static NotificationChannel channel;
    public static List<MediaItem> mediaItems = new ArrayList<>();

public List<MediaItem> getMediaItems() {
    return mediaItems;
}

    private final BroadcastReceiver becomingNoisyReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (AudioManager.ACTION_AUDIO_BECOMING_NOISY.equals(intent.getAction())) {
                if (player != null && player.getPlayWhenReady()) {
                    getMediaController().pause();
                    updateNotification(context, R.drawable.ic_play);
                }
            }
        }
    };

    @OptIn(markerClass = UnstableApi.class)
    @Override
    public void onCreate() {
        super.onCreate();

        IntentFilter intentFilter = new IntentFilter(PlayPanelActivity.mAudioManager.ACTION_AUDIO_BECOMING_NOISY);
        registerReceiver(becomingNoisyReceiver, intentFilter);

        player = new ExoPlayer.Builder(this).build();
        CommandButton previousTrackButton = new CommandButton.Builder()
                .setDisplayName("Previous Track")
                .setIconResId(R.drawable.button_prev)
                .setSessionCommand(new SessionCommand("PREVIOUS_TRACK", new Bundle()))
                .build();

        CommandButton playPauseButton = new CommandButton.Builder()
                .setDisplayName("Play/Pause")
                .setIconResId(R.drawable.ic_pause)
                .setSessionCommand(new SessionCommand("PLAY_PAUSE", new Bundle()))
                .build();

        CommandButton nextTrackButton = new CommandButton.Builder()
                .setDisplayName("Next Track")
                .setIconResId(R.drawable.button_next)
                .setSessionCommand(new SessionCommand("NEXT_TRACK", new Bundle()))
                .build();


        ForwardingPlayer forwardingPlayer = new ForwardingPlayer(player) {
            @Override
            public void play() {
                PlayPanelActivity.play();
                super.play();
            }

            @Override
            public void pause() {
                PlayPanelActivity.pause();
                super.pause();
            }

            @Override
            public void prepare() {
                PlayPanelActivity.setInfo();
                super.prepare();
            }

            @Override
            public void seekToDefaultPosition() {
                super.seekToDefaultPosition();
                PlayPanelActivity.setInfo();
            }

            @Override
            public void seekTo(long positionMs) {
                super.seekTo(positionMs);
            }

            @Override
            public void seekToNext() {
                super.seekToNext();
                PlayPanelActivity.setInfo();
            }

            @Override
            public void seekToPrevious() {
                super.seekToPrevious();
                PlayPanelActivity.setInfo();
            }

            @Override
            public void setPlayWhenReady(boolean playWhenReady) {
                super.setPlayWhenReady(playWhenReady);
                seekToNext();
                PlayPanelActivity.setInfo();
            }
        };
        Intent intent = new Intent(this, PlayTrackActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        mediaSession =
                new MediaSession.Builder(this, forwardingPlayer)
                        .setCallback(new CustomMediaSessionCallback())
                        .setCustomLayout(ImmutableList.of(previousTrackButton, playPauseButton, nextTrackButton))
                        .setSessionActivity(pendingIntent)
                        .build();
            createMediaController();
    }

    public static void setMediaItems() {
        mediaController.pause();
        mediaController.stop();
        mediaItems.clear();
        if (PlayPanelActivity.chapters != null) {
            for (AudioItem item : PlayPanelActivity.chapters) {
                MediaMetadataRetriever retriever = new MediaMetadataRetriever();
                retriever.setDataSource(item.uri);

                String title = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);
                String artist = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);
                String album = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM);
                int discNumber = item.id;
                byte[] artworkData = retriever.getEmbeddedPicture();

                @OptIn(markerClass = UnstableApi.class)
                androidx.media3.common.MediaMetadata mediaMetadata = new androidx.media3.common.MediaMetadata.Builder()
                        .setTitle(title)
                        .setArtist(album)
                        .setAlbumArtist(artist)
                        .setArtworkData(artworkData)
                        .setTrackNumber(discNumber)
                        .build();

                @OptIn(markerClass = UnstableApi.class) MediaItem mediaItem = new MediaItem.Builder()
                        .setMediaMetadata(mediaMetadata)
                        .setUri(Uri.fromFile(new File(item.uri)))
                        .build();
                mediaItems.add(mediaItem);
            }
        }
    }

    public static void playTrack() {
        DataBase dataBase = new DataBase(PlayPanelActivity.playButtonsLayout.getContext());
        setMediaItems();
        mediaController.setMediaItems(mediaItems);
        mediaController.prepare();
        mediaController.setPlaybackSpeed(dataBase.getBookSpeedByForeignKey(PlayPanelActivity.currentBook.id));
        mediaController.play();
    }

    public static void playBookmark(int startIndex, long position) {
        DataBase dataBase = new DataBase(PlayPanelActivity.playButtonsLayout.getContext());
        setMediaItems();
        mediaController.setMediaItems(mediaItems, startIndex, position);
        mediaController.prepare();
        mediaController.setPlaybackSpeed(dataBase.getBookSpeedByForeignKey(PlayPanelActivity.currentBook.id));
        mediaController.play();
    }



    private void createMediaController() {
        PlayListAdapter playListAdapter = new PlayListAdapter();

        ListenableFuture<MediaController> controllerFuture =
                new MediaController.Builder(this, mediaSession.getToken()).buildAsync();
        controllerFuture.addListener(() -> {
            try {
                mediaController = controllerFuture.get();
                if (mediaController != null) {
                    mediaController.addListener(new Player.Listener() {
                        @Override
                        public void onPositionDiscontinuity(Player.PositionInfo oldPosition, Player.PositionInfo newPosition, int reason) {
                            PlayPanelActivity.setInfo();
                        }

                        @Override
                        public void onMediaItemTransition(@Nullable MediaItem mediaItem, int reason) {
                            Log.i("MediaPlaybackService", "onMediaItemTransition: " + mediaItem.mediaMetadata.title);
                            PlayPanelActivity.setInfo();
                            playListAdapter.setCurrentItemIndex(mediaController.getCurrentMediaItemIndex());

                        }

                        @Override
                        public void onPlaybackStateChanged(int state) {
                            PlayPanelActivity.setInfo();
                            playListAdapter.setCurrentItemIndex(mediaController.getCurrentMediaItemIndex());

                        }

                        @Override
                        public void onIsPlayingChanged(boolean isPlaying) {
                            PlayPanelActivity.setInfo();
                        }
                    });
                } else {
                    Log.e("MediaPlaybackService", "MediaController is null");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, MoreExecutors.directExecutor());
    }

    public static MediaController getMediaController() {
        return mediaController;
    }
    public static void createNotificationChannel(Context context) {
        CharSequence name = "Music Channel";
        String description = "Channel for music controls";
        channel = new NotificationChannel("1", name, NotificationManager.IMPORTANCE_LOW);
        channel.setDescription(description);
        notificationManager = context.getSystemService(NotificationManager.class);
        notificationManager.createNotificationChannel(channel);
    }

    public static void updateNotification(Context context, int iconResId) {
        createNotificationChannel(context);

        notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        Intent toggleIntent = new Intent(context, MediaPlaybackService.class);
        toggleIntent.setAction("PLAY_PAUSE");
        PendingIntent togglePendingIntent = PendingIntent.getService(context, 0, toggleIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        Intent nextChapterIntent = new Intent(context, MediaPlaybackService.class);
        nextChapterIntent.setAction("NEXT_CHAPTER");
        PendingIntent nextChapterPendingIntent = PendingIntent.getService(context, 1, nextChapterIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        Intent previousChapterIntent = new Intent(context, MediaPlaybackService.class);
        previousChapterIntent.setAction("PREVIOUS_CHAPTER");
        PendingIntent previousChapterPendingIntent = PendingIntent.getService(context, 2, previousChapterIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        Bitmap bitmap = BitmapFactory.decodeByteArray(PlayPanelActivity.currentBook.image, 0, PlayPanelActivity.currentBook.image.length);
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 50, stream);

        @OptIn(markerClass = UnstableApi.class) Notification customNotification = new NotificationCompat.Builder(context, "1")
                .setSmallIcon(R.drawable.launch_icon)
                .setLargeIcon(bitmap)
                .setContentTitle("Listen Book")
                .setContentText(PlayPanelActivity.currentBook.title)
                .addAction(R.drawable.button_prev, "previous", previousChapterPendingIntent)
                .addAction(iconResId, "resume", togglePendingIntent)
                .addAction(R.drawable.button_next, "next", nextChapterPendingIntent)
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setCategory(NotificationCompat.CATEGORY_TRANSPORT)
                .setOngoing(true)
                .setAutoCancel(false)
                .setStyle(new androidx.media.app.NotificationCompat.MediaStyle()
                        .setMediaSession(mediaSession.getSessionCompatToken())
                        .setShowActionsInCompactView(0, 1, 2))  // Show all actions in compact view
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .build();

        notificationManager.notify(1, customNotification);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        PlayListAdapter playListAdapter = new PlayListAdapter();
        if (intent != null) {
            String action = intent.getAction();
            if (action != null) {
                switch (action) {
                    case "PLAY_PAUSE":
                            PlayPanelActivity.resumeMusic();
                        break;
                    case "NEXT_CHAPTER":
                        getMediaController().seekToNextMediaItem();
                        playListAdapter.setCurrentItemIndex(mediaController.getCurrentMediaItemIndex());
                    case "PREVIOUS_CHAPTER":
                        getMediaController().seekToPreviousMediaItem();
                    break;
                }
            }
        }
        return START_NOT_STICKY;
    }
    @Override
    public void onTaskRemoved(@Nullable Intent rootIntent) {
        player = (ExoPlayer) mediaSession.getPlayer();
        if (player.getPlayWhenReady()) {
            // Make sure the service is not in foreground.
            player.pause();
        }
        stopSelf();
    }
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        super.onBind(intent);
        return new LocalBinder();
    }

    public class LocalBinder extends Binder {
        MediaPlaybackService getService() {
            return MediaPlaybackService.this;
        }
    }

    @Nullable
    @Override
    public MediaSession onGetSession(MediaSession.ControllerInfo controllerInfo) {
        return mediaSession;
    }

    @Override
    public void onDestroy() {
        mediaSession.getPlayer().release();
        mediaSession.release();
        mediaSession = null;
        unregisterReceiver(becomingNoisyReceiver);
        if (notificationManager != null) {
            notificationManager.cancel(1); // Cancel the notification by its ID
            if (channel != null) {
                notificationManager.deleteNotificationChannel(channel.getId());
            }
        }
        super.onDestroy();
    }
}

