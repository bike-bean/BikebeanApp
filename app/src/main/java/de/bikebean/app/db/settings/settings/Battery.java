package de.bikebean.app.db.settings.settings;

import androidx.annotation.NonNull;

import de.bikebean.app.db.settings.Setting;
import de.bikebean.app.db.sms.Sms;
import de.bikebean.app.db.state.State;
import de.bikebean.app.ui.utils.sms.parser.SmsParser;

public class Battery extends Setting {

    private final double battery;
    private final @NonNull State state;
    private final @NonNull ConversationListAdder conversationListAdder;

    private static final State.KEY key = State.KEY.BATTERY;
    private static final State.STATUS status = State.STATUS.CONFIRMED;

    public Battery(@NonNull SmsParser smsParser, boolean isStatus, boolean isNoWifi) {
        super(smsParser.getSms(), key);

        if (isStatus)
            battery = smsParser.getStatusBattery();
        else if (isNoWifi)
            battery = smsParser.getBatteryNoWifi();
        else
            battery = smsParser.getBattery();

        conversationListAdder = super::addToList;

        state = new State(
                getDate(), key, get(), "",
                status, getId()
        );
    }

    public Battery() {
        super(new Sms(), key);

        battery = -1.0;
        conversationListAdder = super::addToList;

        state = new State(
                getDate(), key, get(), "",
                State.STATUS.UNSET, getId()
        );
    }

    @Override
    public final @NonNull State getState() {
        return state;
    }

    @Override
    public final @NonNull ConversationListAdder getConversationListAdder() {
        return conversationListAdder;
    }

    @Override
    public final @NonNull Double get() {
        return battery;
    }
}
