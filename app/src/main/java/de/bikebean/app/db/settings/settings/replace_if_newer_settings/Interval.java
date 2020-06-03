package de.bikebean.app.db.settings.settings.replace_if_newer_settings;

import de.bikebean.app.db.settings.settings.ReplaceIfNewerSetting;
import de.bikebean.app.db.sms.Sms;
import de.bikebean.app.db.state.State;
import de.bikebean.app.ui.utils.sms.parser.SmsParser;

public class Interval extends ReplaceIfNewerSetting {

    private static final State.KEY key = State.KEY.INTERVAL;
    private static final int INITIAL_INTERVAL = 1;

    private final int interval;

    public Interval(SmsParser smsParser, boolean isStatus) {
        super(smsParser.getSms(), key);

        if (isStatus)
            this.interval = smsParser.getStatusInterval();
        else
            this.interval = smsParser.getInterval();
    }

    public Interval() {
        super(new Sms(), key);
        this.interval = INITIAL_INTERVAL;
    }

    @Override
    public final Double get() {
        return (double) interval;
    }

    @Override
    public final State getState() {
        return new State(getDate(), key, get(), "", State.STATUS.CONFIRMED, getId());
    }
}
