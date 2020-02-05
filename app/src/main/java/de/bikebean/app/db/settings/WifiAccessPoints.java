package de.bikebean.app.db.settings;

import de.bikebean.app.db.sms.Sms;
import de.bikebean.app.db.state.State;

public class WifiAccessPoints extends Setting {
    private String wifiAccessPoints;

    public WifiAccessPoints(String wifiAccessPoints, Sms sms) {
        this.wifiAccessPoints = wifiAccessPoints;
        this.sms = sms;
        this.key = State.KEY_WIFI_ACCESS_POINTS;
    }

    public String get() {
        return wifiAccessPoints;
    }
}
