package com.dam.puzzlepals;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.dam.puzzlepals.enums.Level;
import com.dam.puzzlepals.utils.GalleryManager;

public class ScoreActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_score);

        TextView dateText = findViewById(R.id.date_text);
        TextView scoreText = findViewById(R.id.score_text);
        TextView levelText = findViewById(R.id.level_text);
        ImageView imageView = findViewById(R.id.score_image);

        Intent intent = getIntent();
        String date = intent.getStringExtra("date");
        String score = intent.getStringExtra("score");
        Level level = Level.valueOf(intent.getStringExtra("level"));
        String image = intent.getStringExtra("image");

        dateText.setText(date);
        scoreText.setText(score);
        levelText.setText(level.toString());
        imageView.setImageBitmap(GalleryManager.uriToBitmap(this, Uri.parse(image)));
    }

    public void onClickStartButton(View view) {
        Intent mainActivityIntent = new Intent(this, MainActivity.class);
        startActivity(mainActivityIntent);
    }
}