package de.bikebean.app.db.settings.settings;

import androidx.annotation.NonNull;

import de.bikebean.app.db.settings.Setting;
import de.bikebean.app.db.sms.Sms;
import de.bikebean.app.db.state.State;
import de.bikebean.app.db.state.StateFactory;

public class LowBattery extends Setting {

    public LowBattery(final @NonNull Sms sms, final @NonNull LowBatteryGetter lowBatteryGetter) {
        super(
                StateFactory.createNumberState(
                        sms, State.KEY.BATTERY, lowBatteryGetter.getLowBattery(),
                        State.STATUS.CONFIRMED
                ),
                Setting::addToList
        );
    }

    public interface LowBatteryGetter {
        double getLowBattery();
    }
}
