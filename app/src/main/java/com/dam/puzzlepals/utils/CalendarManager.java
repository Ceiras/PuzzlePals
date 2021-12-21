package com.dam.puzzlepals.utils;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.pm.PackageManager;
import android.provider.CalendarContract;

import androidx.core.app.ActivityCompat;

import com.dam.puzzlepals.R;
import com.dam.puzzlepals.enums.Level;

public class CalendarManager {

    ContentResolver contentResolver;

    public CalendarManager(Context ctx) {
        contentResolver = ctx.getContentResolver();
    }

    public boolean checkPermissions(Context applicationContext, String permission) {
        return applicationContext.checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED;
    }

    public void addRecordEventToCalendar(Activity activity, Level level, String score) {
        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_CALENDAR) == PackageManager.PERMISSION_GRANTED) {
            long currentTime = System.currentTimeMillis();

            ContentValues values = new ContentValues();
            values.put(CalendarContract.Events.DTSTART, currentTime);
            values.put(CalendarContract.Events.DTEND, currentTime);
            values.put(CalendarContract.Events.TITLE, activity.getString(R.string.new_record_with_score_and_level, score, level));
            values.put(CalendarContract.Events.DESCRIPTION, activity.getString(R.string.puzzlepals_record));
            values.put(CalendarContract.Events.CALENDAR_ID, 3);
            values.put(CalendarContract.Events.EVENT_TIMEZONE, "Europe/Madrid");

            ContentResolver contentResolver = activity.getContentResolver();
            contentResolver.insert(CalendarContract.Events.CONTENT_URI, values);
        }
    }

}
