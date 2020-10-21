package de.bikebean.app.db.type.types.sms_parser_types;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.lang.ref.WeakReference;

import de.bikebean.app.db.settings.settings.Battery;
import de.bikebean.app.db.settings.settings.Wapp;
import de.bikebean.app.db.settings.settings.number_settings.WifiAccessPoints;
import de.bikebean.app.db.sms.Sms;
import de.bikebean.app.db.state.State;
import de.bikebean.app.db.type.types.SmsParserType;
import de.bikebean.app.ui.drawer.log.LogViewModel;

public class NoWifiList extends SmsParserType {

    private static boolean matches() {
        return noWifiMatcher.find(0);
    }

    public static @Nullable NoWifiList createIfMatches(
            final @NonNull Sms sms,
            final @NonNull WeakReference<LogViewModel> lv) {
        noWifiMatcher = noWifiPattern.matcher(sms.getBody());

        if (matches())
            return new NoWifiList(sms, lv);

        return null;
    }

    private NoWifiList(final @NonNull Sms sms, final @NonNull WeakReference<LogViewModel> lv) {
        super(TYPE.NO_WIFI_LIST, sms, lv);

        /* battery value is encoded differently in this case */
        getSettings().add(new WifiAccessPoints(sms, super::getWappWifiAccessPoints));
        getSettings().add(new Wapp(sms, State.WAPP_WIFI_ACCESS_POINTS));
        getSettings().add(new Battery(sms, super::getBatteryNoWifi));
    }
}
