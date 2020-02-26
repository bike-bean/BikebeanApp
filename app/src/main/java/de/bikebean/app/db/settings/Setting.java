package de.bikebean.app.db.settings;

import de.bikebean.app.db.sms.Sms;
import de.bikebean.app.db.state.State;
import de.bikebean.app.db.state.StateList;

public abstract class Setting {
    Sms sms;
    State.KEY key;

    private State.KEY getKey() {
        return key;
    }

    long getDate() {
        return sms.getTimestamp();
    }

    private int getId() {
        return sms.getId();
    }

    abstract Object get();

    abstract void addStatusEntry(StateList newStateEntries);

    private boolean equalsKey(Setting other) {
        return this.getKey().equals(other.getKey());
    }

    private boolean isNewer(Setting other) {
        return this.getDate() > other.getDate();
    }

    void addStatusEntryConfirmed(StateList entries, boolean takeLong) {
        if (takeLong)
            entries.add(new State(
                    getDate(), getKey(), 0.0, (String) get(), State.STATUS.CONFIRMED, getId())
            );
        else
            entries.add(new State(
                    getDate(), getKey(), (double) get(), "", State.STATUS.CONFIRMED, getId())
            );
    }

    void addStatusEntryPending(StateList entries) {
        entries.add(new State(
                getDate(), getKey(), (Double) get(), "", State.STATUS.PENDING, getId())
        );
    }

    void addToConversationList(SettingsList conversationList) {
        for (Setting intListItem : conversationList)
            if (equalsKey(intListItem) && isNewer(intListItem)) {
                conversationList._add(this).remove(intListItem);
                break;
            }
    }
}
