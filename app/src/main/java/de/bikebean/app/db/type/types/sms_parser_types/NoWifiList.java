package de.bikebean.app.db.type.types.sms_parser_types;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

import de.bikebean.app.db.settings.Setting;
import de.bikebean.app.db.settings.settings.Battery;
import de.bikebean.app.db.settings.settings.Wapp;
import de.bikebean.app.db.settings.settings.number_settings.WifiAccessPoints;
import de.bikebean.app.db.state.State;
import de.bikebean.app.db.type.types.SmsParserType;
import de.bikebean.app.ui.utils.sms.parser.SmsParser;

public class NoWifiList extends SmsParserType {

    private final @NonNull List<Setting> settings;

    public NoWifiList(final @NonNull SmsParser smsParser) {
        super(SMSTYPE.NO_WIFI_LIST);
        this.mSmsParser = smsParser;
        this.settings = new ArrayList<>();

        // battery value is encoded differently in this case
        settings.add(new WifiAccessPoints(smsParser));
        settings.add(new Wapp(State.WAPP_WIFI_ACCESS_POINTS, smsParser));
        settings.add(new Battery(mSmsParser, false, true));
    }

    @Override
    public @NonNull List<Setting> getSettings() {
        return settings;
    }
}
