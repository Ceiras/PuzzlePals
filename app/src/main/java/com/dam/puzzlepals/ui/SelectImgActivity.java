package com.dam.puzzlepals.ui;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.dam.puzzlepals.R;
import com.dam.puzzlepals.holders.PuzzleHolder;

public class SelectImgActivity extends AppCompatActivity {

    private boolean imageSelected = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_img);

        ImageView selectedImageView = findViewById(R.id.selected_img_view);
        ActivityResultLauncher<Intent> launcher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                TextView alertText = findViewById(R.id.alert_text);
                Button nextButton = findViewById(R.id.next_button);

                if (result.getResultCode() == RESULT_OK) {
                    SelectImgActivity.this.imageSelected = true;

                    nextButton.setClickable(SelectImgActivity.this.imageSelected);
                    alertText.setVisibility(View.INVISIBLE);

                    Uri photoPath = result.getData().getData();

                    selectedImageView.setImageURI(photoPath);

                    BitmapDrawable drawable = (BitmapDrawable) selectedImageView.getDrawable();
                    Bitmap bitmap = drawable.getBitmap();
                    PuzzleHolder.getInstance().getPuzzle().setImage(bitmap);
                } else {
                    nextButton.setClickable(SelectImgActivity.this.imageSelected);
                    nextButton.setBackgroundColor(Color.GRAY);
                    alertText.setVisibility(View.VISIBLE);
                }
            }
        });

        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");

        Button selectImageButton = findViewById(R.id.select_img_button);
        selectImageButton.setOnClickListener(view -> launcher.launch(intent));
        selectedImageView.setOnClickListener(view -> launcher.launch(intent));

        launcher.launch(intent);
    }

    public void loadImageFromGallery(View view) {
        Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivity(galleryIntent);
    }

    public void chooseLevel(View view) {
        if (this.imageSelected) {
            Intent selectLevelActivityIntent = new Intent(this, SelectLevelActivity.class);
            startActivity(selectLevelActivityIntent);
        }
    }

}