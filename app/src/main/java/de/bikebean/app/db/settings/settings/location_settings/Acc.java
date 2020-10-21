package de.bikebean.app.db.settings.settings.location_settings;

import androidx.annotation.NonNull;

import de.bikebean.app.db.settings.settings.LocationSetting;
import de.bikebean.app.db.sms.Sms;
import de.bikebean.app.db.state.State;

public class Acc extends LocationSetting {

    private static final @NonNull State.KEY key = State.KEY.ACC;

    public Acc(double acc, final @NonNull Sms sms) {
        super(acc, sms, key);
    }

    public Acc() {
        super(key);
    }
}
