package de.bikebean.app.db.settings;

import de.bikebean.app.db.sms.Sms;
import de.bikebean.app.db.state.State;

public class Wifi extends Setting {
    private boolean wifi;

    public Wifi(boolean wifi, Sms sms) {
        this.wifi = wifi;
        this.sms = sms;
        this.key = State.KEY_WIFI;
    }

    public Double get() {
        return wifi ? 1.0 : 0.0;
    }
}
