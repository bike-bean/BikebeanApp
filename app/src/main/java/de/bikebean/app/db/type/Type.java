package de.bikebean.app.db.type;

import java.util.List;

import de.bikebean.app.db.settings.Setting;

public abstract class Type {

    public enum SMSTYPE {
        POSITION, STATUS, WIFI_ON, WIFI_OFF, INT, LOW_BATTERY,
        WARNING_NUMBER, CELL_TOWERS, WIFI_LIST, NO_WIFI_LIST,
        UNDEFINED, INITIAL, LOCATION, INITIAL_CONVERSATION
    }

    public abstract List<Setting> getSettings();

    public final SMSTYPE smsType;

    public Type(SMSTYPE t) {
        smsType = t;
    }
}
