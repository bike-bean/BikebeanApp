package de.bikebean.app.db.type;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

import de.bikebean.app.db.settings.Setting;

public abstract class SmsType {

    private final @NonNull TYPE smsType;
    private final @NonNull List<Setting> settings;

    public SmsType(final @NonNull TYPE type) {
        smsType = type;
        settings = new ArrayList<>();
    }

    public @NonNull List<Setting> getSettings() {
        return settings;
    }

    public @NonNull TYPE getSmsType() {
        return smsType;
    }

    public enum TYPE {
        POSITION, STATUS, WIFI_ON, WIFI_OFF, INT, LOW_BATTERY,
        WARNING_NUMBER, CELL_TOWERS, WIFI_LIST, NO_WIFI_LIST,
        INITIAL, LOCATION, INITIAL_CONVERSATION
    }
}
