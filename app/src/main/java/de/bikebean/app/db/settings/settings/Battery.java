package de.bikebean.app.db.settings.settings;

import de.bikebean.app.db.settings.Setting;
import de.bikebean.app.db.sms.Sms;
import de.bikebean.app.db.state.State;

public class Battery extends Setting {

    private static final State.KEY key = State.KEY.BATTERY;
    private static final C_LIST_ADD_TYPE cListAddType = C_LIST_ADD_TYPE.ADD_TO_LIST;

    private final double battery;

    public Battery(double battery, Sms sms) {
        super(sms, key, STATE_TYPE.CONFIRMED, cListAddType);
        this.battery = battery;
    }

    public Battery() {
        super(new Sms(), key, STATE_TYPE.UNSET, cListAddType);
        this.battery = -1.0;
    }

    @Override
    public Double get() {
        return battery;
    }
}
