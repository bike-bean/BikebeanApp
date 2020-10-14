package de.bikebean.app.db.settings.settings.location_settings;

import androidx.annotation.NonNull;

import de.bikebean.app.db.settings.settings.LocationSetting;
import de.bikebean.app.db.settings.settings.WappState;
import de.bikebean.app.db.sms.Sms;
import de.bikebean.app.db.state.State;

public class Lat extends LocationSetting {

    private static final @NonNull State.KEY key = State.KEY.LAT;

    public Lat(double lat, final @NonNull WappState wappState) {
        super(lat, wappState, key);
    }

    public Lat() {
        super(new Sms(), key, State.STATUS.UNSET);
    }
}
