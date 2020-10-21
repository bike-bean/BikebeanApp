package de.bikebean.app.db.settings.settings;

import androidx.annotation.NonNull;

import java.util.List;

import de.bikebean.app.db.settings.Setting;
import de.bikebean.app.db.sms.Sms;
import de.bikebean.app.db.sms.SmsFactory;
import de.bikebean.app.db.state.State;
import de.bikebean.app.db.state.StateFactory;

public abstract class NumberSetting extends Setting {

    private final @NonNull List<? extends RawNumberSettings> list;
    private final @NonNull State numberState;

    protected NumberSetting(final @NonNull String wappString, final @NonNull Sms sms,
                            final @NonNull State.KEY key, final @NonNull State numberState,
                            final @NonNull List<? extends RawNumberSettings> list) {
        super(
                StateFactory.createStringState(
                        sms, key, wappString, State.STATUS.CONFIRMED
                ),
                Setting::addToList
        );

        this.numberState = numberState;
        this.list = list;
    }

    public NumberSetting(final @NonNull String wappString, final @NonNull State.KEY key,
                         final @NonNull State numberState,
                         final @NonNull List<? extends RawNumberSettings> list) {
        super(
                StateFactory.createStringState(
                        SmsFactory.createNullSms(), key,
                        wappString, State.STATUS.UNSET),
                Setting::addToList
        );

        this.numberState = numberState;
        this.list = list;
    }

    public final @NonNull List<? extends RawNumberSettings> getList() {
        return list;
    }

    public final int getNumber() {
        return getNumberState().getValue().intValue();
    }

    public final @NonNull State getNumberState() {
        return numberState;
    }

    public abstract static class RawNumberSettings {}
}
