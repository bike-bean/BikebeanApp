package de.bikebean.app.db.type.types.sms_parser_types;

import androidx.annotation.NonNull;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import de.bikebean.app.db.settings.Setting;
import de.bikebean.app.db.type.types.SmsParserType;
import de.bikebean.app.ui.main.status.menu.log.LogViewModel;
import de.bikebean.app.ui.utils.sms.parser.SmsParser;

public class Undefined extends SmsParserType {

    private final @NonNull List<Setting> settings;

    public Undefined(final @NonNull SmsParser smsParser,
                     final @NonNull WeakReference<LogViewModel> logViewModelReference) {
        super(SMSTYPE.UNDEFINED);
        this.mSmsParser = smsParser;
        this.settings = new ArrayList<>();

        logViewModelReference.get().w("Could not Parse SMS: " + smsParser.getSms().getBody());
    }

    @Override
    public @NonNull List<Setting> getSettings() {
        return settings;
    }
}
