package de.bikebean.app.db.settings.settings.location_settings;

import de.bikebean.app.db.settings.settings.LocationSetting;
import de.bikebean.app.db.settings.settings.WappState;
import de.bikebean.app.db.sms.Sms;
import de.bikebean.app.db.state.State;

public class Lng extends LocationSetting {

    private static final State.KEY key = State.KEY.LNG;

    public Lng(double lng, WappState wappState) {
        super(lng, wappState, key);
    }

    public Lng() {
        super(new Sms(), key, State.STATUS.UNSET);
    }
}
