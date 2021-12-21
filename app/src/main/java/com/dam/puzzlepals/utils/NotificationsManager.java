package com.dam.puzzlepals.utils;

import static androidx.core.content.ContextCompat.getSystemService;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.dam.puzzlepals.R;
import com.dam.puzzlepals.ScoreActivity;
import com.dam.puzzlepals.models.Score;
import com.dam.puzzlepals.sqlite.ScoreAPI;

import java.util.ArrayList;
import java.util.UUID;

public class NotificationsManager {

    public static void createNotification(Context context, Score score, Long puzzleNumber) {
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
        intent.putExtra("number", puzzleNumber);
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

    public static boolean isRecord(Context context, long score) {
        ScoreAPI scoreAPI = new ScoreAPI(context);
        ArrayList<Score> betterScores = scoreAPI.getBetterScores(null, 1);
        Score bestScore = betterScores.size() >= 1 ? betterScores.get(0) : null;

        boolean isRecord = bestScore == null;
        if (bestScore != null && score < bestScore.getScore()) {
            isRecord = true;
        }
        return isRecord;
    }

}
