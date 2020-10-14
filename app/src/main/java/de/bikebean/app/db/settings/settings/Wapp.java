package de.bikebean.app.db.settings.settings;

import androidx.annotation.NonNull;

import de.bikebean.app.db.settings.Setting;
import de.bikebean.app.db.sms.Sms;
import de.bikebean.app.db.state.State;
import de.bikebean.app.ui.utils.sms.parser.SmsParser;

public class Wapp extends Setting {

    private static final @NonNull State.KEY key = State.KEY.WAPP;

    private final double wapp;

    public Wapp(double wapp, final @NonNull SmsParser smsParser) {
        super(smsParser.getSms(), key);
        this.wapp = wapp;
    }

    public Wapp() {
        super(new Sms(), key);
        this.wapp = 0.0;
    }

    @Override
    public final @NonNull Double get() {
        return wapp;
    }

    @Override
    public final @NonNull State getState(){
        return new State(getDate(), key, get(), "", State.STATUS.PENDING, getId());
    }

    @Override
    public final @NonNull ConversationListAdder getConversationListAdder() {
        return super::addToList;
    }
}
