package de.bikebean.app.db.settings.settings;

import de.bikebean.app.db.settings.Setting;
import de.bikebean.app.db.sms.Sms;
import de.bikebean.app.db.state.State;

public class Status extends Setting {

    private static final State.KEY key = State.KEY._STATUS;
    private static final C_LIST_ADD_TYPE cListAddType = C_LIST_ADD_TYPE.REPLACE_IF_NEWER;

    private final double status;

    public Status(double status, Sms sms) {
        super(sms, key, STATE_TYPE.CONFIRMED, cListAddType);
        this.status = status;
    }

    public Status() {
        super(new Sms(), key, STATE_TYPE.UNSET, cListAddType);
        this.status = 0.0;
    }

    @Override
    public Double get() {
        return status;
    }
}
