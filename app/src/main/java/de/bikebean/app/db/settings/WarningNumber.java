package de.bikebean.app.db.settings;

import de.bikebean.app.db.sms.Sms;
import de.bikebean.app.db.state.State;
import de.bikebean.app.db.state.StateList;

public class WarningNumber extends Setting {

    private final String warningNumber;

    public WarningNumber(String warningNumber, Sms sms) {
        this.warningNumber = warningNumber;
        this.sms = sms;
        this.key = State.KEY.WARNING_NUMBER;
    }

    @Override
    public String get() {
        return warningNumber;
    }

    @Override
    void addStatusEntry(StateList newStateEntries) {
        addStatusEntryConfirmed(newStateEntries, true);
    }

    @Override
    void addToConversationList(SettingsList conversationList) {
        super.addToConversationList(conversationList);
    }
}
