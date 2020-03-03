package de.bikebean.app.db.settings.settings;

import de.bikebean.app.db.settings.Setting;
import de.bikebean.app.db.sms.Sms;
import de.bikebean.app.db.state.State;

public class Status extends Setting {

    private final double status;

    public Status(double status, Sms sms) {
        super(sms, State.KEY._STATUS);
        conversationListAdder = super::replaceIfNewer;
        stateGetter = super::getStateConfirmed;

        this.status = status;
    }

    public Status() {
        super(new Sms(), State.KEY._STATUS);
        conversationListAdder = super::replaceIfNewer;
        stateGetter = super::getStateUnset;

        this.status = 0.0;
    }

    @Override
    public Double get() {
        return status;
    }
}
