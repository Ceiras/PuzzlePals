package com.dam.puzzlepals.ui;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.dam.puzzlepals.R;
import com.dam.puzzlepals.database.ImagesCollection;
import com.dam.puzzlepals.holders.PuzzleHolder;
import com.dam.puzzlepals.utils.GalleryManager;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SelectImgActivity extends AppCompatActivity {

    private boolean nextStep;

    @BindView(R.id.selected_img_view)
    ImageView selectedImageView;
    @BindView(R.id.loading_image_spinner)
    ProgressBar loadingImgSpinner;
    @BindView(R.id.next_button)
    Button nextButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_img);

        nextStep = false;

        ButterKnife.bind(this)

        Handler getPuzzleImageHandler = new Handler(Looper.getMainLooper());
        getPuzzleImageHandler.post(this::getPuzzleImage);
    }

    private void getPuzzleImage() {
        Long puzzleNumber = PuzzleHolder.getInstance().getUser().getPuzzleNumber();
        Task<QuerySnapshot> puzzleImageQuery = ImagesCollection.getImage(puzzleNumber);
        puzzleImageQuery.addOnSuccessListener(command -> {
            loadingImgSpinner.setVisibility(View.INVISIBLE);
            if (command.getDocuments().size() == 1) {
                DocumentSnapshot puzzleImage = command.getDocuments().get(0);
                Bitmap puzzleBitmap = GalleryManager.base64ToBitmap(puzzleImage.getString(ImagesCollection.IMAGES_COL_DATA));
                selectedImageView.setImageBitmap(puzzleBitmap);
                PuzzleHolder.getInstance().getPuzzle().setImage(puzzleBitmap);
                PuzzleHolder.getInstance().getPuzzle().setNumber(puzzleNumber);
                nextStep = true;
                nextButton.setVisibility(View.VISIBLE);
            } else {
                Toast.makeText(this, R.string.get_from_database_error, Toast.LENGTH_LONG).show();
                finish();
            }
        }).addOnFailureListener(command -> {
            Toast.makeText(this, R.string.get_from_database_error, Toast.LENGTH_LONG).show();
            finish();
        });
    }

    public void chooseLevel(View view) {
        if (this.nextStep) {
            Intent selectLevelActivityIntent = new Intent(this, SelectLevelActivity.class);
            startActivity(selectLevelActivityIntent);
        }
    }

}