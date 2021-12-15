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
import android.os.Build;
import android.provider.CalendarContract;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.dam.puzzlepals.MainActivity;
import com.dam.puzzlepals.R;
import com.dam.puzzlepals.enums.Level;
import com.dam.puzzlepals.models.Score;
import com.dam.puzzlepals.ui.PuzzleActivity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

public class CalendarManager {
    public static final Uri CALENDAR_URI = Uri.parse("content://com.android.calendar/calendars");

    ContentResolver contentResolver;
    Set<String> calendars = new HashSet<String>();
    // Projection array. Creating indices for this array instead of doing
    // dynamic lookups improves performance.
    public static final String[] EVENT_PROJECTION = new String[] {
            CalendarContract.Calendars._ID,                           // 0
            CalendarContract.Calendars.ACCOUNT_NAME,                  // 1
            CalendarContract.Calendars.CALENDAR_DISPLAY_NAME,         // 2
            CalendarContract.Calendars.OWNER_ACCOUNT                  // 3
    };
    public CalendarManager(Context ctx){
        contentResolver = ctx.getContentResolver();
    }



    // The indices for the projection array above.
    private static final int PROJECTION_ID_INDEX = 0;
    private static final int PROJECTION_ACCOUNT_NAME_INDEX = 1;
    private static final int PROJECTION_DISPLAY_NAME_INDEX = 2;
    private static final int PROJECTION_OWNER_ACCOUNT_INDEX = 3;

    public boolean checkPermissions(Context applicationContext, String permission){

        int result = applicationContext.checkSelfPermission(permission);
        boolean respond;

        respond = result == PackageManager.PERMISSION_GRANTED;

        return respond;

    }

    public ArrayList<Score> obtenerPuntuaciones(MainActivity activity) {

        ArrayList<Score> scores = new ArrayList<>();

        //for (int i = 0; i < 5; i++) {
            ContentResolver contentResolver = activity.getContentResolver();
            Uri.Builder builder = CalendarContract.Instances.CONTENT_URI.buildUpon();

            Calendar beginTime = Calendar.getInstance();
            beginTime.set(2000, Calendar.JANUARY, 1, 0, 0);
            long startMills = beginTime.getTimeInMillis();
            long endMills = System.currentTimeMillis();

            String titulo = "Puzzlepals record";
            ContentUris.appendId(builder, startMills);
            ContentUris.appendId(builder, endMills);
            String[] args = new String[]{"3", titulo};

            // Seleccionamos del Calendario aquellas entradas que coinciden con las puntuaciones de nuestro juego
            Cursor eventCursor = contentResolver.query(builder.build(), new String[]{CalendarContract.Instances.TITLE,
                            CalendarContract.Instances.BEGIN, CalendarContract.Instances.END, CalendarContract.Instances.DESCRIPTION},
                    CalendarContract.Instances.CALENDAR_ID + " = ? AND " + CalendarContract.Instances.DESCRIPTION + " = ?", args, null);


            if (eventCursor.getCount() > 0) {
                double min = Double.MAX_VALUE;
                Date minDate = new Date();

                eventCursor.moveToLast();
                final String title = eventCursor.getString(0);
                final Date begin = new Date(eventCursor.getLong(1));
                final Date end = new Date(eventCursor.getLong(2));


                int first = "¡Nuevo récord: ".length();
                int last = title.indexOf(",");
                String time = title.substring(first,last);
                time = time.replace("s ","");
                time = time.replace("m","");

                long score = Long.parseLong(time);

                int id = 4;

                Level level = null;

                if(title.contains("EASY")) {
                    level = Level.EASY;
                }else {
                    level = Level.HARD;
                }


                scores.add(new Score(id, begin, score, level));
                //for(int i = 0; i < 3; ++i){
                int id1 = 0;
                while (eventCursor.moveToPrevious()) {
                    if(id >= 3){
                        ++id;
                        final String title1 = eventCursor.getString(0);
                        final Date begin1 = new Date(eventCursor.getLong(1));
                        final Date end1 = new Date(eventCursor.getLong(2));


                        int last1 = title1.indexOf(",");
                        String time1 = title1.substring(first,last1);
                        time1 = time1.replace("s ","");
                        time1 = time1.replace("m","");

                        long score1 = Long.parseLong(time1);


                        Level level1 = null;

                        if(title1.contains("EASY")) {
                            level1 = Level.EASY;
                        }else {
                            level1 = Level.HARD;
                        }

                        scores.add(new Score(id1, begin1, score1, level1));
                    }
                }

            }

        return scores;
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
            values.put(CalendarContract.Events.TITLE, "¡Nuevo récord: " + score  + " ," + nivel + " level" + "!");
            values.put(CalendarContract.Events.DESCRIPTION, "Puzzlepals record");
            values.put(CalendarContract.Events.CALENDAR_ID, 3);
            values.put(CalendarContract.Events.EVENT_TIMEZONE, "Europe/Madrid");

            // Comprobamos si tenemos permisos de acceso al calendario
            if (ActivityCompat.checkSelfPermission(puzzleActivity, android.Manifest.permission.WRITE_CALENDAR) != PackageManager.PERMISSION_GRANTED) {
                return;
            }

            Uri uri = cr.insert(CalendarContract.Events.CONTENT_URI, values);

            // Envía notificación
            newNotification( puzzleActivity );

            String record = score;
            NotificationCompat.Builder builder = new NotificationCompat.Builder(puzzleActivity, "CHANNEL_NEW_RECORD")
                    .setSmallIcon(R.drawable.puzzle_pals_icon)
                    .setContentTitle("PuzzlePals")
                    .setContentText("¡Nuevo récord: " + record +"!");

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
        String titulo = "Puzzlepals record";

        ContentUris.appendId(builder, startMills);
        ContentUris.appendId(builder, endMills);
        String[] args = new String[]{"3", titulo};

        Cursor eventCursor = contentResolver.query(builder.build(), new String[]{CalendarContract.Instances.TITLE,
                        CalendarContract.Instances.BEGIN, CalendarContract.Instances.END, CalendarContract.Instances.DESCRIPTION},
                CalendarContract.Instances.CALENDAR_ID + " = ? AND " + CalendarContract.Instances.DESCRIPTION + " = ?" , args, null);

        boolean isRecord = false;
        boolean hayRegistros = false;

        while (eventCursor.moveToNext()) {
            final String title = eventCursor.getString(0);

            if (title.contains("¡Nuevo récord")) {
                int begin = "¡Nuevo récord: ".length();
                int last = title.indexOf(",");
                String time = title.substring(begin,last);
                time = time.replace("s ","");
                time = time.replace("m","");
                hayRegistros = true;
                if (tiempo < Double.parseDouble(time.replace(",", "."))) {
                    isRecord = true;
                } else {
                    isRecord = false;
                }
            }
        }

        if (hayRegistros) {
            return isRecord;
        } else {
            return !isRecord;
        }
    }

    // Este método crea el canal que permite enviar las notificaciones de los récords.
    private void newNotification(Context activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = activity.getResources().getString(R.string.channel_name);
            String description = activity.getResources().getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel("CHANNEL_NEW_RECORD", name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = getSystemService(activity, NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }


}
