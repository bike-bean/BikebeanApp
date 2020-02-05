package de.bikebean.app.db.settings;

import java.util.ArrayList;
import java.util.List;

import de.bikebean.app.db.sms.Sms;
import de.bikebean.app.db.state.State;

public abstract class Setting {
    protected Sms sms;
    protected String key;

    public long getDate() {
        return sms.getTimestamp();
    }

    public String getKey() {
        return key;
    }

    public int getId() {
        return sms.getId();
    }

    public abstract Object get();

    public List<State> updatePreferences(List<Setting> settingList) {
        /*
        Basically takes a list of Settings and converts them into a list of State's.

        This happens through the magical properties of "Setting", which connects
        Settings to State's.
         */
        List<State> newStateEntries = new ArrayList<>();

        for (Setting setting : settingList)
            if (setting.getDate() != 0)
                if (setting.getKey().equals(State.KEY_CELL_TOWERS) || setting.getKey().equals(State.KEY_WIFI_ACCESS_POINTS))
                    setting.addStatusEntryPending(newStateEntries);
                else if (!setting.getKey().equals(State.KEY_WARNING_NUMBER))
                    setting.addStatusEntryConfirmed(newStateEntries, false);
                else
                    setting.addStatusEntryConfirmed(newStateEntries, true);

        return newStateEntries;
    }

    private void addStatusEntryConfirmed(List<State> entries, boolean takeLong) {
        if (takeLong)
            entries.add(new State(
                    getDate(), getKey(), 0.0, (String) get(),
                    State.STATUS_CONFIRMED, getId())
            );
        else
            entries.add(new State(
                    getDate(), getKey(), (double) get(), "",
                    State.STATUS_CONFIRMED, getId())
            );
    }

    private void addStatusEntryPending(List<State> entries) {
        entries.add(new State(
                getDate(), getKey(), 0.0, (String) get(), State.STATUS_PENDING, getId())
        );
    }
}
