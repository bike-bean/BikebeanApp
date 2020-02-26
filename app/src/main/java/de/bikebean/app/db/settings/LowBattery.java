package de.bikebean.app.db.settings;

import de.bikebean.app.db.sms.Sms;
import de.bikebean.app.db.state.State;
import de.bikebean.app.db.state.StateList;

public class LowBattery extends Setting {

    private final double battery;

    public LowBattery(double battery, Sms sms) {
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
    void addToConversationList(SettingsList conversationList) {
        conversationList.add(this);
    }
}
