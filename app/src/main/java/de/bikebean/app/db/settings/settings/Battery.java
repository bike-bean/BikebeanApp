package de.bikebean.app.db.settings.settings;

import de.bikebean.app.db.settings.Setting;
import de.bikebean.app.db.sms.Sms;
import de.bikebean.app.db.state.State;
import de.bikebean.app.ui.utils.sms.parser.SmsParser;

public class Battery extends Setting {

    private final double battery;
    private final State state;
    private final ConversationListAdder conversationListAdder;

    private static final State.KEY key = State.KEY.BATTERY;
    private static final State.STATUS status = State.STATUS.CONFIRMED;

    public Battery(SmsParser smsParser, boolean isStatus, boolean isNoWifi) {
        super(smsParser.getSms(), key);

        if (isStatus)
            this.battery = smsParser.getStatusBattery();
        else if (isNoWifi)
            this.battery = smsParser.getBatteryNoWifi();
        else
            this.battery = smsParser.getBattery();

        this.conversationListAdder = super::addToList;

        this.state = new State(
                getDate(), key, get(), "",
                status, getId()
        );
    }

    public Battery() {
        super(new Sms(), key);

        this.battery = -1.0;
        this.conversationListAdder = super::addToList;

        this.state = new State(
                getDate(), key, get(), "",
                State.STATUS.UNSET, getId()
        );
    }

    @Override
    public final State getState() {
        return state;
    }

    @Override
    public final ConversationListAdder getConversationListAdder() {
        return conversationListAdder;
    }

    @Override
    public final Double get() {
        return battery;
    }
}
