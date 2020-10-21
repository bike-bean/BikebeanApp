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

public class WifiOff extends SmsParserType {

    private static boolean matches() {
        return wifiStatusOffMatcher.find(0) && statusBatteryStatusMatcher.find(0);
    }

    public static @Nullable WifiOff createIfMatches(
            final @NonNull Sms sms,
            final @NonNull WeakReference<LogViewModel> lv) {
        wifiStatusOffMatcher = wifiStatusOffPattern.matcher(sms.getBody());
        statusBatteryStatusMatcher = statusBatteryStatusPattern.matcher(sms.getBody());

        if (matches())
            return new WifiOff(sms, lv);

        return null;
    }

    private WifiOff(final @NonNull Sms sms, final @NonNull WeakReference<LogViewModel> lv) {
        super(TYPE.WIFI_OFF, sms, lv);

        getSettings().add(new Battery(sms, super::getStatusBattery));
        getSettings().add(new Wifi(false, sms));
        getSettings().add(new Status(sms));
    }
}
