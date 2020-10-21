package de.bikebean.app.db.settings.settings.replace_if_newer_settings;

import androidx.annotation.NonNull;

import de.bikebean.app.db.settings.settings.ReplaceIfNewerSetting;
import de.bikebean.app.db.sms.Sms;
import de.bikebean.app.db.sms.SmsFactory;
import de.bikebean.app.db.state.State;
import de.bikebean.app.db.state.StateFactory;

public class Interval extends ReplaceIfNewerSetting {

    public static final int INITIAL_INTERVAL = 1;
    private static final @NonNull State.KEY key = State.KEY.INTERVAL;

    private Interval(final @NonNull Sms sms, final int interval) {
        super(sms,
                StateFactory.createNumberState(
                        sms, key, interval, State.STATUS.CONFIRMED
                )
        );
    }

    public Interval(final @NonNull Sms sms, final @NonNull StatusGetter statusGetter) {
        this(sms, statusGetter.getStatus());
    }

    public Interval() {
        this(SmsFactory.createNullSms(), INITIAL_INTERVAL);
    }

    public interface StatusGetter {
        int getStatus();
    }
}
