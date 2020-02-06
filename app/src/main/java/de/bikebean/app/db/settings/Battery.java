package de.bikebean.app.db.settings;

import de.bikebean.app.db.sms.Sms;
import de.bikebean.app.db.state.State;

public class Battery extends Setting {
    private final double battery;

    public Battery(double battery, Sms sms) {
        this.battery = battery;
        this.sms = sms;
        this.key = State.KEY_BATTERY;
    }

    public Double get() {
        return battery;
    }
}
