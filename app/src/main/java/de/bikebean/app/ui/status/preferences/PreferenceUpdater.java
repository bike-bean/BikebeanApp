package de.bikebean.app.ui.status.preferences;

import android.content.SharedPreferences;

import de.bikebean.app.Utils;

public class PreferenceUpdater {

    public void updateInterval(SharedPreferences settings, String value) {
        settings.edit()
                .putString("interval", value)
                .putString("intervalLastChange", Utils.getTimestamp())
                .apply();
    }

    public void updateInterval(SharedPreferences settings, String value, String date) {
        settings.edit()
                .putString("interval", value)
                .putString("intervalLastChange", date)
                .apply();
    }

    public void updateWarningNumber(SharedPreferences settings, String value) {
        settings.edit()
                .putString("warningNumber", value)
                .putString("warningNumberLastChange", Utils.getTimestamp())
                .apply();
    }

    public void updateWarningNumber(SharedPreferences settings, String value, String date) {
        settings.edit()
                .putString("warningNumber", value)
                .putString("warningNumberLastChange", date)
                .apply();
    }

    public void updateWifi(SharedPreferences settings, boolean state) {
        settings.edit()
                .putBoolean("wlan", state)
                .putString("wifiLastChange", Utils.getTimestamp())
                .apply();
    }

    public void updateWifi(SharedPreferences settings, boolean state, String date) {
        settings.edit()
                .putBoolean("wlan", state)
                .putString("wifiLastChange", date)
                .apply();
    }

    public void updateInitialLoading(SharedPreferences settings, boolean state) {
        settings.edit()
                .putBoolean("initialLoading", state)
                .apply();
    }
}
