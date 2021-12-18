package com.dam.puzzlepals.ui;

import android.content.Intent;
import android.location.SettingInjectorService;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.documentfile.provider.DocumentFile;

import com.dam.puzzlepals.BackgroundMusicService;
import com.dam.puzzlepals.MainActivity;
import com.dam.puzzlepals.R;
import com.dam.puzzlepals.WelcomeActivity;
import com.dam.puzzlepals.enums.MusicPlayer;

public class SettingsActivity extends AppCompatActivity {

    TextView backgroundMusic;
    ActivityResultLauncher<Intent> launcher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result) {
            if (result.getResultCode() == RESULT_OK) {
                backgroundMusic.setText(R.string.loading_song);

                Uri songPath = result.getData().getData();

                Intent backgroundMusicServiceStopIntent = new Intent(SettingsActivity.this, BackgroundMusicService.class);
                backgroundMusicServiceStopIntent.setAction(MusicPlayer.STOP.toString());
                startService(backgroundMusicServiceStopIntent);

                Intent backgroundMusicServiceChangeMusicIntent = new Intent(SettingsActivity.this, BackgroundMusicService.class);
                backgroundMusicServiceChangeMusicIntent.setAction(MusicPlayer.CHANGE_SONG.toString());
                backgroundMusicServiceChangeMusicIntent.setData(songPath);
                startService(backgroundMusicServiceChangeMusicIntent);

                Handler handler = new Handler(Looper.getMainLooper());
                handler.postDelayed(() -> {
                    DocumentFile documentFileSong = DocumentFile.fromSingleUri(SettingsActivity.this, songPath);
                    String songFilename = documentFileSong != null ? documentFileSong.getName() : String.valueOf(R.string.unknown_sogn);
                    backgroundMusic.setText(songFilename);
                }, 2000);
            } else {
                backgroundMusic.setText(R.string.error_selecting_song);
            }
        }
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        backgroundMusic = findViewById(R.id.background_music);
        backgroundMusic.setText(R.string.default_music);
    }

    public void onClickChangeBackgroundMusic(View view) {
        Intent getMusicIntent = new Intent(Intent.ACTION_GET_CONTENT);
        getMusicIntent.setType("audio/*");
        launcher.launch(getMusicIntent);
    }
}