package com.dam.puzzlepals.ui;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.dam.puzzlepals.MainActivity;
import com.dam.puzzlepals.R;
import com.dam.puzzlepals.calendar.CalendarManager;
import com.dam.puzzlepals.enums.Level;
import com.dam.puzzlepals.holders.PuzzleHolder;
import com.dam.puzzlepals.models.PuzzlePiece;
import com.dam.puzzlepals.sqlite.ScoreAPI;
import com.dam.puzzlepals.utils.TimeConverter;

import java.util.ArrayList;
import java.util.List;


public class PuzzleActivity extends AppCompatActivity {

    private final int CALENDAR_PERMISSION_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_puzzle);

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

        ArrayAdapter<PuzzlePiece> gridAdapter = new ArrayAdapter<PuzzlePiece>(PuzzleActivity.this, R.layout.activity_puzzle, PuzzleHolder.getInstance().getPuzzle().getShuffledPuzzlePieces()) {
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
        MediaPlayer exchangePiecesMediaPlayer = MediaPlayer.create(this, R.raw.exchange_piece_sound);
        puzzleGridView.setOnItemClickListener((parent, view, position, id) -> {
            piecesToExchange.add(position);
            if (piecesToExchange.size() == 2) {
                exchangePiecesMediaPlayer.start();

                View oldPiece = puzzleGridView.getChildAt(piecesToExchange.get(0));
                View newPiece = puzzleGridView.getChildAt(piecesToExchange.get(1));

                Animation exchangePieceAnimation = AnimationUtils.loadAnimation(this, R.anim.disappear);

                exchangePieceAnimation.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        PuzzleHolder.getInstance().getPuzzle().exchangePieces(piecesToExchange.get(0), piecesToExchange.get(1));
                        gridAdapter.notifyDataSetChanged();

                        boolean isComplete = PuzzleHolder.getInstance().getPuzzle().isComplete();
                        if (isComplete) {
                            PuzzleHolder.getInstance().getPuzzle().finish();
                            long score = PuzzleHolder.getInstance().getPuzzle().getScore();
                            Level level = PuzzleHolder.getInstance().getPuzzle().getLevel();

                            ScoreAPI scoreApi = new ScoreAPI(PuzzleActivity.this);
                            scoreApi.addScore(score, level);

                            CalendarManager calendar = new CalendarManager(PuzzleActivity.this);
                            managePermissions(calendar, Manifest.permission.READ_CALENDAR);
                            managePermissions(calendar, Manifest.permission.WRITE_CALENDAR);
                            calendar.addRecordEvenToCalendar(PuzzleActivity.this, level, score, TimeConverter.convertTimeMillisToReadableString(score));

                            final Dialog finishDialog = new Dialog(PuzzleActivity.this, android.R.style.Theme_Black_NoTitleBar);
                            finishDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.argb(100, 0, 0, 0)));
                            finishDialog.setContentView(R.layout.congrats_dialogue);
                            finishDialog.setCancelable(true);
                            finishDialog.show();

                            TextView scoreText = (TextView) finishDialog.findViewById(R.id.score_txt);
                            scoreText.setText(TimeConverter.convertTimeMillisToReadableString(score));
                            Button finishButton = (Button) finishDialog.findViewById(R.id.finish_btn);
                            finishButton.setOnClickListener(dialogView -> {
                                Intent mainActivityIntent = new Intent(PuzzleActivity.this, MainActivity.class);
                                startActivity(mainActivityIntent);
                            });
                        } else {
                            piecesToExchange.clear();
                        }
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });

                oldPiece.startAnimation(exchangePieceAnimation);
                newPiece.startAnimation(exchangePieceAnimation);
            } else {
                Animation selectPieceAnimation = AnimationUtils.loadAnimation(this, R.anim.selected);
                selectPieceAnimation.setFillAfter(true);
                view.startAnimation(selectPieceAnimation);
            }
        });
    }

    // With this code the app check if the user has given permissions to use the calendar's user.
    public void managePermissions(CalendarManager calendar, String permission) {
        boolean result = calendar.checkPermissions(PuzzleActivity.this, permission);
        if (!result) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, permission)) {
                new AlertDialog.Builder(this)
                        .setTitle("Permission needed")
                        .setMessage("This permission is needed because of this and that")
                        .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ActivityCompat.requestPermissions(
                                        PuzzleActivity.this,
                                        new String[]{
                                                Manifest.permission.READ_CALENDAR,
                                                Manifest.permission.WRITE_CALENDAR
                                        },
                                        CALENDAR_PERMISSION_CODE
                                );
                            }
                        })
                        .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .create().show();

            } else {
                ActivityCompat.requestPermissions(
                        this,
                        new String[]{
                                Manifest.permission.READ_CALENDAR,
                                Manifest.permission.WRITE_CALENDAR
                        },
                        CALENDAR_PERMISSION_CODE
                );
            }
        }
    }

}
