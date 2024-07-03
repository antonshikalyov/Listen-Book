package com.example.listenbook.services;

import android.os.Bundle;

import androidx.annotation.OptIn;
import androidx.media3.common.util.UnstableApi;
import androidx.media3.session.MediaSession;
import androidx.media3.session.SessionCommand;
import androidx.media3.session.SessionCommands;

public class CustomMediaSessionCallback implements MediaSession.Callback {
    @OptIn(markerClass = UnstableApi.class)
    @Override
    public MediaSession.ConnectionResult onConnect(
            MediaSession session,
            MediaSession.ControllerInfo controller) {
        SessionCommands sessionCommands =
                MediaSession.ConnectionResult.DEFAULT_SESSION_COMMANDS.buildUpon()
                        .add(new SessionCommand("PREVIOUS_TRACK", new Bundle()))
                        .add(new SessionCommand("PLAY_PAUSE", new Bundle()))
                        .add(new SessionCommand("NEXT_TRACK", new Bundle()))
                        .build();
        return new MediaSession.ConnectionResult.AcceptedResultBuilder(MediaPlaybackService.mediaSession)
                .setAvailableSessionCommands(sessionCommands)
                .build();
    }
}
