package de.bikebean.app.db.settings;

import de.bikebean.app.db.sms.Sms;
import de.bikebean.app.db.state.State;
import de.bikebean.app.db.state.StateList;

public class CellTowers extends Setting {

    private final String cellTowers;

    public CellTowers(String cellTowers, Sms sms) {
        this.cellTowers = cellTowers;
        this.sms = sms;
        this.key = State.KEY.CELL_TOWERS;
    }

    @Override
    public String get() {
        return cellTowers;
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
