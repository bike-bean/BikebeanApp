package de.bikebean.app.db.settings.settings;

import de.bikebean.app.db.settings.Setting;
import de.bikebean.app.db.sms.Sms;
import de.bikebean.app.db.state.State;

public class Wifi extends Setting {

    private static final boolean INITIAL_WIFI = false;

    private final boolean wifi;

    public Wifi(boolean wifi, Sms sms) {
        super(sms, State.KEY.WIFI);
        conversationListAdder = super::replaceIfNewer;
        stateGetter = super::getStateConfirmed;

        this.wifi = wifi;
    }

    public Wifi() {
        super(new Sms(), State.KEY.WIFI);
        conversationListAdder = super::replaceIfNewer;
        stateGetter = super::getStateConfirmed;

        this.wifi = INITIAL_WIFI;
    }

    @Override
    public Double get() {
        return wifi ? 1.0 : 0.0;
    }

    public boolean getRaw() {
        return wifi;
    }
}
