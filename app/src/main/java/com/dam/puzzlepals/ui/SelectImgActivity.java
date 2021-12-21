package com.dam.puzzlepals.ui;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import com.dam.puzzlepals.R;
import com.dam.puzzlepals.holders.PuzzleHolder;
import com.dam.puzzlepals.utils.GalleryManager;

import java.io.File;
import java.io.IOException;

public class SelectImgActivity extends AppCompatActivity {

    private boolean imageSelected = false;
    private ActivityResultLauncher<Intent> galleryLauncher;
    private String imagePath;
    private TextView alertText;
    private Button nextButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_img);

        alertText = findViewById(R.id.alert_text);
        nextButton = findViewById(R.id.next_button);

        ImageView selectedImageView = findViewById(R.id.selected_img_view);
        galleryLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                if (result.getResultCode() == RESULT_OK) {
                    SelectImgActivity.this.imageSelected = true;
                    nextButton.setBackgroundColor(Color.parseColor("#FF6200EE"));
                    nextButton.setClickable(SelectImgActivity.this.imageSelected);
                    alertText.setVisibility(View.INVISIBLE);
                    Bitmap bitmap;
                    if (result.getData().getData() != null) {
                        Uri photoPath = result.getData().getData();
                        PuzzleHolder.getInstance().getPuzzle().setPath(photoPath.toString());
                        selectedImageView.setImageURI(photoPath);
                        BitmapDrawable drawable = (BitmapDrawable) selectedImageView.getDrawable();
                        bitmap = drawable.getBitmap();
                    } else {
                        bitmap = BitmapFactory.decodeFile(imagePath);
                        PuzzleHolder.getInstance().getPuzzle().setPath(imagePath);
                        selectedImageView.setImageBitmap(bitmap);
                    }
                    PuzzleHolder.getInstance().getPuzzle().setImage(bitmap);
                } else {
                    SelectImgActivity.this.imageSelected = false;
                    selectedImageView.setImageURI(null);
                    nextButton.setClickable(SelectImgActivity.this.imageSelected);
                    nextButton.setBackgroundColor(Color.GRAY);
                    alertText.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    public void loadImageFromGallery(View view) {
        ImageView selectedImageView = findViewById(R.id.selected_img_view);

        Uri imagePath = GalleryManager.getRandomImageFromGallery(this);
        if (imagePath != null) {
            PuzzleHolder.getInstance().getPuzzle().setPath(imagePath.toString());
            SelectImgActivity.this.imageSelected = true;
            nextButton.setBackgroundColor(Color.parseColor("#FF6200EE"));
            nextButton.setClickable(SelectImgActivity.this.imageSelected);
            alertText.setVisibility(View.INVISIBLE);
            selectedImageView.setImageURI(imagePath);
            BitmapDrawable drawable = (BitmapDrawable) selectedImageView.getDrawable();
            Bitmap bitmap = drawable.getBitmap();
            PuzzleHolder.getInstance().getPuzzle().setImage(bitmap);
        } else {
            SelectImgActivity.this.imageSelected = false;
            selectedImageView.setImageURI(null);
            nextButton.setClickable(SelectImgActivity.this.imageSelected);
            nextButton.setBackgroundColor(Color.GRAY);
            alertText.setVisibility(View.VISIBLE);
        }
    }

    public void takeImageFromCamera(View view) {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        try {
            Uri photoURI = FileProvider.getUriForFile(this, this.getApplicationContext().getPackageName() + ".provider", getImageFile());
            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
            galleryLauncher.launch(cameraIntent);
        } catch (IOException e) {
            alertText.setVisibility(View.VISIBLE);
        }
    }

    public void chooseLevel(View view) {
        if (this.imageSelected) {
            Intent selectLevelActivityIntent = new Intent(this, SelectLevelActivity.class);
            startActivity(selectLevelActivityIntent);
        }
    }

    private File getImageFile() throws IOException {
        File storageDirectory = getExternalFilesDir(Environment.DIRECTORY_PICTURES);

        File imageFile = File.createTempFile("IMG_", ".jpg", storageDirectory);
        imagePath = imageFile.getAbsolutePath();

        return imageFile;
    }

}