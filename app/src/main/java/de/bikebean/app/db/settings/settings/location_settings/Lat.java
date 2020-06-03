package de.bikebean.app.db.settings.settings.location_settings;

import de.bikebean.app.db.settings.settings.LocationSetting;
import de.bikebean.app.db.sms.Sms;
import de.bikebean.app.db.state.State;

public class Lat extends LocationSetting {

    private static final State.KEY key = State.KEY.LAT;

    public Lat(double lat, Sms sms) {
        super(lat, sms, key);
    }

    public Lat() {
        super(new Sms(), key, State.STATUS.UNSET);
    }
}
