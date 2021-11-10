package com.dam.puzzlepals.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import static com.dam.puzzlepals.entities.PuzzleModel.DIFICULTAD_DIFICIL;
import static com.dam.puzzlepals.entities.PuzzleModel.DIFICULTAD_FACIL;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;

import com.dam.puzzlepals.MainActivity;
import com.dam.puzzlepals.MainActivityViewModel;
import com.dam.puzzlepals.R;
import com.dam.puzzlepals.entities.Pieza;
import com.dam.puzzlepals.entities.PuzzleModel;

import java.util.ArrayList;
import java.util.List;

public class SelectLevelActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_level);
    }

    public void onClickEasyButton(View view) {
        PuzzleActivity.dificult = DIFICULTAD_FACIL;
        Intent puzzleActivityIntent = new Intent(SelectLevelActivity.this, PuzzleActivity.class);
        startActivity(puzzleActivityIntent);
    }

    public void onClickHardButton(View view) {
        MainActivityViewModel viewModel = new ViewModelProvider(this).get(MainActivityViewModel.class);
        viewModel.getPuzzleModel().setDificultad(DIFICULTAD_DIFICIL);
        Intent puzzleActivityIntent = new Intent(SelectLevelActivity.this, PuzzleActivity.class);
        startActivity(puzzleActivityIntent);
    }
}