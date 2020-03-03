package de.bikebean.app.db.settings.settings;

import de.bikebean.app.db.settings.Setting;
import de.bikebean.app.db.sms.Sms;
import de.bikebean.app.db.state.State;

public class Interval extends Setting {

    private static final int INITIAL_INTERVAL = 1;

    private final int interval;

    public Interval(int interval, Sms sms) {
        super(sms, State.KEY.INTERVAL);
        conversationListAdder = super::replaceIfNewer;
        stateGetter = super::getStateConfirmed;

        this.interval = interval;
    }

    public Interval() {
        super(new Sms(), State.KEY.INTERVAL);
        conversationListAdder = super::replaceIfNewer;
        stateGetter = super::getStateConfirmed;

        this.interval = INITIAL_INTERVAL;
    }

    @Override
    public Double get() {
        return (double) interval;
    }
}
