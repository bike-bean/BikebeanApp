package de.bikebean.app.db.settings.settings;

import de.bikebean.app.db.settings.Setting;
import de.bikebean.app.db.sms.Sms;
import de.bikebean.app.db.state.State;

public class Location extends Setting {

    private final static State.KEY key = State.KEY.LOCATION;
    private final double location;

    public Location(double location, Sms sms) {
        super(sms, key);
        conversationListAdder = super::addToList;
        stateGetter = super::getStateConfirmedNewer;

        this.location = location;
    }

    public Location() {
        super(new Sms(), key);
        conversationListAdder = super::addToList;
        stateGetter = super::getStateUnset;

        this.location = 0.0;
    }

    public Location(Wapp wapp) {
        super(wapp.getSms(), key);
        conversationListAdder = super::addToList;
        stateGetter = super::getStatePending;

        this.location = 0.0;
    }

    @Override
    public Double get() {
        return location;
    }
}
