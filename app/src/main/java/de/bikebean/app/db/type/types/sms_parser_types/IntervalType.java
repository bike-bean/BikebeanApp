package de.bikebean.app.db.type.types.sms_parser_types;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.lang.ref.WeakReference;

import de.bikebean.app.db.settings.settings.Battery;
import de.bikebean.app.db.settings.settings.replace_if_newer_settings.Interval;
import de.bikebean.app.db.settings.settings.replace_if_newer_settings.Status;
import de.bikebean.app.db.sms.Sms;
import de.bikebean.app.db.type.types.SmsParserType;
import de.bikebean.app.ui.drawer.log.LogViewModel;

public class IntervalType extends SmsParserType {

    private static boolean matches() {
        return intervalChangedMatcher.find(0) && statusBatteryStatusMatcher.find(0);
    }

    public static @Nullable IntervalType createIfMatches(
            final @NonNull Sms sms,
            final @NonNull WeakReference<LogViewModel> lv) {
        intervalChangedMatcher = intervalChangedPattern.matcher(sms.getBody());
        statusBatteryStatusMatcher = statusBatteryStatusPattern.matcher(sms.getBody());

        if (matches())
            return new IntervalType(sms, lv);

        return null;
    }

    private IntervalType(final @NonNull Sms sms, final @NonNull WeakReference<LogViewModel> lv) {
        super(TYPE.INT, sms, lv);

        getSettings().add(new Battery(sms, super::getStatusBattery));
        getSettings().add(new Interval(sms, super::getInterval));
        getSettings().add(new Status(sms));
    }
}
