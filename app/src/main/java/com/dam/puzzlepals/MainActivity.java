package com.dam.puzzlepals;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
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
import androidx.core.app.ActivityCompat;

import com.dam.puzzlepals.database.ImagesCollection;
import com.dam.puzzlepals.database.ScoresCollection;
import com.dam.puzzlepals.database.UsersCollection;
import com.dam.puzzlepals.enums.Level;
import com.dam.puzzlepals.enums.LoginMethod;
import com.dam.puzzlepals.enums.MusicPlayer;
import com.dam.puzzlepals.holders.PuzzleHolder;
import com.dam.puzzlepals.models.Score;
import com.dam.puzzlepals.models.User;
import com.dam.puzzlepals.services.BackgroundMusicService;
import com.dam.puzzlepals.ui.HelpActivity;
import com.dam.puzzlepals.ui.PersonalScoreActivity;
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
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    private final String DEFAULT_WEB_CLIENT_ID = "883037560613-6krtm86bgm757843sclkh5a9jn0bbm42.apps.googleusercontent.com";

    private final String PREFS_FILE = "com.dam.puzzlepals.PREFERENCE_FILE";
    private final String LOGIN_EMAIL = "email";
    private final String LOGIN_NAME = "name";

    ActivityResultLauncher<Intent> googleSignInLauncher;

    @BindView(R.id.user_logged_layout)
    LinearLayout userLogged;
    @BindView(R.id.singin_google_btn)
    Button loginButton;
    @BindView(R.id.user_logged_email_text)
    TextView emailUserLogged;
    @BindView(R.id.user_logged_name_text)
    TextView nameUserLogged;

    MenuItem personalScoresMenuItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        phoneCallListener();

        CalendarManager calendar = new CalendarManager(MainActivity.this);
        PermissionManger.manageCalendarPermissions(MainActivity.this, MainActivity.this, calendar, Manifest.permission.READ_CALENDAR);

        Handler checkLoginDataHandler = new Handler(Looper.getMainLooper());
        checkLoginDataHandler.post(this::checkLoginData);

        Handler locationHandler = new Handler(Looper.getMainLooper());
        locationHandler.post(this::setUpLanguageFromLocation);

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

    private void checkLoginData() {
        SharedPreferences loginSharedPreferences = getSharedPreferences(PREFS_FILE, Context.MODE_PRIVATE);
        String loginEmail = loginSharedPreferences.getString(LOGIN_EMAIL, null);
        String loginName = loginSharedPreferences.getString(LOGIN_NAME, null);

        Handler saveUserHandler = new Handler(Looper.getMainLooper());
        saveUserHandler.post(() -> {
            saveUser(loginEmail, loginName);
        });

        if (loginEmail != null && loginName != null) {
            emailUserLogged.setText(loginEmail);
            nameUserLogged.setText(loginName);
            userLogged.setVisibility(View.VISIBLE);
            loginButton.setVisibility(View.INVISIBLE);
        }
    }

    private void setUpLanguageFromLocation() {
        FusedLocationProviderClient fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationProviderClient.getLastLocation().addOnCompleteListener(command -> {
                Location location = command.getResult();
                if (location != null) {
                    Geocoder geocoder = new Geocoder(this, Locale.getDefault());
                    try {
                        List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                        String countryCode = addresses.get(0).getCountryCode();

                        Resources resources = this.getResources();
                        DisplayMetrics displayMetrics = resources.getDisplayMetrics();
                        Configuration configuration = resources.getConfiguration();
                        configuration.locale = new Locale(countryCode.toLowerCase());
                        resources.updateConfiguration(configuration, displayMetrics);
                        FirebaseEvents.location(this, countryCode);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 44);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        this.setUpLanguageFromLocation();

        Handler getBetterScoresHandler = new Handler(Looper.getMainLooper());
        getBetterScoresHandler.post(this::getBetterScores);
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

        personalScoresMenuItem = menu.findItem(R.id.player_score_item);
        personalScoresMenuItem.setVisible(false);

        return true;
    }

    // Assign activity to Action Menu options
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        int menuItemId = menuItem.getItemId();

        if (menuItemId == R.id.help_item) {
            Intent helpActivityIntent = new Intent(this, HelpActivity.class);
            startActivity(helpActivityIntent);
        } else if (menuItemId == R.id.player_score_item) {
            Intent personalScoreActivityIntent = new Intent(this, PersonalScoreActivity.class);
            startActivity(personalScoreActivityIntent);
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

        Handler saveUserHandler = new Handler(Looper.getMainLooper());
        saveUserHandler.post(() -> {
            saveUser(firebaseUser.getEmail(), firebaseUser.getDisplayName());
        });

        emailUserLogged.setText(firebaseUser.getEmail());
        nameUserLogged.setText(firebaseUser.getDisplayName());
    }

    private void saveUser(String email, String name) {
        if (email != null && name != null) {
            UsersCollection.getUser(email).addOnSuccessListener(command -> {
                User user = new User();
                if (command.getDocuments().size() == 1) {
                    Long puzzleNumber = command.getDocuments().get(0).getLong(UsersCollection.USERS_COL_PUZZLE_NUMBER);

                    UsersCollection.saveUser(email, name, puzzleNumber);

                    user.setEmail(email);
                    user.setName(name);
                    user.setPuzzleNumber(puzzleNumber);
                } else {
                    UsersCollection.saveUser(email, name, 1L);

                    user.setEmail(email);
                    user.setName(name);
                    user.setPuzzleNumber(1L);
                }

                PuzzleHolder.getInstance().setUser(user);
                personalScoresMenuItem.setVisible(true);
            }).addOnFailureListener(command -> {
                Toast.makeText(this, R.string.get_from_database_error, Toast.LENGTH_LONG).show();
            });
        }
    }

    private void getBetterScores() {
        ScoresCollection.getBestScores(10).addOnSuccessListener(command -> {
            List<Score> betterScores = command.getDocuments().stream().map(documentSnapshot -> {
                Date date = documentSnapshot.getDate(ScoresCollection.SCORES_COL_DATE);
                String player = documentSnapshot.getString(ScoresCollection.SCORES_COL_EMAIL);
                Level level = Level.valueOf(documentSnapshot.getString(ScoresCollection.SCORES_COL_LEVEL));
                Long puzzleNumber = documentSnapshot.getLong(ScoresCollection.SCORES_COL_PUZZLE_NUMBER);
                Long score = documentSnapshot.getLong(ScoresCollection.SCORES_COL_SCORE);

                return new Score(date, player, level, puzzleNumber, score);
            }).collect(Collectors.toList());

            if (betterScores.size() > 0) {
                ListView topScoreList = findViewById(R.id.top_score_list);
                topScoreList.setVisibility(View.VISIBLE);
                ScoreListAdapter topScoreAdapter = new ScoreListAdapter(this, betterScores);
                topScoreList.setAdapter(topScoreAdapter);
            } else {
                TextView emptyScoreList = findViewById(R.id.empty_score_list);
                emptyScoreList.setVisibility(View.VISIBLE);
            }
        });
    }

    public void onClickSingOut(View view) {
        SharedPreferences.Editor loginSharedPreferences = getSharedPreferences(PREFS_FILE, Context.MODE_PRIVATE).edit();
        loginSharedPreferences.clear();
        loginSharedPreferences.apply();

        FirebaseAuth.getInstance().signOut();
        PuzzleHolder.getInstance().setUser(null);

        loginButton.setVisibility(View.VISIBLE);
        userLogged.setVisibility(View.INVISIBLE);
        personalScoresMenuItem.setVisible(false);

        FirebaseEvents.singOutEvent(this, LoginMethod.GOOGLE);
    }

    public void onClickPlayButton(View view) {
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            ImagesCollection.getImages().addOnSuccessListener(command -> {
                if (command.getDocuments().size() < PuzzleHolder.getInstance().getUser().getPuzzleNumber()) {
                    final Dialog finishDialog = new Dialog(this, android.R.style.Theme_Black_NoTitleBar);
                    finishDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.argb(100, 0, 0, 0)));
                    finishDialog.setContentView(R.layout.end_game_dialogue);
                    finishDialog.setCancelable(true);

                    Button closeButton = (Button) finishDialog.findViewById(R.id.close_btn);
                    closeButton.setOnClickListener(dialogView -> {
                        finishDialog.dismiss();
                    });

                    Button startAgainButton = (Button) finishDialog.findViewById(R.id.start_again_btn);
                    startAgainButton.setOnClickListener(dialogView -> {
                        User user = PuzzleHolder.getInstance().getUser();
                        UsersCollection.saveUser(user.getEmail(), user.getName(), 1L);

                        user.setPuzzleNumber(1L);
                        PuzzleHolder.getInstance().setUser(user);

                        finishDialog.dismiss();

                        FirebaseEvents.startGameEvent(this);
                        Intent selectImageActivityIntent = new Intent(this, SelectImgActivity.class);
                        startActivity(selectImageActivityIntent);
                    });

                    finishDialog.show();
                } else {
                    FirebaseEvents.startGameEvent(this);
                    Intent selectImageActivityIntent = new Intent(this, SelectImgActivity.class);
                    startActivity(selectImageActivityIntent);
                }
            });
        } else {
            Toast.makeText(MainActivity.this, R.string.must_be_authenticated, Toast.LENGTH_SHORT).show();
        }
    }

}