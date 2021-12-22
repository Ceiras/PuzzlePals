package com.dam.puzzlepals.database;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

public class UsersCollection {

    public static String USERS_COLLECTION = "users";

    private static final FirebaseFirestore database = FirebaseFirestore.getInstance();

    public static final String USERS_COL_EMAIL = "email";
    public static final String USERS_COL_NAME = "name";
    public static final String USERS_COL_PUZZLE_NUMBER = "number";

    public static Task<QuerySnapshot> getUser(String email) {
        return database.collection(USERS_COLLECTION).whereEqualTo(USERS_COL_EMAIL, email).get();
    }

    public static void saveUser(String email, String name, Long puzzleNumber) {
        Map<String, Object> user = new HashMap<>();
        user.put(USERS_COL_EMAIL, email);
        user.put(USERS_COL_NAME, name);
        user.put(USERS_COL_PUZZLE_NUMBER, puzzleNumber);

        database.collection(USERS_COLLECTION).document(email).set(user);
    }

}
