package com.dam.puzzlepals.utils;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Base64;
import android.widget.ImageView;

import com.dam.puzzlepals.sqlite.ScoreAPI;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Random;

public class GalleryManager {

    public static ArrayList<Uri> getAllImagesFromGallery(Context context) {
        Uri uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        String[] projection = {MediaStore.Images.Media._ID, MediaStore.Images.Media.BUCKET_DISPLAY_NAME};
        Cursor cursor = context.getContentResolver().query(uri, projection, null, null, "");

        ArrayList<Uri> listOfAllImages = new ArrayList<>();
        if (cursor.moveToFirst()) {
            int idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID);
            do {
                long idImage = cursor.getLong(idColumn);
                Uri uriImage = Uri.withAppendedPath(uri, String.valueOf(idImage));
                listOfAllImages.add(uriImage);
            } while (cursor.moveToNext());
        }

        cursor.close();

        return listOfAllImages;
    }

    public static Uri getRandomImageFromGallery(Context context) {
        ScoreAPI scoreApi = new ScoreAPI(context);

        ArrayList<Uri> images = getAllImagesFromGallery(context);
        ArrayList<String> puzzleCompleted = scoreApi.getPuzzlesCompleted();

        Uri randomImage;
        boolean puzzleMatch;
        int attempts = (int) ((int) images.size() * 1.5);
        int attemptsCount = 0;
        do {
            randomImage = images.get(new Random().nextInt(images.size()));
            puzzleMatch = puzzleCompleted.contains(uriToBase64(context, randomImage));
            attemptsCount++;
        } while (puzzleMatch && attemptsCount <= attempts);

        return randomImage;
    }

    public static String uriToBase64(Context context, Uri uri) {
        ImageView imageView = new ImageView(context);
        imageView.setImageURI(uri);
        BitmapDrawable drawable = (BitmapDrawable) imageView.getDrawable();
        Bitmap bitmap = drawable.getBitmap();
        return bitmapToBase64(bitmap);
    }

    public static String bitmapToBase64(Bitmap bitmap) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);

        return Base64.encodeToString(outputStream.toByteArray(), Base64.DEFAULT);
    }

}
