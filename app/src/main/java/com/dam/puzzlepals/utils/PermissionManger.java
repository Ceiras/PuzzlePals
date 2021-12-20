package com.dam.puzzlepals.utils;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;

import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;

import com.dam.puzzlepals.R;
import com.dam.puzzlepals.calendar.CalendarManager;

public class PermissionManger {

    private static final int CALENDAR_PERMISSION_CODE = 1;

    public static void managePermissions(Activity activity, Context context, CalendarManager calendar, String permission) {
        boolean result = calendar.checkPermissions(context, permission);
        if (!result) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)) {
                new AlertDialog.Builder(context)
                        .setTitle(R.string.calendar_permission_required)
                        .setMessage(R.string.calendar_permission_required_description)
                        .setPositiveButton(R.string.agree, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ActivityCompat.requestPermissions(
                                        activity,
                                        new String[]{
                                                Manifest.permission.READ_CALENDAR,
                                                Manifest.permission.WRITE_CALENDAR
                                        },
                                        CALENDAR_PERMISSION_CODE
                                );
                            }
                        })
                        .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .create().show();

            } else {
                ActivityCompat.requestPermissions(
                        activity,
                        new String[]{
                                Manifest.permission.READ_CALENDAR,
                                Manifest.permission.WRITE_CALENDAR
                        },
                        CALENDAR_PERMISSION_CODE
                );
            }
        }
    }

}
