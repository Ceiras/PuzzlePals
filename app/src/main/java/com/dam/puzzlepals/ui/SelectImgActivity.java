package com.dam.puzzlepals.ui;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.dam.puzzlepals.R;
import com.dam.puzzlepals.holders.PuzzleHolder;

public class SelectImgActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_img);

        ImageView selectedImageView = findViewById(R.id.selected_img_view);
        selectedImageView.setImageResource(R.drawable.puzzle_pals_logo);

        BitmapDrawable drawable = (BitmapDrawable) selectedImageView.getDrawable();
        Bitmap bitmap = drawable.getBitmap();
        PuzzleHolder.getInstance().getPuzzle().setImage(bitmap);
    }

    public void chooseLevel(View view) {
        Intent selectLevelActivityIntent = new Intent(this, SelectLevelActivity.class);
        startActivity(selectLevelActivityIntent);
    }

}