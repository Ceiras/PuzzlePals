package com.dam.puzzlepals.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import java.io.ByteArrayOutputStream;

public class GalleryManager {

    public static String bitmapToBase64(Bitmap bitmap) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);

        return Base64.encodeToString(outputStream.toByteArray(), Base64.DEFAULT);
    }

    public static Bitmap base64ToBitmap(String base64) {
        byte[] base64Decoded = Base64.decode(base64, Base64.DEFAULT);

        return BitmapFactory.decodeByteArray(base64Decoded, 0, base64Decoded.length);
    }

}
