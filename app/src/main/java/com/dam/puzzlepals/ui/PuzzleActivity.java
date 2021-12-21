package com.dam.puzzlepals.ui;

import android.Manifest;
import android.app.Dialog;
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

import com.dam.puzzlepals.MainActivity;
import com.dam.puzzlepals.R;
import com.dam.puzzlepals.utils.CalendarManager;
import com.dam.puzzlepals.enums.Level;
import com.dam.puzzlepals.holders.PuzzleHolder;
import com.dam.puzzlepals.models.PuzzlePiece;
import com.dam.puzzlepals.models.Score;
import com.dam.puzzlepals.sqlite.ScoreAPI;
import com.dam.puzzlepals.utils.GalleryManager;
import com.dam.puzzlepals.utils.NotificationsManager;
import com.dam.puzzlepals.utils.PermissionManger;
import com.dam.puzzlepals.utils.TimeConverter;

import java.util.ArrayList;
import java.util.List;

public class PuzzleActivity extends AppCompatActivity {

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
        ImageView puzzleCompletedView = findViewById(R.id.puzzle_completed_image_view);

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
                PuzzlePiece puzzlePiece = getItem(position);
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
                            Animation fadeOutAnimation = AnimationUtils.loadAnimation(PuzzleActivity.this, R.anim.fade_out);
                            fadeOutAnimation.setAnimationListener(new Animation.AnimationListener() {
                                @Override
                                public void onAnimationStart(Animation animation) {

                                }

                                @Override
                                public void onAnimationEnd(Animation animation) {
                                    Animation fadeInAnimation = AnimationUtils.loadAnimation(PuzzleActivity.this, R.anim.fade_in);
                                    fadeInAnimation.setAnimationListener(new Animation.AnimationListener() {
                                        @Override
                                        public void onAnimationStart(Animation animation) {

                                        }

                                        @Override
                                        public void onAnimationEnd(Animation animation) {
                                            PuzzleHolder.getInstance().getPuzzle().finish();
                                            long score = PuzzleHolder.getInstance().getPuzzle().getScore();
                                            Level level = PuzzleHolder.getInstance().getPuzzle().getLevel();
                                            String image = GalleryManager.bitmapToBase64(PuzzleHolder.getInstance().getPuzzle().getImage());

                                            boolean isRecord = NotificationsManager.isRecord(PuzzleActivity.this, score);

                                            ScoreAPI scoreApi = new ScoreAPI(PuzzleActivity.this);
                                            Score scorePuzzle = scoreApi.addScore(score, level, image);

                                            CalendarManager calendar = new CalendarManager(PuzzleActivity.this);
                                            PermissionManger.manageCalendarPermissions(PuzzleActivity.this, PuzzleActivity.this, calendar, Manifest.permission.READ_CALENDAR);
                                            PermissionManger.manageCalendarPermissions(PuzzleActivity.this, PuzzleActivity.this, calendar, Manifest.permission.WRITE_CALENDAR);
                                            calendar.addRecordEventToCalendar(PuzzleActivity.this, level, TimeConverter.convertTimeMillisToReadableString(score));

                                            if (isRecord) {
                                                NotificationsManager.createNotification(PuzzleActivity.this, scorePuzzle, PuzzleHolder.getInstance().getPuzzle().getNumber());
                                            }

                                            final Dialog finishDialog = new Dialog(PuzzleActivity.this, android.R.style.Theme_Black_NoTitleBar);
                                            finishDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.argb(100, 0, 0, 0)));
                                            finishDialog.setContentView(R.layout.congrats_dialogue);
                                            finishDialog.setCancelable(true);
                                            finishDialog.show();

                                            TextView scoreText = (TextView) finishDialog.findViewById(R.id.score_txt);
                                            scoreText.setText(TimeConverter.convertTimeMillisToReadableString(score));
                                            Button finishButton = (Button) finishDialog.findViewById(R.id.finish_btn);
                                            finishButton.setOnClickListener(dialogView -> {
                                                finishDialog.dismiss();
                                                Intent mainActivityIntent = new Intent(PuzzleActivity.this, MainActivity.class);
                                                startActivity(mainActivityIntent);
                                            });
                                        }

                                        @Override
                                        public void onAnimationRepeat(Animation animation) {

                                        }
                                    });
                                    fadeInAnimation.setFillAfter(true);
                                    puzzleCompletedView.setImageBitmap(PuzzleHolder.getInstance().getPuzzle().getImage());
                                    puzzleCompletedView.setVisibility(View.VISIBLE);
                                    puzzleCompletedView.startAnimation(fadeInAnimation);
                                }

                                @Override
                                public void onAnimationRepeat(Animation animation) {

                                }
                            });

                            fadeOutAnimation.setFillAfter(true);
                            puzzleGridView.startAnimation(fadeOutAnimation);
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

}
