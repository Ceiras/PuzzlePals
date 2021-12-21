package com.dam.puzzlepals;

import android.app.Activity;
import android.app.Application;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.dam.puzzlepals.enums.MusicPlayer;
import com.dam.puzzlepals.services.BackgroundMusicService;

public class App extends Application implements Application.ActivityLifecycleCallbacks {

    private int activityReferences = 0;
    private boolean isActivityChangingConfigurations = false;

    @Override
    public void onCreate() {
        super.onCreate();
        registerActivityLifecycleCallbacks(this);
    }

    @Override
    public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle savedInstanceState) {

    }

    @Override
    public void onActivityStarted(@NonNull Activity activity) {
        if (++activityReferences == 1 && !isActivityChangingConfigurations) {
            Intent backgroundMusicService = new Intent(this, BackgroundMusicService.class);
            backgroundMusicService.setAction(MusicPlayer.PLAY.toString());
            startService(backgroundMusicService);
        }
    }

    @Override
    public void onActivityResumed(@NonNull Activity activity) {

    }

    @Override
    public void onActivityPaused(@NonNull Activity activity) {

    }

    @Override
    public void onActivityStopped(@NonNull Activity activity) {
        isActivityChangingConfigurations = activity.isChangingConfigurations();
        if (--activityReferences == 0 && !isActivityChangingConfigurations) {
            Intent backgroundMusicService = new Intent(this, BackgroundMusicService.class);
            backgroundMusicService.setAction(MusicPlayer.PAUSE.toString());
            startService(backgroundMusicService);
        }
    }

    @Override
    public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle outState) {

    }

    @Override
    public void onActivityDestroyed(@NonNull Activity activity) {
        Intent backgroundMusicService = new Intent(this, BackgroundMusicService.class);
        backgroundMusicService.setAction(MusicPlayer.STOP.toString());
        startService(backgroundMusicService);
    }
}
