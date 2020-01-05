package de.bikebean.app.ui.status.preferences;

import android.content.SharedPreferences;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import de.bikebean.app.MainActivity;
import de.bikebean.app.db.status.Status;
import de.bikebean.app.ui.status.StatusViewModel;

public class PreferenceUpdater {

    private SharedPreferences.Editor editor;

    public void updateInterval(SharedPreferences settings, String value) {
        editor = settings.edit();

        Log.d(MainActivity.TAG, "Updating interval to " + value);

        editor.putString("interval", value);
        editor.putString("intervalLastChange", getTimestamp());
        editor.putString("statusLastChange", getTimestamp());

        // Commit the edits!
        editor.apply();
    }

    public void updateWarningNumber(SharedPreferences settings, String value) {
        editor = settings.edit();

        editor.putString("warningNumber", value);
        editor.putString("warningNumberLastChange", getTimestamp());
        editor.putString("statusLastChange", getTimestamp());

        editor.apply();
    }

    public void updateWifi(SharedPreferences settings, boolean state) {
        editor = settings.edit();

        editor.putBoolean("wlan", state);
        editor.putString("wifiLastChange", getTimestamp());
        editor.putString("statusLastChange", getTimestamp());

        editor.apply();
    }

    void resetAll(SharedPreferences settings) {
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
