package com.dam.puzzlepals.database;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

public class ImagesCollection {

    public static String IMAGES_COLLECTION = "images";

    private static final FirebaseFirestore database = FirebaseFirestore.getInstance();

    public static final String IMAGES_COL_PUZZLE_NUMBER = "number";
    public static final String IMAGES_COL_DATA = "data";

    public static Task<QuerySnapshot> getImage(Long puzzleNumber) {
        return database.collection(IMAGES_COLLECTION).whereEqualTo(IMAGES_COL_PUZZLE_NUMBER, puzzleNumber).get();
    }

    public static Task<QuerySnapshot> getImages() {
        return database.collection(IMAGES_COLLECTION).get();
    }

    public static void saveImage(Long puzzleNumber, String base64Image) {
        Map<String, Object> image = new HashMap<>();
        image.put(IMAGES_COL_PUZZLE_NUMBER, puzzleNumber);
        image.put(IMAGES_COL_DATA, base64Image);

        database.collection(IMAGES_COLLECTION).add(image);
    }

}
