package de.bikebean.app.db.settings.settings.replace_if_newer_settings;

import androidx.annotation.NonNull;

import de.bikebean.app.db.settings.settings.ReplaceIfNewerSetting;
import de.bikebean.app.db.sms.Sms;
import de.bikebean.app.db.sms.SmsFactory;
import de.bikebean.app.db.state.State;
import de.bikebean.app.db.state.StateFactory;

public class Wifi extends ReplaceIfNewerSetting {

    public static final boolean INITIAL_WIFI = false;

    private static final @NonNull State.KEY key = State.KEY.WIFI;

    public Wifi(final @NonNull Sms sms, final @NonNull WifiGetter wifiGetter) {
        super(sms,
                StateFactory.createNumberState(
                        sms, key, wifiGetter.getWifi() ? 1.0 : 0.0, State.STATUS.CONFIRMED
                )
        );
    }

    public Wifi(boolean wifi, final @NonNull Sms sms) {
        super(sms,
                StateFactory.createNumberState(
                        sms, key, wifi ? 1.0 : 0.0,
                        State.STATUS.CONFIRMED
                )
        );
    }

    public Wifi() {
        super(SmsFactory.createNullSms(),
                StateFactory.createNumberState(
                        SmsFactory.createNullSms(), key, INITIAL_WIFI ? 1.0 : 0.0,
                        State.STATUS.CONFIRMED
                )
        );
    }

    public interface WifiGetter {
        boolean getWifi();
    }
}
