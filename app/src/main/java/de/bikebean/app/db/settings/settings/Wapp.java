package de.bikebean.app.db.settings.settings;

import de.bikebean.app.db.settings.Setting;
import de.bikebean.app.db.sms.Sms;
import de.bikebean.app.db.state.State;

public class Wapp extends Setting {

    private static final State.KEY key = State.KEY.WAPP;

    private final double wapp;

    public Wapp(double wapp, Sms sms) {
        super(sms, key);
        this.wapp= wapp;
    }

    public Wapp() {
        super(new Sms(), key);
        this.wapp = 0.0;
    }

    @Override
    public final Double get() {
        return wapp;
    }

    @Override
    public final State getState(){
        return new State(getDate(), key, get(), "", State.STATUS.PENDING, getId());
    }

    @Override
    public final ConversationListAdder getConversationListAdder() {
        return super::addToList;
    }
}
