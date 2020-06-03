package de.bikebean.app.db.settings.settings.replace_if_newer_settings;

import de.bikebean.app.db.settings.settings.ReplaceIfNewerSetting;
import de.bikebean.app.db.sms.Sms;
import de.bikebean.app.db.state.State;
import de.bikebean.app.ui.utils.sms.parser.SmsParser;

public class Status extends ReplaceIfNewerSetting {

    private static final State.KEY key = State.KEY._STATUS;

    private final double status;
    private final State state;

    public Status(SmsParser smsParser) {
        super(smsParser.getSms(), key);
        this.status = 0.0;
        this.state = new State(getDate(), key, get(), "", State.STATUS.CONFIRMED, getId());
    }

    public Status() {
        super(new Sms(), key);
        this.status = 0.0;
        this.state = new State(getDate(), key, get(), "", State.STATUS.UNSET, getId());
    }

    @Override
    public final Double get() {
        return status;
    }

    @Override
    public final State getState() {
        return state;
    }
}
