package com.dam.puzzlepals.ui;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.dam.puzzlepals.MainActivity;
import com.dam.puzzlepals.R;
import com.dam.puzzlepals.enums.Level;
import com.dam.puzzlepals.holders.PuzzleHolder;
import com.dam.puzzlepals.models.PuzzlePiece;
import com.dam.puzzlepals.sqlite.ScoreAPI;
import com.dam.puzzlepals.utils.TimeConverter;

import java.util.ArrayList;
import java.util.List;


public class PuzzleActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.acitivity_puzzle);

        PuzzleHolder.getInstance().getPuzzle().splitPuzzle();
        PuzzleHolder.getInstance().getPuzzle().shuffle();

        buildPuzzleGridView();

        PuzzleHolder.getInstance().getPuzzle().start();
    }

    private void buildPuzzleGridView() {
        GridView puzzleGridView = findViewById(R.id.puzzle_grid_view);

        int grid = 1;
        switch (PuzzleHolder.getInstance().getPuzzle().getLevel()) {
            case EASY:
                grid = 3;
                break;
            case HARD:
                grid = 4;
                break;
        }

        puzzleGridView.setNumColumns(grid);
        puzzleGridView.setVerticalSpacing(3);
        puzzleGridView.setHorizontalSpacing(3);

        ArrayAdapter gridAdapter = new ArrayAdapter<PuzzlePiece>(PuzzleActivity.this, R.layout.acitivity_puzzle, PuzzleHolder.getInstance().getPuzzle().getShuffledPuzzlePieces()) {
            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                View cell = LayoutInflater.from(PuzzleActivity.this).inflate(R.layout.celda_puzzle_grid_view, parent, false);
                ImageView cellImage = cell.findViewById(R.id.celda_puzzle_imagen);
                PuzzlePiece puzzlePiece = (PuzzlePiece) getItem(position);
                cellImage.setImageBitmap(puzzlePiece.getBitmap());

                return cell;
            }
        };

        puzzleGridView.setAdapter(gridAdapter);

        List<Integer> piecesToExchange = new ArrayList<>();
        puzzleGridView.setOnItemClickListener((parent, view, position, id) -> {
            piecesToExchange.add(position);
            if (piecesToExchange.size() == 2) {
                PuzzleHolder.getInstance().getPuzzle().exchangePieces(piecesToExchange.get(0), piecesToExchange.get(1));
                gridAdapter.notifyDataSetChanged();

                boolean isComplete = PuzzleHolder.getInstance().getPuzzle().isComplete();
                if (isComplete) {
                    PuzzleHolder.getInstance().getPuzzle().finish();
                    long score = PuzzleHolder.getInstance().getPuzzle().getScore();
                    Level level = PuzzleHolder.getInstance().getPuzzle().getLevel();

                    ScoreAPI scoreApi = new ScoreAPI(PuzzleActivity.this);
                    scoreApi.addScore(score, level);

                    final Dialog finishDialog = new Dialog(PuzzleActivity.this, android.R.style.Theme_Black_NoTitleBar);
                    finishDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.argb(100, 0, 0, 0)));
                    finishDialog.setContentView(R.layout.congrats_dialogue);
                    finishDialog.setCancelable(true);
                    finishDialog.show();

                    TextView scoreText = (TextView) finishDialog.findViewById(R.id.score_txt);
                    scoreText.setText(TimeConverter.convertTimeMillisToReadableString(score));
                    Button finishButton = (Button) finishDialog.findViewById(R.id.finish_btn);
                    finishButton.setOnClickListener(dialogView -> {
                        Intent mainActivityIntent = new Intent(this, MainActivity.class);
                        startActivity(mainActivityIntent);
                    });
                } else {
                    piecesToExchange.clear();
                }
            }
        });
    }

}
