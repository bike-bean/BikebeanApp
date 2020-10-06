package de.bikebean.app.db.settings.settings.location_settings;

import androidx.annotation.NonNull;

import de.bikebean.app.db.settings.settings.LocationSetting;
import de.bikebean.app.db.settings.settings.WappState;
import de.bikebean.app.db.sms.Sms;
import de.bikebean.app.db.state.State;

public class Acc extends LocationSetting {

    private static final State.KEY key = State.KEY.ACC;

    public Acc(double acc, @NonNull WappState wappState) {
        super(acc, wappState, key);
    }

    public Acc() {
        super(new Sms(), key, State.STATUS.UNSET);
    }
}
