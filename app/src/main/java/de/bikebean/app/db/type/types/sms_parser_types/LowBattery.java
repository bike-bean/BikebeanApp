package de.bikebean.app.db.type.types.sms_parser_types;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

import de.bikebean.app.db.settings.Setting;
import de.bikebean.app.db.type.types.SmsParserType;
import de.bikebean.app.ui.utils.sms.parser.SmsParser;

public class LowBattery extends SmsParserType {

    private final List<Setting> settings;

    public LowBattery(@NonNull SmsParser smsParser) {
        super(SMSTYPE.LOW_BATTERY);
        this.mSmsParser = smsParser;
        this.settings = new ArrayList<>();

        settings.add(new de.bikebean.app.db.settings.settings.LowBattery(mSmsParser));
    }

    @Override
    public List<Setting> getSettings() {
        return settings;
    }
}
