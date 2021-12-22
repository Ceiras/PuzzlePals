package com.dam.puzzlepals.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.widget.Toast;

import com.dam.puzzlepals.R;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

public class GalleryManager {

    public static Bitmap base64ToBitmap(String base64) {
        byte[] base64Decoded = Base64.decode(base64, Base64.DEFAULT);

        return BitmapFactory.decodeByteArray(base64Decoded, 0, base64Decoded.length);
    }

    public static String urlToBase64(Context context, String url) {
        try {
            InputStream inputStream = new URL(url).openConnection().getInputStream();
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int read = 0;
            while ((read = inputStream.read(buffer, 0, buffer.length)) != -1) {
                byteArrayOutputStream.write(buffer, 0, read);
            }
            byteArrayOutputStream.flush();

            return Base64.encodeToString(byteArrayOutputStream.toByteArray(), Base64.DEFAULT);
        } catch (IOException e) {
            Toast.makeText(context, R.string.get_from_database_error, Toast.LENGTH_LONG).show();
        }

        return null;
    }

}
