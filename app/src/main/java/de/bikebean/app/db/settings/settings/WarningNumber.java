package de.bikebean.app.db.settings.settings;

import de.bikebean.app.db.settings.Setting;
import de.bikebean.app.db.sms.Sms;
import de.bikebean.app.db.state.State;

public class WarningNumber extends Setting {

    private final String warningNumber;

    public WarningNumber(String warningNumber, Sms sms) {
        super(sms, State.KEY.WARNING_NUMBER);
        conversationListAdder = super::replaceIfNewer;
        stateGetter = super::getStateConfirmedLong;

        this.warningNumber = warningNumber;
    }

    @Override
    public String get() {
        return warningNumber;
    }
}
