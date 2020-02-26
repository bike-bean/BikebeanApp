package de.bikebean.app.db.settings;

import de.bikebean.app.db.sms.Sms;
import de.bikebean.app.db.state.State;
import de.bikebean.app.db.state.StateList;

public class Wifi extends Setting {

    private final boolean wifi;

    public Wifi(boolean wifi, Sms sms) {
        this.wifi = wifi;
        this.sms = sms;
        this.key = State.KEY.WIFI;
    }

    @Override
    public Double get() {
        return wifi ? 1.0 : 0.0;
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
