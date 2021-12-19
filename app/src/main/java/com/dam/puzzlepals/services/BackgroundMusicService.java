package com.dam.puzzlepals.services;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.IBinder;

import androidx.annotation.Nullable;

import com.dam.puzzlepals.R;
import com.dam.puzzlepals.enums.MusicPlayer;
import com.dam.puzzlepals.holders.PuzzleHolder;

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
        } else if (action.equals(MusicPlayer.CHANGE_SONG)) {
            changeSong(intent.getData());
        } else if (action.equals(MusicPlayer.MUTE)) {
            mute();
        } else if (action.equals(MusicPlayer.UNMUTE)) {
            unmute();
        }

        return Service.START_NOT_STICKY;
    }

    private void play() {
        mediaPlayer.start();
    }

    private void pause() {
        mediaPlayer.pause();
    }

    private void stop() {
        mediaPlayer.stop();
        mediaPlayer.release();
    }

    private void changeSong(Uri uriSong) {
        mediaPlayer = MediaPlayer.create(this, uriSong);
        mediaPlayer.setLooping(true);
        if (PuzzleHolder.getInstance().isMute()) {
            mute();
        } else {
            unmute();
        }
        play();
    }

    private void mute() {
        mediaPlayer.setVolume(0, 0);
        PuzzleHolder.getInstance().setMute(true);
    }

    private void unmute() {
        mediaPlayer.setVolume(1, 1);
        PuzzleHolder.getInstance().setMute(false);
    }

    @Override
    public void onDestroy() {
        stop();
    }
}
