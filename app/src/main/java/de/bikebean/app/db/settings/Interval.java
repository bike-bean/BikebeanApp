package de.bikebean.app.db.settings;

import de.bikebean.app.db.sms.Sms;
import de.bikebean.app.db.state.State;
import de.bikebean.app.db.state.StateList;

public class Interval extends Setting {

    private final int interval;

    public Interval(int interval, Sms sms) {
        this.interval = interval;
        this.sms = sms;
        this.key = State.KEY.INTERVAL;
    }

    @Override
    public Double get() {
        return (double) interval;
    }

    @Override
    void addStatusEntry(StateList newStateEntries) {
        addStatusEntryConfirmed(newStateEntries, false);
    }

    @Override
    void addToConversationList(SettingsList conversationList) {
        super.addToConversationList(conversationList);
    }
}
