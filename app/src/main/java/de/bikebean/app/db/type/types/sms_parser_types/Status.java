package de.bikebean.app.db.type.types.sms_parser_types;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import de.bikebean.app.db.settings.Setting;
import de.bikebean.app.db.settings.settings.Battery;
import de.bikebean.app.db.settings.settings.replace_if_newer_settings.WarningNumber;
import de.bikebean.app.db.settings.settings.replace_if_newer_settings.Wifi;
import de.bikebean.app.db.type.types.SmsParserType;
import de.bikebean.app.ui.main.status.menu.log.LogViewModel;
import de.bikebean.app.ui.utils.sms.parser.SmsParser;

public class Status extends SmsParserType {

    private final List<Setting> settings;

    public Status(SmsParser smsParser, WeakReference<LogViewModel> logViewModelReference) {
        super(SMSTYPE.STATUS);
        this.mSmsParser = smsParser;
        this.settings = new ArrayList<>();

        settings.add(new WarningNumber(mSmsParser, true));
        settings.add(new de.bikebean.app.db.settings.settings.replace_if_newer_settings.Interval(mSmsParser, true));
        settings.add(new Wifi(mSmsParser));
        settings.add(new Battery(mSmsParser, true, false));
        settings.add(new de.bikebean.app.db.settings.settings.replace_if_newer_settings.Status(mSmsParser));

        if (logViewModelReference != null)
            logViewModelReference.get().w("WarningNumber is not set!");
    }

    @Override
    public final List<Setting> getSettings() {
        return settings;
    }
}
