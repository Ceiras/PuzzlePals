package com.dam.puzzlepals.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.dam.puzzlepals.R;
import com.dam.puzzlepals.database.ScoresCollection;
import com.dam.puzzlepals.enums.Level;
import com.dam.puzzlepals.holders.PuzzleHolder;
import com.dam.puzzlepals.models.Score;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class PersonalScoreActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_top_score);

        Handler getBetterPlayerScoresHandler = new Handler(Looper.getMainLooper());
        getBetterPlayerScoresHandler.post(this::getBetterPlayerScores);
    }

    private void getBetterPlayerScores() {
        ScoresCollection.getUserBestScores(PuzzleHolder.getInstance().getUser().getEmail()).addOnSuccessListener(command -> {
            List<Score> betterScores = command.getDocuments().stream().map(documentSnapshot -> {
                Date date = documentSnapshot.getDate(ScoresCollection.SCORES_COL_DATE);
                String player = documentSnapshot.getString(ScoresCollection.SCORES_COL_EMAIL);
                Level level = Level.valueOf(documentSnapshot.getString(ScoresCollection.SCORES_COL_LEVEL));
                Long puzzleNumber = documentSnapshot.getLong(ScoresCollection.SCORES_COL_PUZZLE_NUMBER);
                Long score = documentSnapshot.getLong(ScoresCollection.SCORES_COL_SCORE);

                return new Score(date, player, level, puzzleNumber, score);
            }).collect(Collectors.toList());

            if (betterScores.size() > 0) {
                ListView personalTopScoreList = findViewById(R.id.personal_top_score_list);
                PlayerScoreListAdapter playerScoreAdapter = new PlayerScoreListAdapter(this, betterScores);
                personalTopScoreList.setAdapter(playerScoreAdapter);
            } else {
                Toast.makeText(this, R.string.top_score_empty_list, Toast.LENGTH_LONG).show();
            }
        });
    }
}