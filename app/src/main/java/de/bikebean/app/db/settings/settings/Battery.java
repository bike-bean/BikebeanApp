package de.bikebean.app.db.settings.settings;

import androidx.annotation.NonNull;

import de.bikebean.app.db.settings.Setting;
import de.bikebean.app.db.sms.Sms;
import de.bikebean.app.db.sms.SmsFactory;
import de.bikebean.app.db.state.State;
import de.bikebean.app.db.state.StateFactory;

public class Battery extends Setting {

    public static final double UNSET_BATTERY = -1.0;
    private static final @NonNull State.KEY key = State.KEY.BATTERY;

    public Battery(final @NonNull Sms sms, final @NonNull BatteryGetter batteryGetter) {
        super(
                StateFactory.createNumberState(
                        sms, key, batteryGetter.getBattery(), State.STATUS.CONFIRMED
                ),
                Setting::addToList
        );
    }

    public Battery() {
        super(
                StateFactory.createNumberState(SmsFactory.createNullSms(), key, UNSET_BATTERY, State.STATUS.UNSET),
                Setting::addToList
        );
    }

    public interface BatteryGetter {
        double getBattery();
    }
}
