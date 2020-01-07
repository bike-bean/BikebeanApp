package de.bikebean.app;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;

import androidx.core.app.ActivityCompat;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class Utils {

    public static boolean isWarningNumberSet(SharedPreferences sharedPreferences) {
        String w = sharedPreferences.getString("warningNumber", "");
        boolean b = sharedPreferences.getBoolean("askedForWarningNumber", true);

        return !w.isEmpty() || b;
    }

    public static boolean hasNoPermissions(Activity activity, String... permissions) {
        if (activity != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(activity, permission)
                        != PackageManager.PERMISSION_GRANTED) {
                    return true;
                }
            }
        }
        return false;
    }

    public static String getTimestamp() {
        return new SimpleDateFormat("dd.MM.yyyy HH:mm:ss", Locale.GERMANY).format(
                Calendar.getInstance().getTime()
        );
    }

    public static String convertToTime(long datetime) {
        Date date = new Date(datetime);
        DateFormat formatter = new SimpleDateFormat("dd.MM HH:mm", Locale.GERMANY);
        return formatter.format(date);
    }
}