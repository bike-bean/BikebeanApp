package de.bikebean.app.db.settings.settings.replace_if_newer_settings;

import androidx.annotation.NonNull;

import de.bikebean.app.db.settings.settings.ReplaceIfNewerSetting;
import de.bikebean.app.db.sms.Sms;
import de.bikebean.app.db.state.State;
import de.bikebean.app.ui.utils.sms.parser.SmsParser;

public class WarningNumber extends ReplaceIfNewerSetting {

    private static final State.KEY key = State.KEY.WARNING_NUMBER;

    private final State state;

    private final String warningNumber;

    public WarningNumber(@NonNull SmsParser smsParser, boolean isStatus) {
        super(smsParser.getSms(), key);

        if (isStatus)
            this.warningNumber = smsParser.getStatusWarningNumber();
        else
            this.warningNumber = smsParser.getWarningNumber();

        this.state = new State(getDate(), key, 0.0, get(), State.STATUS.CONFIRMED, getId());
    }

    public WarningNumber(String warningNumber, Sms sms) {
        super(sms, key);
        this.warningNumber = warningNumber;
        this.state = new State(getDate(), key, 0.0, get(), State.STATUS.UNSET, getId());
    }

    @Override
    public String get() {
        return warningNumber;
    }

    @Override
    public State getState() {
        return state;
    }
}
