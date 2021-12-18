package com.dam.puzzlepals;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.dam.puzzlepals.enums.MusicPlayer;
import com.dam.puzzlepals.models.Score;
import com.dam.puzzlepals.sqlite.ScoreAPI;
import com.dam.puzzlepals.ui.HelpActivity;
import com.dam.puzzlepals.ui.ScoreListAdapter;
import com.dam.puzzlepals.ui.SelectImgActivity;
import com.dam.puzzlepals.ui.SettingsActivity;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ScoreAPI scoreAPI = new ScoreAPI(this);
        ArrayList<Score> betterScores = scoreAPI.getBetterScores(null, 3);

        if (betterScores.size() > 0) {
            ListView topScoreList = findViewById(R.id.top_score_list);
            topScoreList.setVisibility(View.VISIBLE);
            ScoreListAdapter topScoreAdapter = new ScoreListAdapter(this, betterScores);
            topScoreList.setAdapter(topScoreAdapter);
        } else {
            TextView emptyScoreList = findViewById(R.id.empty_score_list);
            emptyScoreList.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finishAffinity();
        System.exit(0);
    }

    @Override
    protected void onStart() {
        super.onStart();

        Intent backgroundMusicService = new Intent(this, BackgroundMusicService.class);
        backgroundMusicService.setAction(MusicPlayer.PLAY.toString());
        startService(backgroundMusicService);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        Intent backgroundMusicService = new Intent(this, BackgroundMusicService.class);
        backgroundMusicService.setAction(MusicPlayer.STOP.toString());
        startService(backgroundMusicService);
    }

    // Creates Action Menu
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_action, menu);

        return true;
    }

    // Assign activity to Action Menu options
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        int menuItemId = menuItem.getItemId();

        if (menuItemId == R.id.help_item) {
            Intent helpActivityIntent = new Intent(this, HelpActivity.class);
            startActivity(helpActivityIntent);
        } else if (menuItemId == R.id.settings_item) {
            Intent settingsActivityIntent = new Intent(this, SettingsActivity.class);
            startActivity(settingsActivityIntent);
        }

        return super.onOptionsItemSelected(menuItem);
    }

    public void onClickPlayButton(View view) {
        Intent selectImageActivityIntent = new Intent(this, SelectImgActivity.class);
        startActivity(selectImageActivityIntent);
    }

}