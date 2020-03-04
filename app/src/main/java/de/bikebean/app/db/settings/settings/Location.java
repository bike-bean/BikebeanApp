package de.bikebean.app.db.settings.settings;

import de.bikebean.app.db.settings.Setting;
import de.bikebean.app.db.sms.Sms;
import de.bikebean.app.db.state.State;

public class Location extends Setting {

    private final static State.KEY key = State.KEY.LOCATION;
    private final static C_LIST_ADD_TYPE cListAddType = C_LIST_ADD_TYPE.ADD_TO_LIST;

    private final double location;

    public Location(double location, Sms sms) {
        super(sms, key, STATE_TYPE.CONFIRMED_NEWER, cListAddType);
        this.location = location;
    }

    public Location() {
        super(new Sms(), key, STATE_TYPE.UNSET, cListAddType);
        this.location = 0.0;
    }

    public Location(Wapp wapp) {
        super(wapp.getSms(), key, STATE_TYPE.PENDING, cListAddType);
        this.location = 0.0;
    }

    @Override
    public Double get() {
        return location;
    }
}
