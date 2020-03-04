package de.bikebean.app.db.settings.settings;

import de.bikebean.app.db.settings.Setting;
import de.bikebean.app.db.sms.Sms;
import de.bikebean.app.db.state.State;

public class LowBattery extends Setting {

    private static final State.KEY key = State.KEY.BATTERY;
    private static final C_LIST_ADD_TYPE cListAddType = C_LIST_ADD_TYPE.ADD_TO_LIST;

    private final double battery;

    public LowBattery(double battery, Sms sms) {
        super(sms, key, STATE_TYPE.CONFIRMED, cListAddType);
        this.battery = battery;
    }

    @Override
    public Double get() {
        return battery;
    }
}
