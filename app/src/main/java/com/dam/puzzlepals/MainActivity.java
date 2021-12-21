package com.dam.puzzlepals;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.dam.puzzlepals.enums.LoginMethod;
import com.dam.puzzlepals.enums.MusicPlayer;
import com.dam.puzzlepals.models.Score;
import com.dam.puzzlepals.services.BackgroundMusicService;
import com.dam.puzzlepals.sqlite.ScoreAPI;
import com.dam.puzzlepals.ui.HelpActivity;
import com.dam.puzzlepals.ui.ScoreListAdapter;
import com.dam.puzzlepals.ui.SelectImgActivity;
import com.dam.puzzlepals.ui.SettingsActivity;
import com.dam.puzzlepals.utils.CalendarManager;
import com.dam.puzzlepals.utils.FirebaseEvents;
import com.dam.puzzlepals.utils.PermissionManger;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private final String PREFS_FILE = "com.dam.puzzlepals.PREFERENCE_FILE";
    private final String LOGIN_EMAIL = "email";
    private final String LOGIN_NAME = "name";
    private final String DEFAULT_WEB_CLIENT_ID = "883037560613-6krtm86bgm757843sclkh5a9jn0bbm42.apps.googleusercontent.com";

    private ActivityResultLauncher<Intent> googleSignInLauncher;
    private LinearLayout userLogged;
    private Button loginButton;
    private Button playButton;
    private TextView emailUserLogged;
    private TextView nameUserLogged;
    private boolean isLogged;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        userLogged = findViewById(R.id.user_logged_layout);
        loginButton = findViewById(R.id.singin_google_btn);
        playButton = findViewById(R.id.play_btn);
        emailUserLogged = findViewById(R.id.user_logged_email_text);
        nameUserLogged = findViewById(R.id.user_logged_name_text);

        phoneCallListener();

        CalendarManager calendar = new CalendarManager(MainActivity.this);
        PermissionManger.manageCalendarPermissions(MainActivity.this, MainActivity.this, calendar, Manifest.permission.READ_CALENDAR);

        Handler getBetterScoresHandler = new Handler(Looper.getMainLooper());
        getBetterScoresHandler.post(this::getBetterScores);

        Handler checkLoginDataHandler = new Handler(Looper.getMainLooper());
        checkLoginDataHandler.post(this::checkLoginData);

        googleSignInLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                Task<GoogleSignInAccount> googleSignInAccountTask = GoogleSignIn.getSignedInAccountFromIntent(result.getData());
                try {
                    GoogleSignInAccount googleSignInAccount = googleSignInAccountTask.getResult(ApiException.class);
                    if (googleSignInAccount != null) {
                        AuthCredential authCredential = GoogleAuthProvider.getCredential(googleSignInAccount.getIdToken(), null);
                        FirebaseAuth.getInstance().signInWithCredential(authCredential)
                                .addOnCompleteListener(task -> {
                                    if (task.isSuccessful()) {
                                        Handler saveLoginDataHandler = new Handler(Looper.getMainLooper());
                                        saveLoginDataHandler.post(() -> {
                                            if (task.getResult().getUser() != null) {
                                                MainActivity.this.saveLoginData(task.getResult().getUser());
                                            }
                                        });

                                        userLogged.setVisibility(View.VISIBLE);
                                        loginButton.setVisibility(View.INVISIBLE);

                                        FirebaseEvents.loginEvent(MainActivity.this, LoginMethod.GOOGLE);
                                    } else {
                                        Toast.makeText(MainActivity.this, R.string.login_fail, Toast.LENGTH_LONG).show();
                                    }
                                });
                    }
                } catch (ApiException e) {
                    Toast.makeText(MainActivity.this, R.string.login_fail, Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void checkLoginData() {
        SharedPreferences loginSharedPreferences = getSharedPreferences(PREFS_FILE, Context.MODE_PRIVATE);
        String loginEmail = loginSharedPreferences.getString(LOGIN_EMAIL, null);
        String loginName = loginSharedPreferences.getString(LOGIN_NAME, null);

        if (loginEmail != null && loginName != null) {
            emailUserLogged.setText(loginEmail);
            nameUserLogged.setText(loginName);
            userLogged.setVisibility(View.VISIBLE);
            loginButton.setVisibility(View.INVISIBLE);
        }
    }

    private void getBetterScores() {
        ScoreAPI scoreAPI = new ScoreAPI(this);
        ArrayList<Score> betterScores = scoreAPI.getBetterScores(null, 3);

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

    private void phoneCallListener() {
        TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        PhoneStateListener phoneStateListener = new PhoneStateListener() {
            @Override
            public void onCallStateChanged(int state, String phoneNumber) {
                super.onCallStateChanged(state, phoneNumber);
                Intent backgroundMusicServiceStopIntent = new Intent(MainActivity.this, BackgroundMusicService.class);
                if (state == 1) {
                    backgroundMusicServiceStopIntent.setAction(MusicPlayer.PAUSE.toString());
                } else {
                    backgroundMusicServiceStopIntent.setAction(MusicPlayer.PLAY.toString());
                }
                startService(backgroundMusicServiceStopIntent);
            }
        };
        telephonyManager.listen(phoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);
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

    // Assign activity to Action Menu options
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        int menuItemId = menuItem.getItemId();

        if (menuItemId == R.id.help_item) {
            Intent helpActivityIntent = new Intent(this, HelpActivity.class);
            startActivity(helpActivityIntent);
        } else if (menuItemId == R.id.settings_item) {
            Intent settingsActivityIntent = new Intent(this, SettingsActivity.class);
            startActivity(settingsActivityIntent);
        }

        return super.onOptionsItemSelected(menuItem);
    }

    public void onClickSingInWithGoogle(View view) {
        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(DEFAULT_WEB_CLIENT_ID)
                .requestEmail()
                .build();

        GoogleSignInClient googleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions);
        googleSignInClient.signOut();

        Intent googleSignInIntent = new Intent(googleSignInClient.getSignInIntent());
        googleSignInLauncher.launch(googleSignInIntent);
    }

    private void saveLoginData(FirebaseUser firebaseUser) {
        SharedPreferences.Editor loginSharedPreferences = getSharedPreferences(PREFS_FILE, Context.MODE_PRIVATE).edit();
        loginSharedPreferences.putString(LOGIN_EMAIL, firebaseUser.getEmail());
        loginSharedPreferences.putString(LOGIN_NAME, firebaseUser.getDisplayName());
        loginSharedPreferences.apply();

        emailUserLogged.setText(firebaseUser.getEmail());
        nameUserLogged.setText(firebaseUser.getDisplayName());
    }

    public void onClickSingOut(View view) {
        SharedPreferences.Editor loginSharedPreferences = getSharedPreferences(PREFS_FILE, Context.MODE_PRIVATE).edit();
        loginSharedPreferences.clear();
        loginSharedPreferences.apply();

        FirebaseAuth.getInstance().signOut();

        loginButton.setVisibility(View.VISIBLE);
        userLogged.setVisibility(View.INVISIBLE);

        FirebaseEvents.singOutEvent(this, LoginMethod.GOOGLE);
    }

    public void onClickPlayButton(View view) {
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            FirebaseEvents.startGameEvent(this);
            Intent selectImageActivityIntent = new Intent(this, SelectImgActivity.class);
            startActivity(selectImageActivityIntent);
        } else {
            Toast.makeText(MainActivity.this, R.string.must_be_authenticated, Toast.LENGTH_SHORT).show();
        }
    }

}