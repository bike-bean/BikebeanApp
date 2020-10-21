package de.bikebean.app.db.type.types.sms_parser_types;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.lang.ref.WeakReference;

import de.bikebean.app.db.settings.settings.Battery;
import de.bikebean.app.db.settings.settings.replace_if_newer_settings.Interval;
import de.bikebean.app.db.settings.settings.replace_if_newer_settings.Status;
import de.bikebean.app.db.settings.settings.replace_if_newer_settings.WarningNumber;
import de.bikebean.app.db.settings.settings.replace_if_newer_settings.Wifi;
import de.bikebean.app.db.sms.Sms;
import de.bikebean.app.db.type.types.SmsParserType;
import de.bikebean.app.ui.drawer.log.LogViewModel;

public class StatusType extends SmsParserType {

    private static boolean matchesAll() {
        return statusWarningNumberMatcher.find(0) && statusBatteryStatusMatcher.find(0) &&
                statusIntervalMatcher.find(0) && statusWifiStatusMatcher.find(0);
    }

    private static boolean matches() {
        return statusBatteryStatusMatcher.find(0) && statusIntervalMatcher.find(0)
                && statusWifiStatusMatcher.find(0);
    }

    public static @Nullable StatusType createIfMatches(
            final @NonNull Sms sms,
            final @NonNull WeakReference<LogViewModel> lv) {
        statusWarningNumberMatcher = statusWarningNumberPattern.matcher(sms.getBody());
        statusIntervalMatcher = statusIntervalPattern.matcher(sms.getBody());
        statusWifiStatusMatcher = statusWifiStatusPattern.matcher(sms.getBody());
        statusBatteryStatusMatcher = statusBatteryStatusPattern.matcher(sms.getBody());

        if (matchesAll())
            return new StatusType(sms, lv);

        statusBatteryStatusMatcher.reset();
        statusIntervalMatcher.reset();
        statusWifiStatusMatcher.reset();
        if (matches()) {
            if (lv.get() != null)
                lv.get().w("WarningNumber is not set!");

            return new StatusType(sms, lv);
        }

        return null;
    }

    private StatusType(final @NonNull Sms sms, final @NonNull WeakReference<LogViewModel> lv) {
        super(TYPE.STATUS, sms, lv);

        getSettings().add(new WarningNumber(sms, super::getStatusWarningNumber));
        getSettings().add(new Interval(sms, super::getStatusInterval));
        getSettings().add(new Wifi(sms, super::getStatusWifi));
        getSettings().add(new Battery(sms, super::getStatusBattery));
        getSettings().add(new Status(sms));
    }
}
