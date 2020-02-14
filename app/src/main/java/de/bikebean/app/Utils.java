package de.bikebean.app;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import de.bikebean.app.db.state.State;
import de.bikebean.app.ui.status.StateViewModel;

public class Utils {

    public static final int PERMISSION_KEY_SMS = 1;
    public static final int PERMISSION_KEY_MAPS = 2;

    private static final Map<Integer, Double> batteryRuntimeByInterval =
            new HashMap<Integer, Double>() {{
                put(1, 175.0);
                put(2, 260.0);
                put(4, 346.0);
                put(8, 415.0);
                put(12, 444.0);
                put(24, 477.0);
    }};

    private static final String[] smsPermissions = {
            android.Manifest.permission.READ_SMS,
            android.Manifest.permission.SEND_SMS,
            android.Manifest.permission.RECEIVE_SMS
    };

    private static final String[] mapsPermissions = {
            Manifest.permission.ACCESS_FINE_LOCATION
    };

    private static final Map<Integer, String[]> permissionMap =
            new HashMap<Integer, String[]>() {{
                put(PERMISSION_KEY_SMS, smsPermissions);
                put(PERMISSION_KEY_MAPS, mapsPermissions);
    }};

    public interface RationaleShower {
        void showRationaleDialog();
    }

    public static boolean getPermissions(Activity activity, int permissionKey, RationaleShower r) {
        String[] permissions = permissionMap.get(permissionKey);

        if (activity == null || permissions == null)
            return false;

        for (String permission : permissions)
            if (ContextCompat.checkSelfPermission(activity, permission)
                    != PackageManager.PERMISSION_GRANTED)
                if (ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)) {
                    r.showRationaleDialog();
                    return false;
                } else {
                    askForPermissions(activity, permissionKey);
                    return false;
                }

        return true;
    }

    public static void askForPermissions(Activity activity, int permissionKey) {
        String[] permissions = permissionMap.get(permissionKey);

        if (activity == null || permissions == null)
            return;

        ActivityCompat.requestPermissions(activity, permissions, permissionKey);
    }

    public static String convertToTime(long datetime) {
        Date date = new Date(datetime);
        DateFormat formatter = new SimpleDateFormat("dd.MM HH:mm", Locale.GERMANY);
        return formatter.format(date);
    }

    public static String getEstimatedDaysText(StateViewModel st) {
        State lastBatteryState = st.getConfirmedBatterySync();
        boolean isWifiOn = st.getConfirmedWifiSync();
        int interval = st.getConfirmedIntervalSync();

        return estimateBatteryDays(lastBatteryState, isWifiOn, interval);
    }

    private static String estimateBatteryDays(State lastBatteryState, boolean isWifiOn, int interval) {
        if (lastBatteryState == null) {
            return "";
        }

        int remainingPercent = lastBatteryState.getValue().intValue() - 10;
        Double days;
        long timeSinceLastManagement = new Date().getTime() - lastBatteryState.getTimestamp();
        double daysSinceLastManagement = timeSinceLastManagement / 1000.0 / 60 / 60 / 24;

        if (remainingPercent < 10)
            return "Unter 10%, bitte umgehend aufladen!";

        if (isWifiOn)
            days = 1.7;
        else if (batteryRuntimeByInterval.containsKey(interval)) {
            days = batteryRuntimeByInterval.get(interval);
            if (days == null)
                return "";
        } else
            return "";

        double remainingDays = ((double) remainingPercent) / 100 * days;
        double remainingDaysFromNow = remainingDays - daysSinceLastManagement;
        double remainingHoursFromNow = remainingDaysFromNow * 24;
        long chargeDateMs =
                lastBatteryState.getTimestamp() + ((long) remainingDays * 1000 * 60 * 60 * 24);
        Date chargeDateDate = new Date(chargeDateMs);
        String chargeDate =
                new SimpleDateFormat("dd.MM.yy", Locale.GERMANY).format(chargeDateDate);

        if (remainingDaysFromNow > 1)
            return "Noch ca. " + (int) remainingDaysFromNow + " Tage (" + chargeDate + ")";

        if (remainingHoursFromNow > 0)
            return "Noch ca. " + (int) remainingHoursFromNow + " Stunden!";

        return "Unter 10%, bitte umgehend aufladen!";
    }

    public static String convertToDateHuman(long datetime) {
        Date date = new Date(datetime);

        String outputTime = new SimpleDateFormat("HH:mm", Locale.GERMANY).format(date);
        String outputDate = new SimpleDateFormat("dd.MM", Locale.GERMANY).format(date);
        String outputDateWithYear = new SimpleDateFormat("dd.MM.yy", Locale.GERMANY).format(date);
        String outputAll = new SimpleDateFormat("dd.MM.yy HH:mm", Locale.GERMANY).format(date);

        long ms = new Date().getTime() - date.getTime();
        long s = ms/1000;
        long m = s/60;
        int h = (int) m/60;
        int d = h/24;
        int y = d/365;

        if (y > 1)
            return outputDateWithYear + String.format(Locale.GERMANY, " (Vor %d Jahren)", y);
        if (y > 0)
            return outputDateWithYear + " (Vor 1 Jahr)";
        if (d > 1)
            return outputDate + String.format(Locale.GERMANY, " (Vor %d Tagen)", d);
        if (d > 0)
            return outputDate + " (Vor 1 Tag)";
        if (h > 1)
            return outputTime + String.format(Locale.GERMANY, " (Vor %d Stunden)", h);
        if (h > 0)
            return outputTime + " (Vor 1 Stunde)";
        if (m > 1)
            return outputTime + String.format(Locale.GERMANY, " (Vor %d Minuten)", m);
        if (m > 0)
            return outputTime + " (Vor 1 Minute)";
        if (s > 1)
            return outputTime + String.format(Locale.GERMANY, " (Vor %d Sekunden)", s);
        if (s > 0)
            return outputTime + " (Vor 1 Sekunde)";

        return outputAll;
    }

    public static Drawable getBatteryDrawable(Context ctx, double batteryStatus) {
        if (batteryStatus == 100.0)
            return ContextCompat.getDrawable(ctx, R.drawable.ic_battery_full_black_24dp);
        if (batteryStatus > 90.0)
            return ContextCompat.getDrawable(ctx, R.drawable.ic_battery_90_black_24dp);
        if (batteryStatus > 80.0)
            return ContextCompat.getDrawable(ctx, R.drawable.ic_battery_80_black_24dp);
        if (batteryStatus > 60.0)
            return ContextCompat.getDrawable(ctx, R.drawable.ic_battery_60_black_24dp);
        if (batteryStatus > 50.0)
            return ContextCompat.getDrawable(ctx, R.drawable.ic_battery_50_black_24dp);
        if (batteryStatus > 30.0)
            return ContextCompat.getDrawable(ctx, R.drawable.ic_battery_30_black_24dp);
        if (batteryStatus > 20.0)
            return ContextCompat.getDrawable(ctx, R.drawable.ic_battery_20_black_24dp);
        if (batteryStatus > 0)
            return ContextCompat.getDrawable(ctx, R.drawable.ic_battery_alert_red_24dp);

        return ContextCompat.getDrawable(ctx, R.drawable.ic_battery_unknown_black_24dp);
    }
}


