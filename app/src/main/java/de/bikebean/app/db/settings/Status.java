package de.bikebean.app.db.settings;

import de.bikebean.app.db.sms.Sms;
import de.bikebean.app.db.state.State;

public class Status extends Setting {
    private double status;

    public Status(double status, Sms sms) {
        this.status = status;
        this.sms = sms;
        this.key = State.KEY_STATUS;
    }

    public Double get() {
        return status;
    }
}
