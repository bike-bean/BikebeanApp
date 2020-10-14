package de.bikebean.app.db.settings.settings.location_settings;

import androidx.annotation.NonNull;

import de.bikebean.app.db.settings.settings.LocationSetting;
import de.bikebean.app.db.settings.settings.WappState;
import de.bikebean.app.db.sms.Sms;
import de.bikebean.app.db.state.State;

public class Location extends LocationSetting {

    private final static @NonNull State.KEY key = State.KEY.LOCATION;

    public Location(double location, final @NonNull WappState wappState) {
        super(location, wappState, key);
    }

    public Location() {
        super(new Sms(), key, State.STATUS.UNSET);
    }

    public Location(final @NonNull WappState wappState) {
        super(wappState.getSms(), key, State.STATUS.PENDING);
    }
}
