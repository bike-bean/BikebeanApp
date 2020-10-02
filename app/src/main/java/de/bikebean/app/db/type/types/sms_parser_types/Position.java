package de.bikebean.app.db.type.types.sms_parser_types;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

import de.bikebean.app.db.settings.Setting;
import de.bikebean.app.db.type.types.SmsParserType;
import de.bikebean.app.ui.utils.sms.parser.SmsParser;

public class Position extends SmsParserType {

    private final List<Setting> settings;

    public Position(@NonNull SmsParser smsParser) {
        super(SMSTYPE.POSITION);
        this.mSmsParser = smsParser;
        this.settings = new ArrayList<>();
    }

    @Override
    public final List<Setting> getSettings() {
        return settings;
    }
}
