package de.bikebean.app.ui.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.ColorInt;
import androidx.annotation.ColorRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.ColorUtils;
import androidx.preference.PreferenceManager;

import com.google.android.material.snackbar.Snackbar;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

import de.bikebean.app.BuildConfig;
import de.bikebean.app.R;
import de.bikebean.app.db.state.State;
import de.bikebean.app.ui.main.map.MapFragment;
import de.bikebean.app.ui.main.status.StatusFragment;
import de.bikebean.app.ui.utils.date.Period;

public class Utils {

    /* Permissions */

    public enum PERMISSION_KEY {
        SMS, MAPS
    }

    public interface RationaleShower {
        void showRationaleDialog();
    }

    private static final Map<PERMISSION_KEY, String[]> permissionMap =
            new HashMap<PERMISSION_KEY, String[]>() {{
                put(PERMISSION_KEY.SMS, StatusFragment.getSmsPermissions());
                put(PERMISSION_KEY.MAPS, MapFragment.mapsPermissions);
    }};

    public static boolean getPermissions(final @NonNull Activity activity,
                                         final @NonNull PERMISSION_KEY p,
                                         final @NonNull RationaleShower r) {
        final @Nullable String[] permissions = permissionMap.get(p);

        if (permissions == null)
            return false;

        for (@NonNull String permission : permissions)
            if (checkSelfPermission(activity, permission))
                if (ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)) {
                    r.showRationaleDialog();
                    return false;
                } else {
                    askForPermissions(activity, p);
                    return false;
                }

