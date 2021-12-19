package com.dam.puzzlepals.calendar;

import static androidx.core.content.ContextCompat.getSystemService;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.provider.CalendarContract;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.dam.puzzlepals.MainActivity;
import com.dam.puzzlepals.R;
import com.dam.puzzlepals.enums.Level;
import com.dam.puzzlepals.models.Score;
import com.dam.puzzlepals.ui.PuzzleActivity;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class CalendarManager {

    ContentResolver contentResolver;

    public CalendarManager(Context ctx) {
        contentResolver = ctx.getContentResolver();
    }

    public boolean checkPermissions(Context applicationContext, String permission) {
        return applicationContext.checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED;
    }

    // Este método agrega un evento al calendario si se ha producido un récord
    // y en ese caso además envía una notificación.
    public void addRecordEvenToCalendar(PuzzleActivity puzzleActivity, Level nivel, double tiempo, String score) {
        if (checkRecords(puzzleActivity, nivel, tiempo)) {
            long currentTime = System.currentTimeMillis();
            ContentResolver cr = puzzleActivity.getContentResolver();
            ContentValues values = new ContentValues();

            values.put(CalendarContract.Events.DTSTART, currentTime);
            values.put(CalendarContract.Events.DTEND, currentTime);
            values.put(CalendarContract.Events.TITLE, puzzleActivity.getString(R.string.new_record_with_score_and_level, score, nivel));
            values.put(CalendarContract.Events.DESCRIPTION, puzzleActivity.getString(R.string.puzzlepals_record));
            values.put(CalendarContract.Events.CALENDAR_ID, 3);
            values.put(CalendarContract.Events.EVENT_TIMEZONE, "Europe/Madrid");

            // Comprobamos si tenemos permisos de acceso al calendario
            if (ActivityCompat.checkSelfPermission(puzzleActivity, android.Manifest.permission.WRITE_CALENDAR) != PackageManager.PERMISSION_GRANTED) {
                return;
            }

            cr.insert(CalendarContract.Events.CONTENT_URI, values);

            // Envía notificación
            newNotification(puzzleActivity);

            NotificationCompat.Builder builder = new NotificationCompat.Builder(puzzleActivity, "CHANNEL_NEW_RECORD")
                    .setSmallIcon(R.drawable.puzzle_pals_icon)
                    .setContentTitle(puzzleActivity.getString(R.string.app_name))
                    .setContentText(puzzleActivity.getString(R.string.new_record_with_score, score));
            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(puzzleActivity);

            notificationManager.notify(1, builder.build());
        }
    }

    // Este método comprueba si hay registros de la aplicación en el calendario
    public boolean checkRecords(PuzzleActivity puzzleActivity, Level nivel, double tiempo) {
        ContentResolver contentResolver = puzzleActivity.getContentResolver();
        Uri.Builder builder = CalendarContract.Instances.CONTENT_URI.buildUpon();

        Calendar beginTime = Calendar.getInstance();
        beginTime.set(2000, Calendar.JANUARY, 1, 0, 0);
        long startMills = beginTime.getTimeInMillis();
        long endMills = System.currentTimeMillis();
        String titulo = puzzleActivity.getString(R.string.puzzlepals_record);

        ContentUris.appendId(builder, startMills);
        ContentUris.appendId(builder, endMills);
        String[] args = new String[]{"3", titulo};

        Cursor eventCursor = contentResolver.query(
                builder.build(),
                new String[]{
                        CalendarContract.Instances.TITLE,
                        CalendarContract.Instances.BEGIN,
                        CalendarContract.Instances.END,
                        CalendarContract.Instances.DESCRIPTION
                },
                CalendarContract.Instances.CALENDAR_ID + " = ? AND " + CalendarContract.Instances.DESCRIPTION + " = ?",
                args,
                null
        );

        boolean isRecord = false;
        boolean hayRegistros = false;

        while (eventCursor.moveToNext()) {
            final String title = eventCursor.getString(0);

            if (title.contains(puzzleActivity.getString(R.string.new_record_control_flag))) {
                int begin = puzzleActivity.getString(R.string.new_record_control_flag).length() + 1;
                int last = title.indexOf(",");
                String time = title.substring(begin, last);
                time = time.replace("s ", "");
                time = time.replace("m", "");
                hayRegistros = true;
                isRecord = tiempo < Double.parseDouble(time.replace(",", "."));
            }
        }

        eventCursor.close();

        if (hayRegistros) {
            return isRecord;
        } else {
            return !isRecord;
        }
    }

    // Este método crea el canal que permite enviar las notificaciones de los récords.
    private void newNotification(Context activity) {
        CharSequence name = activity.getResources().getString(R.string.channel_name);
        String description = activity.getResources().getString(R.string.channel_description);
        int importance = NotificationManager.IMPORTANCE_HIGH;
        NotificationChannel channel = new NotificationChannel("CHANNEL_NEW_RECORD", name, importance);
        channel.setDescription(description);
        NotificationManager notificationManager = getSystemService(activity, NotificationManager.class);
        if (notificationManager != null) {
            notificationManager.createNotificationChannel(channel);
        }
    }

}
