package com.dam.puzzlepals;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.dam.puzzlepals.database.ImagesCollection;
import com.dam.puzzlepals.enums.Level;
import com.dam.puzzlepals.utils.GalleryManager;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class ScoreActivity extends AppCompatActivity {

    private ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_score);

        TextView dateText = findViewById(R.id.date_text);
        TextView scoreText = findViewById(R.id.score_text);
        TextView levelText = findViewById(R.id.level_text);
        imageView = findViewById(R.id.score_image);

        Intent intent = getIntent();
        String date = intent.getStringExtra("date");
        String score = intent.getStringExtra("score");
        Level level = Level.valueOf(intent.getStringExtra("level"));
        int number = intent.getIntExtra("number", 0);

        dateText.setText(date);
        scoreText.setText(score);
        levelText.setText(level.toString());

        Handler getPuzzleImageHandler = new Handler(Looper.getMainLooper());
        getPuzzleImageHandler.post(() -> {
            getPuzzleImage(number);
        });
    }

    private void getPuzzleImage(int puzzleNumber) {
        Task<QuerySnapshot> puzzleImageQuery = ImagesCollection.getImage(puzzleNumber);
        puzzleImageQuery.addOnSuccessListener(command -> {
            if (command.getDocuments().size() == 1) {
                DocumentSnapshot puzzleImage = command.getDocuments().get(0);
                Bitmap puzzleBitmap = GalleryManager.base64ToBitmap(puzzleImage.getString(ImagesCollection.IMAGES_COL_DATA));
                imageView.setImageBitmap(puzzleBitmap);
            } else {
                Toast.makeText(this, R.string.get_from_database_error, Toast.LENGTH_LONG).show();
            }
        });
    }

    public void onClickStartButton(View view) {
        Intent mainActivityIntent = new Intent(this, MainActivity.class);
        startActivity(mainActivityIntent);
    }
}