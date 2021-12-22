package com.dam.puzzlepals.utils;

import static androidx.core.content.ContextCompat.getSystemService;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.dam.puzzlepals.R;
import com.dam.puzzlepals.ScoreActivity;
import com.dam.puzzlepals.database.ScoresCollection;
import com.dam.puzzlepals.enums.Level;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.Date;
import java.util.UUID;

public class NotificationsManager {

    public static void createNotification(Context context, String email, Date date, Long puzzleNumber, Level level, Long score) {
        Handler createNotificationHandler = new Handler(Looper.getMainLooper());
        createNotificationHandler.post(() -> {
            ScoresCollection.scoreIsBest(email, puzzleNumber, level, score).addOnSuccessListener(command -> {
                boolean isRecord = command.getDocuments().size() == 0;

                if (isRecord) {
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
                    intent.putExtra("date", TimeConverter.convertDateToCompleteFormatDate(date));
                    intent.putExtra("score", TimeConverter.convertTimeMillisToReadableString(score));
                    intent.putExtra("level", level.toString());
                    intent.putExtra("number", puzzleNumber);
                    PendingIntent pendingIntent = PendingIntent.getActivity(context, UUID.randomUUID().hashCode(), intent, PendingIntent.FLAG_UPDATE_CURRENT);

                    NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "CHANNEL_NEW_RECORD")
                            .setSmallIcon(R.mipmap.ic_launcher)
                            .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher))
                            .setContentTitle(context.getString(R.string.app_name))
                            .setContentText(context.getString(R.string.new_record_with_score, TimeConverter.convertTimeMillisToReadableString(score)))
                            .setContentIntent(pendingIntent);
                    NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);

                    notificationManagerCompat.notify(1, builder.build());
                }
            }).addOnFailureListener(command -> {
                Toast.makeText(context, R.string.get_from_database_error, Toast.LENGTH_LONG).show();
            });
        });
    }

}
