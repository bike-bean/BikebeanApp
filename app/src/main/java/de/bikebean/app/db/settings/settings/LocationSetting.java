package de.bikebean.app.db.settings.settings;

import androidx.annotation.NonNull;

import de.bikebean.app.db.settings.Setting;
import de.bikebean.app.db.sms.Sms;
import de.bikebean.app.db.state.State;

public abstract class LocationSetting extends Setting {

    private final double location;
    private final @NonNull State state;

    protected LocationSetting(double location, @NonNull WappState wappState, State.KEY key) {
        super(wappState.getSms(), key);

        this.location = location;

        this.state = new State(
                getDate() + 1, key, get(), "",
                State.STATUS.CONFIRMED, getId()
        );
    }

    protected LocationSetting(@NonNull Sms sms, State.KEY key, State.STATUS status) {
        super(sms, key);

        location = 0.0;

        state = new State(
                getDate(), key, get(), "",
                status, getId()
        );
    }

    @Override
    public final @NonNull Double get() {
        return location;
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
