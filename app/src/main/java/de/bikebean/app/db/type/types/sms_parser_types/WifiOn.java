package de.bikebean.app.db.type.types.sms_parser_types;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.lang.ref.WeakReference;

import de.bikebean.app.db.settings.settings.Battery;
import de.bikebean.app.db.settings.settings.replace_if_newer_settings.Status;
import de.bikebean.app.db.settings.settings.replace_if_newer_settings.Wifi;
import de.bikebean.app.db.sms.Sms;
import de.bikebean.app.db.type.types.SmsParserType;
import de.bikebean.app.ui.drawer.log.LogViewModel;

public class WifiOn extends SmsParserType {

    private static boolean matches() {
        return wifiStatusOnMatcher.find(0) && statusBatteryStatusMatcher.find(0);
    }

    public static @Nullable WifiOn createIfMatches(
            final @NonNull Sms sms,
            final @NonNull WeakReference<LogViewModel> lv) {
        wifiStatusOnMatcher = wifiStatusOnPattern.matcher(sms.getBody());
        statusBatteryStatusMatcher = statusBatteryStatusPattern.matcher(sms.getBody());

        if (matches())
            return new WifiOn(sms, lv);

        return null;
    }

    private WifiOn(final @NonNull Sms sms, final @NonNull WeakReference<LogViewModel> lv) {
        super(TYPE.WIFI_ON, sms, lv);

        getSettings().add(new Battery(sms, super::getStatusBattery));
        getSettings().add(new Wifi(true, sms));
        getSettings().add(new Status(sms));
    }
}
