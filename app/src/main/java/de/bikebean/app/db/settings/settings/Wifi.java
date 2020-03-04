package de.bikebean.app.db.settings.settings;

import de.bikebean.app.db.settings.Setting;
import de.bikebean.app.db.sms.Sms;
import de.bikebean.app.db.state.State;

public class Wifi extends Setting {

    private static final State.KEY key = State.KEY.WIFI;
    private static final C_LIST_ADD_TYPE cListAddType = C_LIST_ADD_TYPE.REPLACE_IF_NEWER;

    private static final boolean INITIAL_WIFI = false;

    private final boolean wifi;

    public Wifi(boolean wifi, Sms sms) {
        super(sms, key, STATE_TYPE.CONFIRMED, cListAddType);
        this.wifi = wifi;
    }

    public Wifi() {
        super(new Sms(), key, STATE_TYPE.CONFIRMED, cListAddType);
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
