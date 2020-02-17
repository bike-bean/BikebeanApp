package de.bikebean.app.db.settings;

import de.bikebean.app.db.sms.Sms;
import de.bikebean.app.db.state.State;

public class Wapp extends Setting {

    private final double wapp;

    public Wapp(double wapp, Sms sms) {
        this.wapp= wapp;
        this.sms = sms;
        this.key = State.KEY.WAPP;
    }

    public Double get() {
        return wapp;
    }
}
