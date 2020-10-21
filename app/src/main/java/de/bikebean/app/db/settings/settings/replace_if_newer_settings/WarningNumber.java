package de.bikebean.app.db.settings.settings.replace_if_newer_settings;

import androidx.annotation.NonNull;

import de.bikebean.app.db.settings.settings.ReplaceIfNewerSetting;
import de.bikebean.app.db.sms.Sms;
import de.bikebean.app.db.state.State;
import de.bikebean.app.db.state.StateFactory;

public class WarningNumber extends ReplaceIfNewerSetting {

    private static final @NonNull State.KEY key = State.KEY.WARNING_NUMBER;

    private WarningNumber(final @NonNull Sms sms, final @NonNull String warningNumber,
                          final @NonNull State.STATUS status) {
        super(sms, StateFactory.createStringState(sms, key, warningNumber, status));
    }

    public WarningNumber(final @NonNull Sms sms,
                         final @NonNull WarningNumberGetter warningNumberGetter) {
        this(sms, warningNumberGetter.getWarningNumber(), State.STATUS.CONFIRMED);
    }

    public WarningNumber(final @NonNull Sms sms, final @NonNull String warningNumber) {
        this(sms, warningNumber, State.STATUS.UNSET);
    }

    public interface WarningNumberGetter {
        @NonNull String getWarningNumber();
    }
}
