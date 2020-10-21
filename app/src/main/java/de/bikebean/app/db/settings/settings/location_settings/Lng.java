package de.bikebean.app.db.settings.settings.location_settings;

import androidx.annotation.NonNull;

import de.bikebean.app.db.settings.settings.LocationSetting;
import de.bikebean.app.db.sms.Sms;
import de.bikebean.app.db.state.State;

public class Lng extends LocationSetting {

    private static final @NonNull State.KEY key = State.KEY.LNG;

    public Lng(double lng, final @NonNull Sms sms) {
        super(lng, sms, key);
    }

    public Lng() {
        super(key);
    }
}
