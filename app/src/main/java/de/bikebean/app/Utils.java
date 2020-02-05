package de.bikebean.app;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import de.bikebean.app.db.state.State;
import de.bikebean.app.ui.status.StateViewModel;
import de.bikebean.app.ui.status.status.StatusStatusFragment;

public class Utils {

    private static final Map<Integer, Double> batteryRuntimeByInterval =
            new HashMap<Integer, Double>() {{
        put(1, 175.0);
        put(2, 260.0);
        put(4, 346.0);
        put(8, 415.0);
        put(12, 444.0);
        put(24, 477.0);
    }};

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

    public static String getEstimatedDaysText(StateViewModel stateViewModel,
                                              double batteryStatus, long lastMeasurement) {
        int remainingPercent = (int) batteryStatus - 10;
        Double days;
        long timeSinceLastManagement = new Date().getTime() - lastMeasurement;
        double daysSinceLastManagement = timeSinceLastManagement / 1000.0 / 60 / 60 / 24;

        if (remainingPercent < 10)
            return "Unter 10%, bitte umgehend aufladen!";

        boolean isWifiOn = getConfirmedWifiSync(stateViewModel);
        int interval = getConfirmedIntervalSync(stateViewModel);

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
        long chargeDateMs = lastMeasurement + ((long) remainingDays * 1000 * 60 * 60 * 24);
        String chargeDate = new SimpleDateFormat("dd.MM.yy", Locale.GERMANY).format(new Date(chargeDateMs));

        if (remainingDaysFromNow > 1)
            return "Noch ca. " + (int) remainingDaysFromNow + " Tage (" + chargeDate + ")";

        return "Noch ca. " + (int) remainingHoursFromNow + " Stunden!";
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

        return ContextCompat.getDrawable(ctx, R.drawable.ic_battery_alert_red_24dp);
    }

    public static boolean getWifiStatusSync(StateViewModel st) {
        final MutableString oldWifiValue = new MutableString();

        new Thread(() -> {
            List<State> wifiList = st.getWifi();
            State lastWifiState;

            if (wifiList.size() > 0)
                lastWifiState = wifiList.get(0);
            else
                return;

            oldWifiValue.set(String.valueOf(lastWifiState.getValue() > 0));
        }).start();

        while (oldWifiValue.get().isEmpty()) {
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        return Boolean.valueOf(oldWifiValue.get());
    }

    private static boolean getConfirmedWifiSync(StateViewModel st) {
        final MutableString oldWifiValue = new MutableString();

        new Thread(() -> {
            List<State> wifiList = st.getConfirmedWifi();
            State lastWifiState;

            if (wifiList.size() > 0)
                lastWifiState = wifiList.get(0);
            else
                return;

            oldWifiValue.set(String.valueOf(lastWifiState.getValue() > 0));
        }).start();

        while (oldWifiValue.get().isEmpty()) {
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        return Boolean.valueOf(oldWifiValue.get());
    }

    public static int getIntervalStatusSync(StateViewModel st) {
        final MutableString oldIntervalValue = new MutableString();

        new Thread(() -> {
            List<State> intervalList = st.getInterval();
            State lastIntervalState;

            if (intervalList.size() > 0)
                lastIntervalState = intervalList.get(0);
            else
                return;

            int interval = lastIntervalState.getValue().intValue();
            oldIntervalValue.set(String.valueOf(interval));
        }).start();

        while (oldIntervalValue.get().isEmpty()) {
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        return Integer.valueOf(oldIntervalValue.get());
    }

    public static int getConfirmedIntervalSync(StateViewModel st) {
        final MutableString intervalConfirmed = new MutableString();

        new Thread(() -> {
            List<State> intervalList = st.getIntervalConfirmed();
            State lastIntervalState;

            if (intervalList.size() > 0)
                lastIntervalState = intervalList.get(0);
            else
                return;

            int interval = lastIntervalState.getValue().intValue();
            intervalConfirmed.set(String.valueOf(interval));
        }).start();

        while (intervalConfirmed.get().isEmpty()) {
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        return Integer.valueOf(intervalConfirmed.get());
    }

    static class MutableString {

        private String string;
        private volatile boolean is_set = false;

        void set(String i) {
            this.string = i;
            is_set = true;
        }

        String get() {
            if (is_set)
                return string;
            else
                return "";
        }
    }
}


