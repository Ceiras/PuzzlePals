package com.dam.puzzlepals.ui;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.dam.puzzlepals.R;
import com.dam.puzzlepals.holders.PuzzleHolder;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class SelectImgActivity extends AppCompatActivity {
    private ArrayList<Integer> imagenesDisponibles = new java.util.ArrayList<>();
    private ArrayList<Integer> imagenesUsadas = new ArrayList<>();
    private Integer REQUEST_CAMERA = 1;
    File archivoFoto;
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
// cámara
        if (Build.VERSION.SDK_INT >= 23) {
        requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA}, 2);
    }


    pl = findViewById(R.id.tablero_juego);

    // Establecemos la imagen seleccionada como puzzle
        try {
        pl.establecerImagen(imagen, numCortes);
    } catch (
    IOException e) {
        e.printStackTrace();
    }
}