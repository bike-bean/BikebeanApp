package de.bikebean.app.db.settings.settings.location_settings;

import de.bikebean.app.db.settings.settings.LocationSetting;
import de.bikebean.app.db.sms.Sms;
import de.bikebean.app.db.state.State;

public class Acc extends LocationSetting {

    private static final State.KEY key = State.KEY.ACC;

    public Acc(double acc, Sms sms) {
        super(acc, sms, key);
    }

    public Acc() {
        super(new Sms(), key, State.STATUS.UNSET);
    }
}
