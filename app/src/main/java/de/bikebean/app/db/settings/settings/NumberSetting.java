package de.bikebean.app.db.settings.settings;

import java.util.List;

import de.bikebean.app.db.settings.Setting;
import de.bikebean.app.db.sms.Sms;
import de.bikebean.app.db.state.State;

public abstract class NumberSetting extends Setting {

    public abstract static class RawNumberSettings {}

    public abstract List<? extends RawNumberSettings> getList();
    public abstract int getNumber();
    public abstract State getNumberState();

    private final State state;
    protected final String mWappString;

    protected NumberSetting(String wappString, Sms sms, State.KEY key) {
        super(sms, key);

        mWappString = wappString;

        this.state = new State(
                getDate(), key, 0.0, get(),
                State.STATUS.CONFIRMED, getId()
        );
    }

    public NumberSetting(String wappString, State.KEY key) {
        super(new Sms(), key);

        mWappString = wappString;
        this.state = new State(
                getDate(), key, 0.0, get(),
                State.STATUS.UNSET, getId()
        );
    }

    @Override
    public final String get() {
        return mWappString;
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
