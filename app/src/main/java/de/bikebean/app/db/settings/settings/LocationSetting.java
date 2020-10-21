package de.bikebean.app.db.settings.settings;

import androidx.annotation.NonNull;

import de.bikebean.app.db.settings.Setting;
import de.bikebean.app.db.sms.Sms;
import de.bikebean.app.db.sms.SmsFactory;
import de.bikebean.app.db.state.State;
import de.bikebean.app.db.state.StateFactory;

public abstract class LocationSetting extends Setting {

    protected LocationSetting(double location, final @NonNull Sms sms,
                              final @NonNull State.KEY key) {
        super(
                StateFactory.createStateWithEnum(
                        sms.getTimestamp() + 1, key, location, "",
                        State.STATUS.CONFIRMED, sms.getId()
                ),
                Setting::addToList
        );
    }

    protected LocationSetting(final @NonNull Sms sms, final @NonNull State.KEY key) {
        super(
                StateFactory.createNumberState(
                        sms, key, 0.0, State.STATUS.PENDING),
                Setting::addToList
        );
    }

    protected LocationSetting(final @NonNull State.KEY key) {
        super(
                StateFactory.createNumberState(
                        SmsFactory.createNullSms(), key, 0.0, State.STATUS.UNSET),
                Setting::addToList
        );
    }
}
