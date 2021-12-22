package com.dam.puzzlepals.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.dam.puzzlepals.R;
import com.dam.puzzlepals.enums.Level;
import com.dam.puzzlepals.holders.PuzzleHolder;

public class SelectLevelActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_level);
    }

    public void selectLevel(View view) {
        Level level = Enum.valueOf(Level.class, view.getTag().toString());
        PuzzleHolder.getInstance().getPuzzle().setLevel(level);
        Intent puzzleActivityIntent = new Intent(SelectLevelActivity.this, PuzzleActivity.class);
        startActivity(puzzleActivityIntent);
    }

}
