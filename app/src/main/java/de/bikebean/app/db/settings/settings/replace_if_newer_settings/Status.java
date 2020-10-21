package de.bikebean.app.db.settings.settings.replace_if_newer_settings;

import androidx.annotation.NonNull;

import de.bikebean.app.db.settings.settings.ReplaceIfNewerSetting;
import de.bikebean.app.db.sms.Sms;
import de.bikebean.app.db.sms.SmsFactory;
import de.bikebean.app.db.state.State;
import de.bikebean.app.db.state.StateFactory;

public class Status extends ReplaceIfNewerSetting {

    private static final @NonNull
    State.KEY key = State.KEY._STATUS;

    public Status(final @NonNull Sms sms) {
        super(sms, StateFactory.createNumberState(sms, key, 0.0, State.STATUS.CONFIRMED));
    }

    public Status() {
        super(SmsFactory.createNullSms(),
                StateFactory.createNumberState(
                        SmsFactory.createNullSms(), key, 0.0, State.STATUS.UNSET
                )
        );
    }
}
