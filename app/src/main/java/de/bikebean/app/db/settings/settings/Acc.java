package de.bikebean.app.db.settings.settings;

import de.bikebean.app.db.settings.Setting;
import de.bikebean.app.db.sms.Sms;
import de.bikebean.app.db.state.State;

public class Acc extends Setting {

    private static final State.KEY key = State.KEY.ACC;
    private static final C_LIST_ADD_TYPE cListAddType = C_LIST_ADD_TYPE.ADD_TO_LIST;

    private final double acc;

    public Acc(double acc, Sms sms) {
        super(sms, key, STATE_TYPE.CONFIRMED_NEWER, cListAddType);
        this.acc = acc;
    }

    @Override
    public Double get() {
        return acc;
    }
}
