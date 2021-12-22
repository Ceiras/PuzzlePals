package com.dam.puzzlepals.database;

import com.dam.puzzlepals.enums.Level;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ScoresCollection {

    public static String SCORES_COLLECTION = "scores";

    private static final FirebaseFirestore database = FirebaseFirestore.getInstance();

    public static final String SCORES_COL_EMAIL = "email";
    public static final String SCORES_COL_DATE = "date";
    public static final String SCORES_COL_PUZZLE_NUMBER = "number";
    public static final String SCORES_COL_LEVEL = "level";
    public static final String SCORES_COL_SCORE = "score";

    public static Task<QuerySnapshot> getBestScores(int limit) {
        return database.collection(SCORES_COLLECTION)
                .orderBy(SCORES_COL_SCORE, Query.Direction.ASCENDING)
                .limit(limit)
                .get();
    }

    public static Task<QuerySnapshot> scoreIsBest(String email, Long puzzleNumber, Level level, Long score) {
        return database.collection(SCORES_COLLECTION)
                .whereEqualTo(SCORES_COL_EMAIL, email)
                .whereEqualTo(SCORES_COL_PUZZLE_NUMBER, puzzleNumber)
                .whereEqualTo(SCORES_COL_SCORE, level.toString())
                .whereLessThan(SCORES_COL_SCORE, score)
                .orderBy(SCORES_COL_SCORE, Query.Direction.DESCENDING)
                .limit(1)
                .get();
    }

    public static Task<DocumentReference> addScore(String email, Date date, Long puzzleNumber, Level level, Long score) {
        Map<String, Object> scoreMap = new HashMap<>();
        scoreMap.put(SCORES_COL_EMAIL, email);
        scoreMap.put(SCORES_COL_DATE, date);
        scoreMap.put(SCORES_COL_PUZZLE_NUMBER, puzzleNumber);
        scoreMap.put(SCORES_COL_SCORE, score);
        scoreMap.put(SCORES_COL_LEVEL, level.toString());

        return database.collection(SCORES_COLLECTION).add(scoreMap);
    }

}
