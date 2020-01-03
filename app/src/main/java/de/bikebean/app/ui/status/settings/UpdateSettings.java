package de.bikebean.app.ui.status.settings;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.preference.PreferenceManager;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import de.bikebean.app.MainActivity;
import de.bikebean.app.db.status.Status;
import de.bikebean.app.ui.status.StatusViewModel;

public class UpdateSettings {

    private SharedPreferences settings;
    private SharedPreferences.Editor editor;

    public void updateBattery(int value, StatusViewModel vm) {
        Status s = new Status(
                System.currentTimeMillis(), "battery",
                (double) value, "", Status.STATUS_CONFIRMED
        );
        vm.insert(s);
    }

    public void updateInterval(Context ctx, String value) {
        if (settings == null) {
            settings = PreferenceManager.getDefaultSharedPreferences(ctx);
        }

        editor = settings.edit();

        Log.d(MainActivity.TAG, "Updating interval to " + value);

        editor.putString("interval", value);
        editor.putString("intervalLastChange", getTimestamp());
        editor.putString("statusLastChange", getTimestamp());

        // Commit the edits!
        editor.apply();
    }

    public void updateWarningNumber(Context ctx, String value) {
        if (settings == null) {
            settings = PreferenceManager.getDefaultSharedPreferences(ctx);
        }

        editor = settings.edit();

        editor.putString("warningNumber", value);
        editor.putString("warningNumberLastChange", getTimestamp());
        editor.putString("statusLastChange", getTimestamp());

        editor.apply();
    }

    public void updateWifi(Context ctx, boolean state) {
        if (settings == null) {
            settings = PreferenceManager.getDefaultSharedPreferences(ctx);
        }

        editor = settings.edit();

        editor.putBoolean("wlan", state);
        editor.putString("wifiLastChange", getTimestamp());
        editor.putString("statusLastChange", getTimestamp());

        editor.apply();
    }

    public void updatePosition(String value, StatusViewModel vm) {
        vm.insert(new Status(
                System.currentTimeMillis(), "cellTowers",
                (double) 0, value, Status.STATUS_CONFIRMED)
        );
    }

    public void updateWifiList(String value, StatusViewModel vm) {
        vm.insert(new Status(
                System.currentTimeMillis(), "wifiAccessPoints",
                (double) 0, value, Status.STATUS_CONFIRMED)
        );
    }

    public void updateNoCellTowers(int number, StatusViewModel vm) {
        Status s = new Status(
                System.currentTimeMillis(), "noCellTowers",
                (double) number, "", Status.STATUS_CONFIRMED);

        vm.insert(s);
    }

    public void updateNoWifiAccessPoints(int number, StatusViewModel vm) {
        Status s = new Status(
                System.currentTimeMillis(), "noWifiAccessPoints",
                (double) number, "", Status.STATUS_CONFIRMED);

        vm.insert(s);
    }

    public void updateLngLat(Float Lat, Float Lng, Float Acc, StatusViewModel vm) throws InterruptedException {
        Status s1 = new Status(
                System.currentTimeMillis(), "lat",
                (double) Lat, "", Status.STATUS_CONFIRMED);
        Thread.sleep(1);
        Status s2 = new Status(
                System.currentTimeMillis(), "lng",
                (double) Lng, "", Status.STATUS_CONFIRMED);
        Thread.sleep(1);
        Status s3 = new Status(
                System.currentTimeMillis(), "acc",
                (double) Acc, "", Status.STATUS_CONFIRMED);

        vm.insert(s1);
        vm.insert(s2);
        vm.insert(s3);
    }

    void resetAll(Context ctx) {
        if (settings == null) {
            settings = PreferenceManager.getDefaultSharedPreferences(ctx);
        }

        editor = settings.edit();

        editor.putString("interval", "");
        editor.putString("warningNumber", "");
        editor.putBoolean("wlan", false);

        editor.putString("statusLastChange", "");
        editor.putString("intervalLastChange", "");
        editor.putString("statusLastChange", "");
        editor.putString("warningNumberLastChange", "");
        editor.putString("wifiLastChange", "");

        editor.putBoolean("askedForWarningNumber", false);

        // Commit the edits!
        editor.apply();
    }

    static String getTimestamp() {
        return new SimpleDateFormat("dd.MM.yyyy HH:mm:ss", Locale.GERMANY).format(Calendar.getInstance().getTime());
    }
}
