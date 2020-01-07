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

    public void updateWarningNumber(SharedPreferences settings, String value) {
        settings.edit()
                .putString("warningNumber", value)
                .putString("warningNumberLastChange", Utils.getTimestamp())
                .apply();
    }

    public void updateWifi(SharedPreferences settings, boolean state) {
        settings.edit()
                .putBoolean("wlan", state)
                .putString("wifiLastChange", Utils.getTimestamp())
                .apply();
    }

    void resetAll(SharedPreferences settings) {
        settings.edit()
                .putString("interval", "")
                .putString("warningNumber", "")
                .putBoolean("wlan", false)
                .putString("intervalLastChange", "")
                .putString("warningNumberLastChange", "")
                .putString("wifiLastChange", "")
                .putBoolean("askedForWarningNumber", false)
                .apply();
    }
}
