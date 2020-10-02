package de.bikebean.app.db.settings.settings;

import androidx.annotation.NonNull;

import java.util.List;

import de.bikebean.app.db.settings.Setting;
import de.bikebean.app.db.sms.Sms;
import de.bikebean.app.db.state.State;

public abstract class NumberSetting extends Setting {

    public abstract static class RawNumberSettings {}

    public abstract @NonNull List<? extends RawNumberSettings> getList();
    public abstract int getNumber();
    public abstract @NonNull State getNumberState();

    private final @NonNull State state;
    protected final @NonNull String mWappString;

    protected NumberSetting(@NonNull String wappString, Sms sms, State.KEY key) {
        super(sms, key);

        mWappString = wappString;
        state = new State(
                getDate(), key, 0.0, get(),
                State.STATUS.CONFIRMED, getId()
        );
    }

    public NumberSetting(@NonNull String wappString, State.KEY key) {
        super(new Sms(), key);

        mWappString = wappString;
        state = new State(
                getDate(), key, 0.0, get(),
                State.STATUS.UNSET, getId()
        );
    }

    @Override
    public final @NonNull String get() {
        return mWappString;
    }

    @Override
    public final @NonNull State getState() {
        return state;
    }

    @Override
    public final @NonNull ConversationListAdder getConversationListAdder() {
        return super::addToList;
    }
}
