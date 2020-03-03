package de.bikebean.app.db.settings.settings;

import de.bikebean.app.db.settings.Setting;
import de.bikebean.app.db.sms.Sms;
import de.bikebean.app.db.state.State;

public class Lng extends Setting {

    private final double lng;

    public Lng(double lng, Sms sms) {
        super(sms, State.KEY.LNG);
        conversationListAdder = super::addToList;
        stateGetter = super::getStateConfirmedNewer;

        this.lng = lng;
    }

    @Override
    public Double get() {
        return lng;
    }
}
