package com.dam.puzzlepals.calendar;

import static androidx.core.content.ContextCompat.getSystemService;

import android.Manifest;
import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.provider.CalendarContract;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.dam.puzzlepals.R;
import com.dam.puzzlepals.ScoreActivity;
import com.dam.puzzlepals.enums.Level;
import com.dam.puzzlepals.models.Score;
import com.dam.puzzlepals.utils.TimeConverter;

import java.util.UUID;

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

    public void createNotification(Context context, Score score, String path) {
        CharSequence name = context.getResources().getString(R.string.channel_name);
        String description = context.getResources().getString(R.string.channel_description);
        int importance = NotificationManager.IMPORTANCE_HIGH;
        NotificationChannel channel = new NotificationChannel("CHANNEL_NEW_RECORD", name, importance);
        channel.setDescription(description);
        NotificationManager notificationManager = getSystemService(context, NotificationManager.class);
        if (notificationManager != null) {
            notificationManager.createNotificationChannel(channel);
        }

        Intent intent = new Intent(context, ScoreActivity.class);
        intent.putExtra("date", TimeConverter.convertDateToCompleteFormatDate(score.getDate()));
        intent.putExtra("score", TimeConverter.convertTimeMillisToReadableString(score.getScore()));
        intent.putExtra("level", score.getLevel().toString());
        intent.putExtra("image", path);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, UUID.randomUUID().hashCode(), intent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "CHANNEL_NEW_RECORD")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher))
                .setContentTitle(context.getString(R.string.app_name))
                .setContentText(context.getString(R.string.new_record_with_score, TimeConverter.convertTimeMillisToReadableString(score.getScore())))
                .setContentIntent(pendingIntent);
        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);

        notificationManagerCompat.notify(1, builder.build());
    }

}
