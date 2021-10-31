package com.dam.puzzlepals;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.appcompat.app.AppCompatActivity;

public class WelcomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(() -> {
            Intent helpActivityIntent = new Intent(WelcomeActivity.this, MainActivity.class);
            startActivity(helpActivityIntent);
        }, 3000);
    }
}