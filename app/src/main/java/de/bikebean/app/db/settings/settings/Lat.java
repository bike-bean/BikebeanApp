package de.bikebean.app.db.settings.settings;

import de.bikebean.app.db.settings.Setting;
import de.bikebean.app.db.sms.Sms;
import de.bikebean.app.db.state.State;

public class Lat extends Setting {

    private static final State.KEY key = State.KEY.LAT;
    private static final C_LIST_ADD_TYPE cListAddType = C_LIST_ADD_TYPE.ADD_TO_LIST;

    private final double lat;

    public Lat(double lat, Sms sms) {
        super(sms, key, STATE_TYPE.CONFIRMED_NEWER, cListAddType);
        this.lat = lat;
    }

    @Override
    public Double get() {
        return lat;
    }
}
