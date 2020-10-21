package de.bikebean.app.db.type.types.sms_parser_types;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.lang.ref.WeakReference;

import de.bikebean.app.db.settings.settings.Battery;
import de.bikebean.app.db.settings.settings.replace_if_newer_settings.Status;
import de.bikebean.app.db.settings.settings.replace_if_newer_settings.WarningNumber;
import de.bikebean.app.db.sms.Sms;
import de.bikebean.app.db.type.types.SmsParserType;
import de.bikebean.app.ui.drawer.log.LogViewModel;

public class WarningNumberType extends SmsParserType {

    private static boolean matches() {
        return warningNumberMatcher.find(0) && statusBatteryStatusMatcher.find(0);
    }

    public static @Nullable WarningNumberType createIfMatches(
            final @NonNull Sms sms,
            final @NonNull WeakReference<LogViewModel> lv) {
        warningNumberMatcher = warningNumberPattern.matcher(sms.getBody());
        statusBatteryStatusMatcher = statusBatteryStatusPattern.matcher(sms.getBody());

        if (matches())
            return new WarningNumberType(sms, lv);

        return null;
    }

    private WarningNumberType(final @NonNull Sms sms,
                              final @NonNull WeakReference<LogViewModel> lv) {
        super(TYPE.WARNING_NUMBER, sms, lv);

        getSettings().add(new Battery(sms, super::getStatusBattery));
        getSettings().add(new WarningNumber(sms, super::getWarningNumber));
        getSettings().add(new Status(sms));
    }
}
