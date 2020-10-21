package de.bikebean.app.db.settings.settings.location_settings;

import androidx.annotation.NonNull;

import de.bikebean.app.db.settings.settings.LocationSetting;
import de.bikebean.app.db.sms.Sms;
import de.bikebean.app.db.state.State;

public class Location extends LocationSetting {

    private final static @NonNull State.KEY key = State.KEY.LOCATION;

    public Location(double location, final @NonNull Sms sms) {
        super(location, sms, key);
    }

    public Location(final @NonNull Sms sms) {
        super(sms, key);
    }

    public Location() {
        super(key);
    }
}
