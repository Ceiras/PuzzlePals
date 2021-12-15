package com.dam.puzzlepals;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;

import androidx.annotation.Nullable;

import com.dam.puzzlepals.enums.MusicPlayer;

public class BackgroundMusicService extends Service {

    public MediaPlayer mediaPlayer;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        mediaPlayer = MediaPlayer.create(this, R.raw.background_sound);
        mediaPlayer.setLooping(true);
    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        MusicPlayer action = MusicPlayer.valueOf(intent.getAction());
        if (action.equals(MusicPlayer.PLAY)) {
            play();
        } else if (action.equals(MusicPlayer.PAUSE)) {
            pause();
        } else if (action.equals(MusicPlayer.STOP)) {
            stop();
        }

        return Service.START_NOT_STICKY;
    }

    private void stop() {
        mediaPlayer.stop();
    }

    private void play() {
        mediaPlayer.start();
    }

    private void pause() {
        mediaPlayer.pause();
    }

    @Override
    public void onDestroy() {
        stop();
        mediaPlayer.release();
    }
}