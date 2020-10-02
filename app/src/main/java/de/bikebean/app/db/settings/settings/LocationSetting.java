package de.bikebean.app.db.settings.settings;

import de.bikebean.app.db.settings.Setting;
import de.bikebean.app.db.sms.Sms;
import de.bikebean.app.db.state.State;

public abstract class LocationSetting extends Setting {

    private final double location;
    private final State state;

    protected LocationSetting(double location, WappState wappState, State.KEY key) {
        super(wappState.getSms(), key);

        this.location = location;

        this.state = new State(
                getDate() + 1, key, get(), "",
                State.STATUS.CONFIRMED, getId()
        );
    }

    protected LocationSetting(Sms sms, State.KEY key, State.STATUS status) {
        super(sms, key);

        this.location = 0.0;

        this.state = new State(
                getDate(), key, get(), "",
                status, getId()
        );
    }

    @Override
    public final Double get() {
        return location;
    }

    @Override
    public final State getState() {
        return state;
    }

    @Override
    public final ConversationListAdder getConversationListAdder() {
        return super::addToList;
    }
}
