package com.dam.puzzlepals.database;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

public class ImagesCollection {

    public static String IMAGES_COLLECTION = "images";

    private static final FirebaseFirestore database = FirebaseFirestore.getInstance();

    public static final String IMAGES_COL_NUMBER = "number";
    public static final String IMAGES_COL_DATA = "data";

    public static Task<QuerySnapshot> getImage(Long level) {
        return database.collection(IMAGES_COLLECTION).whereEqualTo(IMAGES_COL_NUMBER, level).get();
    }

}
