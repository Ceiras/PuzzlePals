package com.dam.puzzlepals;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.dam.puzzlepals.calendar.CalendarManager;
import com.dam.puzzlepals.models.Score;
import com.dam.puzzlepals.sqlite.ScoreAPI;
import com.dam.puzzlepals.ui.HelpActivity;
import com.dam.puzzlepals.ui.ScoreListAdapter;
import com.dam.puzzlepals.ui.SelectImgActivity;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private int CALENDAR_PERMISSION_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ScoreAPI scoreAPI = new ScoreAPI(this);
        CalendarManager calendar = new CalendarManager(MainActivity.this);
        //ArrayList<Score> betterScores = scoreAPI.getBetterScores(null, 3);
        ArrayList<Score> betterScores = calendar.obtenerPuntuaciones(MainActivity.this);
        managePermissions(calendar,"WRITE_CALENDAR");
        //calendar.obtenerPuntuaciones(MainActivity.this);

        if (betterScores.size() > 0) {
            ListView topScoreList = findViewById(R.id.top_score_list);
            topScoreList.setVisibility(View.VISIBLE);
            ScoreListAdapter topScoreAdapter = new ScoreListAdapter(this, betterScores);
            topScoreList.setAdapter(topScoreAdapter);
        } else {
            TextView emptyScoreList = findViewById(R.id.empty_score_list);
            emptyScoreList.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finishAffinity();
        System.exit(0);
    }

    // Creates Action Menu
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_action, menu);

        return true;
    }

    // Assign function to Action Menu options
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        int menuItemId = menuItem.getItemId();

        if (menuItemId == R.id.help_item) {
            Intent helpActivityIntent = new Intent(this, HelpActivity.class);
            startActivity(helpActivityIntent);
        }

        return super.onOptionsItemSelected(menuItem);
    }

    public void onClickPlayButton(View view) {
        Intent selectImageActivityIntent = new Intent(this, SelectImgActivity.class);
        startActivity(selectImageActivityIntent);
    }

    public void managePermissions(CalendarManager calendar, String permission){
        boolean result = calendar.checkPermissions(MainActivity.this,permission);
        if(!result){
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    permission)) {

                new AlertDialog.Builder(this)
                        .setTitle("Permission needed")
                        .setMessage("This permission is needed because of this and that")
                        .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ActivityCompat.requestPermissions(MainActivity.this,
                                        new String[] {Manifest.permission.READ_CALENDAR, Manifest.permission.WRITE_CALENDAR}, CALENDAR_PERMISSION_CODE);
                            }
                        })
                        .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .create().show();

            } else {
                ActivityCompat.requestPermissions(this,
                        new String[] {Manifest.permission.READ_CALENDAR, Manifest.permission.WRITE_CALENDAR}, CALENDAR_PERMISSION_CODE);
            }
        }
    }

}