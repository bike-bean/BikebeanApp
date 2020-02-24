package de.bikebean.app.db.settings;

import de.bikebean.app.db.sms.Sms;
import de.bikebean.app.db.state.State;

public class Interval extends Setting {
    private final int interval;

    public Interval(int interval, Sms sms) {
        this.interval = interval;
        this.sms = sms;
        this.key = State.KEY.INTERVAL;
    }

    public Double get() {
        return (double) interval;
    }
}
