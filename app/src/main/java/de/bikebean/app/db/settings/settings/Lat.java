package de.bikebean.app.db.settings.settings;

import de.bikebean.app.db.settings.Setting;
import de.bikebean.app.db.sms.Sms;
import de.bikebean.app.db.state.State;

public class Lat extends Setting {

    private final double lat;

    public Lat(double lat, Sms sms) {
        super(sms, State.KEY.LAT);
        conversationListAdder = super::addToList;
        stateGetter = super::getStateConfirmedNewer;

        this.lat = lat;
    }

    @Override
    public Double get() {
        return lat;
    }
}