        return true;
    }

    private static boolean checkSelfPermission(final @NonNull Context context,
                                               final @NonNull String permission) {
        return ContextCompat.checkSelfPermission(context, permission)
                != PackageManager.PERMISSION_GRANTED;
    }

    static void askForPermissions(final @NonNull Activity activity,
                                  final @NonNull PERMISSION_KEY p) {
        final @Nullable String[] permissions = permissionMap.get(p);

        if (permissions != null)
            ActivityCompat.requestPermissions(activity, permissions, p.ordinal());
    }

    /* */

    /* Version name */

    public static @NonNull String getVersionName() {
        return BuildConfig.VERSION_NAME;
    }

    /* */

    /* UUID */

    private static @Nullable String uniqueID = null;
    private static final @NonNull String PREF_UNIQUE_ID = "DEVICE_UUID";

    public synchronized static @NonNull String getUUID(final @NonNull Context context) {
        if (uniqueID == null) {
            final @Nullable SharedPreferences sharedPrefs =
                    PreferenceManager.getDefaultSharedPreferences(context);

            if (sharedPrefs != null)
                uniqueID = sharedPrefs.getString(PREF_UNIQUE_ID,null);
            else return UUID.randomUUID().toString();

            if (uniqueID == null) {
                uniqueID = UUID.randomUUID().toString();

                final @NonNull SharedPreferences.Editor editor = sharedPrefs.edit();
                editor.putString(PREF_UNIQUE_ID, uniqueID);
                editor.apply();
            }
        }

        return uniqueID;
    }

    /* */

    /* Battery runtime and date utils */

    private static final Map<Integer, Double> batteryRuntimeByInterval =
            new HashMap<Integer, Double>() {{
                put(1, 175.0);
                put(2, 260.0);
                put(4, 346.0);
                put(8, 415.0);
                put(12, 444.0);
                put(24, 477.0);
            }};

    public static final @NonNull String DAYS_SINCE_LAST_STATE = "daysSinceLastState";

    public static double getDaysSinceState(final @NonNull State state) {
        long timeSinceLastManagement = new Date().getTime() - state.getTimestamp();
        return timeSinceLastManagement / 1000.0 / 60 / 60 / 24;
    }

    public static double getDaysSinceState(final @Nullable Bundle args) {
        if (args != null)
            return args.getDouble(DAYS_SINCE_LAST_STATE, 0.0);
        else return 0.0;
    }

    public static @NonNull String estimateBatteryDays(final @Nullable State lastBatteryState,
                                                      boolean isWifiOn, int interval) {
        if (lastBatteryState == null)
            return "";

        int remainingPercent = lastBatteryState.getValue().intValue() - 10;
        @Nullable Double days;
        double daysSinceLastManagement = getDaysSinceState(lastBatteryState);

        if (remainingPercent < 0)
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
        final @NonNull Date chargeDateDate = new Date(chargeDateMs);
        final @NonNull String chargeDate =
                new SimpleDateFormat("dd.MM.yy", Locale.GERMANY).format(chargeDateDate);

        if (remainingDaysFromNow > 1)
            return "Noch ca. " + (int) remainingDaysFromNow + " Tage (" + chargeDate + ")";

        if (remainingHoursFromNow > 0)
            return "Noch ca. " + (int) remainingHoursFromNow + " Stunden!";

        return "Unter 10%, bitte umgehend aufladen!";
    }

    public static @NonNull String ConvertToDateHuman() {
        return new Period(new Date().getTime()).convertPeriodToHuman();
    }

    public static @NonNull String ConvertPeriodToHuman(long datetime) {
        return new Period(datetime).convertPeriodToHuman();
    }

    public static @NonNull String convertToTimeLog(long datetime) {
        return formatDate("dd.MM.yy HH:mm", datetime);
    }

    public static @NonNull String convertToTime(long datetime) {
        return formatDate("dd.MM HH:mm", datetime);
    }

    private static @NonNull String formatDate(final @NonNull String pattern, long datetime) {
        final @NonNull Date date = new Date(datetime);
        final @NonNull DateFormat formatter = new SimpleDateFormat(pattern, Locale.GERMANY);

        return formatter.format(date);
    }

    public static @NonNull Date getDateFromUTCString(final @NonNull String UTCString) {
        final @NonNull DateFormat df1 =
                new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.GERMANY);
        final @Nullable Date tmpDate;

        try {
            tmpDate = df1.parse(UTCString);
        } catch (ParseException e) {
            return new Date(1);
        }

        if (tmpDate != null)
            return tmpDate;

        return new Date(1);
    }

    /* */

    /* Battery drawables */

    private static final @NonNull Map<Double, Integer> batteryDrawables =
            new HashMap<Double, Integer>() {{
        put(100.0, R.drawable.ic_battery_full_black_24dp);
        put(90.0, R.drawable.ic_battery_90_black_24dp);
        put(80.0, R.drawable.ic_battery_80_black_24dp);
        put(60.0, R.drawable.ic_battery_60_black_24dp);
        put(50.0, R.drawable.ic_battery_50_black_24dp);
        put(30.0, R.drawable.ic_battery_30_black_24dp);
        put(20.0, R.drawable.ic_battery_20_black_24dp);
        put(0.0, R.drawable.ic_battery_alert_red_24dp);
        put(-1.0, R.drawable.ic_battery_unknown_black_24dp);
    }};

    public static @Nullable Drawable getBatteryDrawable(final @NonNull Context ctx,
                                                       double batteryStatus) {
        final @NonNull Double flooredBatteryStatus = floorToBatterySteps(batteryStatus);
        final @Nullable Integer batteryDrawableId;

        if (batteryDrawables.containsKey(flooredBatteryStatus))
            batteryDrawableId = batteryDrawables.get(flooredBatteryStatus);
        else
            return null;

        if (batteryDrawableId == null)
            return null;

        return ContextCompat.getDrawable(ctx, batteryDrawableId);
    }

    private static double floorToBatterySteps(double batteryStatus) {
        Double[] doubles = batteryDrawables.keySet().toArray(new Double[]{});
        Arrays.sort(doubles, Collections.reverseOrder());

        for (double d : doubles)
            if (batteryStatus >= d)
                return d;

        return -1.0;
    }

    /* */

    /* Map Marker colors */

    public static @ColorInt int getMarkerColor(final @NonNull Context context,
                                               final double daysSinceState) {
        float d = floorToOne(daysSinceState / 365);
        return new HslColor(context, R.color.secondaryColor).getCodedMarkerColor(d);
    }

    private static float floorToOne(double a) {
        return (float) (a > 1 ? 1 : a);
    }

    static class HslColor {
        final float h, s, l;

        HslColor(final @NonNull Context context, final @ColorRes int color) {
            float[] hsl = new float[3];
            ColorUtils.colorToHSL(ContextCompat.getColor(context, color), hsl);

            h = hsl[0];
            s = hsl[1];
            l = hsl[2];
        }

        @ColorInt int getCodedMarkerColor(final float d) {
            return ColorUtils.HSLToColor(new float[]{h, s * (1-d), (float) (l + (0.33*d))});
        }
    }

    /* */

    /* Share Button and Help Button */

    public static @Nullable Intent getShareIntent(final @NonNull String string) {
        if (string.isEmpty())
            return null;

        final @NonNull Intent sendIntent = new Intent()
                .setAction(Intent.ACTION_SEND)
                .putExtra(Intent.EXTRA_TEXT, string)
                .setType("text/plain");

        return Intent.createChooser(sendIntent, null);
    }

    public static void onHelpClick(final @NonNull View v) {
        Snackbar.make(v,
                R.string.help2,
                Snackbar.LENGTH_LONG)
//                .setAction(R.string.history, (v1 -> {}))
        .show();
    }

    /* */
}
