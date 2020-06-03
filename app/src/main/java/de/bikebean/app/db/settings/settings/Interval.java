package de.bikebean.app.db.settings.settings;

import de.bikebean.app.db.settings.Setting;
import de.bikebean.app.db.sms.Sms;
import de.bikebean.app.db.state.State;

public class Interval extends Setting {

    private static final State.KEY key = State.KEY.INTERVAL;
    private static final C_LIST_ADD_TYPE cListAddType = C_LIST_ADD_TYPE.REPLACE_IF_NEWER;

    private static final int INITIAL_INTERVAL = 1;

    private final int interval;

    public Interval(int interval, Sms sms) {
        super(sms, key, STATE_TYPE.CONFIRMED, cListAddType);
        this.interval = interval;
    }

    public Interval() {
        super(new Sms(), key, STATE_TYPE.CONFIRMED, cListAddType);
        this.interval = INITIAL_INTERVAL;
    }

    @Override
    public Double get() {
        return (double) interval;
    }
}
