package de.bikebean.app.db.settings.settings;

import de.bikebean.app.db.settings.Setting;
import de.bikebean.app.db.sms.Sms;
import de.bikebean.app.db.state.State;

public class LowBattery extends Setting {

    private final double battery;

    public LowBattery(double battery, Sms sms) {
        super(sms, State.KEY.BATTERY);
        conversationListAdder = super::addToList;
        stateGetter = super::getStateConfirmed;

        this.battery = battery;
    }

    @Override
    public Double get() {
        return battery;
    }
}
