package de.bikebean.app.db.settings;

import java.util.ArrayList;
import java.util.List;

import de.bikebean.app.db.sms.Sms;
import de.bikebean.app.db.state.State;

public abstract class Setting {
    Sms sms;
    State.KEY key;

    public long getDate() {
        return sms.getTimestamp();
    }

    public State.KEY getKey() {
        return key;
    }

    private int getId() {
        return sms.getId();
    }

    protected abstract Object get();

    public State[] updatePreferences(Setting[] settingList) {
        /*
        Basically takes a list of Settings and converts them into a list of State's.

        This happens through the magical properties of "Setting", which connects
        Settings to State's.
         */
        List<State> newStateEntries = new ArrayList<>();

        for (Setting setting : settingList)
            if (setting.getDate() != 0)
                switch (setting.getKey()) {
                    case WAPP:
                        setting.addStatusEntryPending(newStateEntries);
                        break;
                    case WARNING_NUMBER:
                    case CELL_TOWERS:
                    case WIFI_ACCESS_POINTS:
                        setting.addStatusEntryConfirmed(newStateEntries, true);
                        break;
                    case _STATUS:
                    case BATTERY:
                    case INTERVAL:
                    case WIFI:
                    case LOCATION:
                    case LAT:
                    case LNG:
                    case ACC:
                    case NO_CELL_TOWERS:
                    case NO_WIFI_ACCESS_POINTS:
                        setting.addStatusEntryConfirmed(newStateEntries, false);
                        break;
                }

        return newStateEntries.toArray(new State[]{});
    }

    private void addStatusEntryConfirmed(List<State> entries, boolean takeLong) {
        if (takeLong)
            entries.add(new State(
                    getDate(), getKey(), 0.0, (String) get(), State.STATUS.CONFIRMED, getId())
            );
        else
            entries.add(new State(
                    getDate(), getKey(), (double) get(), "", State.STATUS.CONFIRMED, getId())
            );
    }

    private void addStatusEntryPending(List<State> entries) {
        entries.add(new State(
                getDate(), getKey(), (Double) get(), "", State.STATUS.PENDING, getId())
        );
    }
}
