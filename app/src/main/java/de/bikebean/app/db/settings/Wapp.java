package de.bikebean.app.db.settings;

import de.bikebean.app.db.sms.Sms;
import de.bikebean.app.db.state.State;
import de.bikebean.app.db.state.StateList;

public class Wapp extends Setting {

    private final double wapp;

    public Wapp(double wapp, Sms sms) {
        this.wapp= wapp;
        this.sms = sms;
        this.key = State.KEY.WAPP;
    }

    @Override
    public Double get() {
        return wapp;
    }

    @Override
    void addStatusEntry(StateList newStateEntries) {
        addStatusEntryPending(newStateEntries);
    }

    @Override
    void addToConversationList(SettingsList conversationList) {
        conversationList.add(this);
    }
}
