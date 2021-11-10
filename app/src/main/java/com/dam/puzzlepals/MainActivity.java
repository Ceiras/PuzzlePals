package com.dam.puzzlepals;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.dam.puzzlepals.entities.Score;
import com.dam.puzzlepals.enums.Levels;
import com.dam.puzzlepals.sqlite.ScoreAPI;
import com.dam.puzzlepals.ui.ScoreListAdapter;
import com.dam.puzzlepals.ui.SelectImgActivity;

import java.util.ArrayList;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ScoreAPI scoreAPI = new ScoreAPI(this);
        // TODO: Check what score will be showed
        ArrayList<Score> betterScores = scoreAPI.getBetterScores(null, 3);

        betterScores.add(new Score(new Date(), 125L, Levels.EASY));
        betterScores.add(new Score(new Date(), 98L, Levels.MEDIUM));
        betterScores.add(new Score(new Date(), 134L, Levels.HARD));

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

    // Creates Action Menu
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_action, menu);

        return true;
    }

    // Assign function to Action Menu options
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        int menuItemId = menuItem.getItemId();

        // TODO: Redirection to Help Activity

        return super.onOptionsItemSelected(menuItem);
    }

    public void onClickPlayButton(View view) {
        Intent selectImageActivityIntent = new Intent(this, SelectImgActivity.class);
        startActivity(selectImageActivityIntent);
    }

}