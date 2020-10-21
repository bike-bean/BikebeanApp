package de.bikebean.app.db.type.types.sms_parser_types;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.lang.ref.WeakReference;

import de.bikebean.app.db.settings.settings.Wapp;
import de.bikebean.app.db.settings.settings.number_settings.CellTowers;
import de.bikebean.app.db.sms.Sms;
import de.bikebean.app.db.state.State;
import de.bikebean.app.db.type.types.SmsParserType;
import de.bikebean.app.ui.drawer.log.LogViewModel;

public class CellTowersType extends SmsParserType {

    private static boolean matches() {
        return positionMatcher.find(0);
    }

    public static @Nullable CellTowersType createIfMatches(
            final @NonNull Sms sms,
            final @NonNull WeakReference<LogViewModel> lv) {
        positionMatcher = positionPattern.matcher(sms.getBody());
        statusBatteryStatusMatcher = statusBatteryStatusPattern.matcher(sms.getBody());

        if (matches())
            return new CellTowersType(sms, lv);

        return null;
    }

    private CellTowersType(final @NonNull Sms sms, final @NonNull WeakReference<LogViewModel> lv) {
        super(TYPE.CELL_TOWERS, sms, lv);

        /* no battery entry in this special case */
        getSettings().add(new CellTowers(sms, super::getWappCellTowers));
        getSettings().add(new Wapp(sms, State.WAPP_CELL_TOWERS));
    }
}
