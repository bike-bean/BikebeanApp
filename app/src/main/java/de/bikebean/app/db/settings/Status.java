package de.bikebean.app.db.settings;

import de.bikebean.app.db.sms.Sms;
import de.bikebean.app.db.state.State;
import de.bikebean.app.db.state.StateList;

public class Status extends Setting {

    private final double status;

    public Status(double status, Sms sms) {
        this.status = status;
        this.sms = sms;
        this.key = State.KEY._STATUS;
    }

    @Override
    public Double get() {
        return status;
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
