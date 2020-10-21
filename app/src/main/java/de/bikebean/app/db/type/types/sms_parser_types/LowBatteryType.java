package de.bikebean.app.db.type.types.sms_parser_types;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.lang.ref.WeakReference;

import de.bikebean.app.db.settings.settings.LowBattery;
import de.bikebean.app.db.sms.Sms;
import de.bikebean.app.db.type.types.SmsParserType;
import de.bikebean.app.ui.drawer.log.LogViewModel;

public class LowBatteryType extends SmsParserType {

    private static boolean matches() {
        return lowBatteryMatcher.find(0);
    }

    public static @Nullable LowBatteryType createIfMatches(
            final @NonNull Sms sms,
            final @NonNull WeakReference<LogViewModel> lv) {
        lowBatteryMatcher = lowBatteryPattern.matcher(sms.getBody());

        if (matches())
            return new LowBatteryType(sms, lv);

        return null;
    }

    private LowBatteryType(final @NonNull Sms sms, final @NonNull WeakReference<LogViewModel> lv) {
        super(TYPE.LOW_BATTERY, sms, lv);

        getSettings().add(new LowBattery(sms, super::getLowBattery));
    }
}
