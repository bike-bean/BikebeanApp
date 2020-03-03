package de.bikebean.app.db.settings.settings;

import de.bikebean.app.db.settings.Setting;
import de.bikebean.app.db.sms.Sms;
import de.bikebean.app.db.state.State;

public class Acc extends Setting {

    private final double acc;

    public Acc(double acc, Sms sms) {
        super(sms, State.KEY.ACC);
        conversationListAdder = super::addToList;
        stateGetter = super::getStateConfirmedNewer;

        this.acc = acc;
    }

    @Override
    public Double get() {
        return acc;
    }
}
