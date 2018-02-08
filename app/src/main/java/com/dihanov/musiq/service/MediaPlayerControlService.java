package com.dihanov.musiq.service;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.media.MediaMetadata;
import android.media.session.MediaController;
import android.media.session.MediaSessionManager;
import android.media.session.PlaybackState;
import android.os.IBinder;
import android.service.notification.NotificationListenerService;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.dihanov.musiq.service.scrobble.Scrobbler;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import javax.inject.Inject;

import dagger.android.AndroidInjection;

/**
 * Created by dimitar.dihanov on 2/6/2018.
 */

public class MediaPlayerControlService extends NotificationListenerService
        implements MediaSessionManager.OnActiveSessionsChangedListener {
    private static final String TAG = MediaPlayerControlService.class.getSimpleName();

    @Inject
    Scrobbler scrobbler;

    private List<MediaController> currentControllers = new ArrayList<>();

    @Override
    public void onCreate() {
        AndroidInjection.inject(this);

        MediaSessionManager mediaSessionManager =
                (MediaSessionManager)
                        getApplicationContext().getSystemService(Context.MEDIA_SESSION_SERVICE);

        ComponentName componentName = new ComponentName(this, this.getClass());
        mediaSessionManager.addOnActiveSessionsChangedListener(this, componentName);

        //force trigger the event before user presses a button, if this is not called, then the media
        //controller will be found only after the user interacts with the media player, thus, skipping a track
        List<MediaController> initialSessions = mediaSessionManager.getActiveSessions(componentName);
        onActiveSessionsChanged(initialSessions);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public void onActiveSessionsChanged(@Nullable List<MediaController> controllers) {
        List<MediaController> toAdd = new ArrayList<>();

        MediaController.Callback callback = new MediaController.Callback() {
            @Override
            public void onPlaybackStateChanged(@NonNull PlaybackState state) {
                scrobbler.setStatus(state);
            }

            @Override
            public void onMetadataChanged(@Nullable MediaMetadata metadata) {
                scrobbler.updateTrackInfo(metadata);
            }
        };

        for (ListIterator<MediaController> iterator = currentControllers.listIterator(); iterator.hasNext(); ) {
            MediaController element = iterator.next();
            boolean contains = false;

            for (MediaController controller : controllers) {
                if (controller.getPackageName().equals(element.getPackageName())) {
                    contains = true;
                    break;
                }
            }

            if (!contains) {
                element.unregisterCallback(callback);
                iterator.remove();
            }
        }

        for (MediaController controller : controllers) {
            boolean contains = false;
            for (MediaController currentController : currentControllers) {
                if (currentController.getPackageName().equals(controller.getPackageName())) {
                    contains = true;
                    break;
                }
            }

            if (!contains) {
                controller.registerCallback(callback);
                toAdd.add(controller);
            }
        }

        currentControllers.addAll(toAdd);
    }
}