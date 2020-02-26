package de.bikebean.app.db.settings;

import de.bikebean.app.db.sms.Sms;
import de.bikebean.app.db.state.State;
import de.bikebean.app.db.state.StateList;

public class Battery extends Setting {

    private final double battery;

    public Battery(double battery, Sms sms) {
        this.battery = battery;
        this.sms = sms;
        this.key = State.KEY.BATTERY;
    }

    @Override
    public Double get() {
        return battery;
    }

    @Override
    void addStatusEntry(StateList newStateEntries) {
        addStatusEntryConfirmed(newStateEntries, false);
    }

    @Override
    public void addToConversationList(SettingsList conversationList) {
        conversationList.add(this);
    }
}
