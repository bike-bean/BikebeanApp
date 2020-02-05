package de.bikebean.app.db.settings;

import de.bikebean.app.db.sms.Sms;
import de.bikebean.app.db.state.State;

public class WarningNumber extends Setting {
    private String warningNumber;

    public WarningNumber(String warningNumber, Sms sms) {
        this.warningNumber = warningNumber;
        this.sms = sms;
        this.key = State.KEY_WARNING_NUMBER;
    }

    public String get() {
        return warningNumber;
    }
}
