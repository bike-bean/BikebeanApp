package de.bikebean.app.db.settings.settings.replace_if_newer_settings;

import androidx.annotation.NonNull;

import de.bikebean.app.db.settings.settings.ReplaceIfNewerSetting;
import de.bikebean.app.db.sms.Sms;
import de.bikebean.app.db.state.State;
import de.bikebean.app.ui.utils.sms.parser.SmsParser;

public class Interval extends ReplaceIfNewerSetting {

    private static final State.KEY key = State.KEY.INTERVAL;
    private static final int INITIAL_INTERVAL = 1;

    private final int interval;

    public Interval(@NonNull SmsParser smsParser, boolean isStatus) {
        super(smsParser.getSms(), key);

        if (isStatus)
            interval = smsParser.getStatusInterval();
        else
            interval = smsParser.getInterval();
    }

    public Interval() {
        super(new Sms(), key);
        interval = INITIAL_INTERVAL;
    }

    @Override
    public final @NonNull Double get() {
        return (double) interval;
    }

    @Override
    public final @NonNull State getState() {
        return new State(getDate(), key, get(), "", State.STATUS.CONFIRMED, getId());
    }
}
