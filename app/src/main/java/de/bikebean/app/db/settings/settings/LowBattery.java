package de.bikebean.app.db.settings.settings;

import androidx.annotation.NonNull;

import de.bikebean.app.db.settings.Setting;
import de.bikebean.app.db.state.State;
import de.bikebean.app.ui.utils.sms.parser.SmsParser;

public class LowBattery extends Setting {

    private final double battery;

    private static final State.KEY key = State.KEY.BATTERY;

    public LowBattery(@NonNull SmsParser smsParser) {
        super(smsParser.getSms(), key);

        battery = smsParser.getLowBattery();
    }

    @Override
    public final @NonNull State getState() {
        return new State(
                getDate(), key, get(), "",
                State.STATUS.CONFIRMED, getId()
        );
    }

    @Override
    public final @NonNull ConversationListAdder getConversationListAdder() {
        return super::addToList;
    }

    @Override
    public final @NonNull Double get() {
        return battery;
    }
}
