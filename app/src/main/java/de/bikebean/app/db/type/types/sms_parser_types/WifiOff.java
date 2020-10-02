package de.bikebean.app.db.type.types.sms_parser_types;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

import de.bikebean.app.db.settings.Setting;
import de.bikebean.app.db.settings.settings.Battery;
import de.bikebean.app.db.settings.settings.replace_if_newer_settings.Status;
import de.bikebean.app.db.settings.settings.replace_if_newer_settings.Wifi;
import de.bikebean.app.db.type.types.SmsParserType;
import de.bikebean.app.ui.utils.sms.parser.SmsParser;

public class WifiOff extends SmsParserType {

    private final List<Setting> settings;

    public WifiOff(@NonNull SmsParser smsParser) {
        super(SMSTYPE.WIFI_OFF);
        this.mSmsParser = smsParser;
        this.settings = new ArrayList<>();

        settings.add(new Battery(mSmsParser, true, false));
        settings.add(new Wifi(false, smsParser.getSms()));
        settings.add(new Status(mSmsParser));
    }

    @Override
    public List<Setting> getSettings() {
        return settings;
    }
}
