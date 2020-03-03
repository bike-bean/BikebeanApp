package de.bikebean.app.db.settings.settings;

import de.bikebean.app.db.settings.Setting;
import de.bikebean.app.db.sms.Sms;
import de.bikebean.app.db.state.State;

public class Battery extends Setting {

    private final double battery;

    public Battery(double battery, Sms sms) {
        super(sms, State.KEY.BATTERY);
        conversationListAdder = super::addToList;
        stateGetter = super::getStateConfirmed;

        this.battery = battery;
    }

    public Battery() {
        super(new Sms(), State.KEY.BATTERY);
        conversationListAdder = super::addToList;
        stateGetter = super::getStateUnset;

        this.battery = -1.0;
    }

    @Override
    public Double get() {
        return battery;
    }
}
