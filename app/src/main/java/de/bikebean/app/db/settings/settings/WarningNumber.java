package de.bikebean.app.db.settings.settings;

import de.bikebean.app.db.settings.Setting;
import de.bikebean.app.db.sms.Sms;
import de.bikebean.app.db.state.State;

public class WarningNumber extends Setting {

    private static final State.KEY key = State.KEY.WARNING_NUMBER;
    private static final C_LIST_ADD_TYPE cListAddType = C_LIST_ADD_TYPE.REPLACE_IF_NEWER;

    private final String warningNumber;

    public WarningNumber(String warningNumber, Sms sms) {
        super(sms, key, STATE_TYPE.CONFIRMED_LONG, cListAddType);
        this.warningNumber = warningNumber;
    }

    @Override
    public String get() {
        return warningNumber;
    }
}
