package de.bikebean.app.db.settings.settings.replace_if_newer_settings;

import androidx.annotation.NonNull;

import de.bikebean.app.db.settings.settings.ReplaceIfNewerSetting;
import de.bikebean.app.db.sms.Sms;
import de.bikebean.app.db.state.State;
import de.bikebean.app.ui.utils.sms.parser.SmsParser;

public class Wifi extends ReplaceIfNewerSetting {

    private static final State.KEY key = State.KEY.WIFI;
    private static final boolean INITIAL_WIFI = false;

    private final boolean wifi;

    public Wifi(@NonNull SmsParser smsParser) {
        super(smsParser.getSms(), key);
        this.wifi = smsParser.getStatusWifi();
    }

    public Wifi(boolean wifi, @NonNull Sms sms) {
        super(sms, key);
        this.wifi = wifi;
    }

    public Wifi() {
        super(new Sms(), key);
        this.wifi = INITIAL_WIFI;
    }

    @Override
    public @NonNull Double get() {
        return wifi ? 1.0 : 0.0;
    }

    @Override
    public @NonNull State getState() {
        return new State(getDate(), key, get(), "", State.STATUS.CONFIRMED, getId());
    }

    public boolean getRaw() {
        return wifi;
    }
}
