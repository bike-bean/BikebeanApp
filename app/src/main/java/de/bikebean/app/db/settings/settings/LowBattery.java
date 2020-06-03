package de.bikebean.app.db.settings.settings;

import de.bikebean.app.db.settings.Setting;
import de.bikebean.app.db.state.State;
import de.bikebean.app.ui.utils.sms.parser.SmsParser;

public class LowBattery extends Setting {

    private final double battery;

    private static final State.KEY key = State.KEY.BATTERY;

    public LowBattery(SmsParser smsParser) {
        super(smsParser.getSms(), key);

        this.battery = smsParser.getLowBattery();
    }

    @Override
    public final State getState() {
        return new State(
                getDate(), key, get(), "",
                State.STATUS.CONFIRMED, getId()
        );
    }

    @Override
    public final ConversationListAdder getConversationListAdder() {
        return super::addToList;
    }

    @Override
    public final Double get() {
        return battery;
    }
}
