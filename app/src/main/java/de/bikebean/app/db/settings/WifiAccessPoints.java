package de.bikebean.app.db.settings;

import de.bikebean.app.db.sms.Sms;
import de.bikebean.app.db.state.State;
import de.bikebean.app.db.state.StateList;

public class WifiAccessPoints extends Setting {

    private final String wifiAccessPoints;

    public WifiAccessPoints(String wifiAccessPoints, Sms sms) {
        this.wifiAccessPoints = wifiAccessPoints;
        this.sms = sms;
        this.key = State.KEY.WIFI_ACCESS_POINTS;
    }

    @Override
    public String get() {
        return wifiAccessPoints;
    }

    @Override
    void addStatusEntry(StateList newStateEntries) {
        addStatusEntryConfirmed(newStateEntries, true);
    }

    @Override
    void addToConversationList(SettingsList conversationList) {
        conversationList.add(this);
    }
}
