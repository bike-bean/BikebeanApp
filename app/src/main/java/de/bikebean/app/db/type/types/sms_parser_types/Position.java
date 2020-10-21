package de.bikebean.app.db.type.types.sms_parser_types;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.lang.ref.WeakReference;

import de.bikebean.app.db.sms.Sms;
import de.bikebean.app.db.type.types.SmsParserType;
import de.bikebean.app.ui.drawer.log.LogViewModel;

public class Position extends SmsParserType {

    private static boolean matches() {
        return positionMatcher.find(0) && statusBatteryStatusMatcher.find(0);
    }

    public static @Nullable Position createIfMatches(
            final @NonNull Sms sms,
            final @NonNull WeakReference<LogViewModel> lv) {
        positionMatcher = positionPattern.matcher(sms.getBody());
        statusBatteryStatusMatcher = statusBatteryStatusPattern.matcher(sms.getBody());

        if (matches())
            return new Position(sms, lv);

        return null;
    }

    private Position(final @NonNull Sms sms, final @NonNull WeakReference<LogViewModel> lv) {
        super(TYPE.POSITION, sms, lv);
    }
}
