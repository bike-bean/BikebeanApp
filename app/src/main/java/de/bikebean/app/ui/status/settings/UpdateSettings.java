package de.bikebean.app.ui.status.settings;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.preference.PreferenceManager;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
// import java.util.Map;

import de.bikebean.app.MainActivity;

public class UpdateSettings {

    private SharedPreferences settings;
    private SharedPreferences.Editor editor;

    public void updateBattery(Context ctx, int value) {
        if (settings == null) {
            settings = PreferenceManager.getDefaultSharedPreferences(ctx);
        }

        editor = settings.edit();

        editor.putInt("batteryStatus", value);
        editor.putString("batteryLastChange", getTimestamp());
        editor.putString("statusLastChange", getTimestamp());

        // Commit the edits!
        editor.apply();
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

    public void updatePosition(Context ctx, String value) {
        if (settings == null) {
            settings = PreferenceManager.getDefaultSharedPreferences(ctx);
        }

        editor = settings.edit();

        editor.putString("location", value);
        editor.putString("locationLastChange", getTimestamp());

        editor.apply();
    }

    public void updateWifiList(Context ctx, String value) {
        if (settings == null) {
            settings = PreferenceManager.getDefaultSharedPreferences(ctx);
        }

        editor = settings.edit();

        editor.putString("wifiList", value);
        editor.putString("wifiListLastChange", getTimestamp());

        editor.apply();
    }

    public void updateNoCellTowers(Context ctx, int number) {
        if (settings == null) {
            settings = PreferenceManager.getDefaultSharedPreferences(ctx);
        }

        editor = settings.edit();

        editor.putInt("numberCellTowers", number);
        editor.putString("numberCellTowersLastChange", getTimestamp());

        editor.apply();
    }

    public void updateNoWifiAccessPoints(Context ctx, int number) {
        if (settings == null) {
            settings = PreferenceManager.getDefaultSharedPreferences(ctx);
        }

        editor = settings.edit();

        editor.putInt("numberWifiAccessPoints", number);
        editor.putString("numberWifiAccessPointsLastChange", getTimestamp());

        editor.apply();
    }

    public void updateLngLat(Context ctx, Float Lat, Float Lng, Float Acc) {
        if (settings == null) {
            settings = PreferenceManager.getDefaultSharedPreferences(ctx);
        }

        editor = settings.edit();

        editor.putFloat("lat", Lat);
        editor.putFloat("lng", Lng);
        editor.putFloat("acc", Acc);
        editor.putString("latLngLastChange", getTimestamp());

        // Map<String,?> m = settings.getAll();

        editor.apply();
    }

    void resetAll(Context ctx) {
        if (settings == null) {
            settings = PreferenceManager.getDefaultSharedPreferences(ctx);
        }

        editor = settings.edit();

        editor.putInt("batteryStatus", 0);
        editor.putString("interval", "");
        editor.putString("warningNumber", "");
        editor.putBoolean("wlan", false);
        editor.putString("location", "");

        editor.putString("wifiList", "");
        editor.putInt("numberCellTowers", 0);
        editor.putInt("numberWifiAccessPoints", 0);
        editor.putFloat("lat", (float) 0.0);
        editor.putFloat("lng", (float) 0.0);

        editor.putFloat("acc", (float) 0.0);
        editor.putString("latLngLastChange", "");
        editor.putBoolean("askedForWarningNumber", false);


        editor.putString("batteryLastChange", "");
        editor.putString("statusLastChange", "");
        editor.putString("intervalLastChange", "");
        editor.putString("statusLastChange", "");
        editor.putString("warningNumberLastChange", "");

        editor.putString("statusLastChange", "");
        editor.putString("wifiLastChange", "");
        editor.putString("statusLastChange", "");
        editor.putString("locationLastChange", "");
        editor.putString("wifiListLastChange", "");

        editor.putString("numberCellTowersLastChange", "");
        editor.putString("numberWifiAccessPointsLastChange", "");

        // Commit the edits!
        editor.apply();
    }

    static String getTimestamp() {
        return new SimpleDateFormat("dd.MM.yyyy HH:mm:ss", Locale.GERMANY).format(Calendar.getInstance().getTime());
    }
}
