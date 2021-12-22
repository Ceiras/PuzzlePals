package com.dam.puzzlepals.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

public class GalleryManager {

    public static Bitmap base64ToBitmap(String base64) {
        byte[] base64Decoded = Base64.decode(base64, Base64.DEFAULT);

        return BitmapFactory.decodeByteArray(base64Decoded, 0, base64Decoded.length);
    }

}
