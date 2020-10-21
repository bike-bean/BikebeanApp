package de.bikebean.app.ui.utils.sms.parser;

import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.lang.ref.WeakReference;
import java.util.Locale;

import de.bikebean.app.db.type.types.SmsParserType;
import de.bikebean.app.db.type.types.sms_parser_types.CellTowersType;
import de.bikebean.app.db.type.types.sms_parser_types.IntervalType;
import de.bikebean.app.db.type.types.sms_parser_types.LowBatteryType;
import de.bikebean.app.db.type.types.sms_parser_types.NoWifiList;
import de.bikebean.app.db.type.types.sms_parser_types.Position;
import de.bikebean.app.db.type.types.sms_parser_types.StatusType;
import de.bikebean.app.db.type.types.sms_parser_types.WarningNumberType;
import de.bikebean.app.db.type.types.sms_parser_types.WifiList;
import de.bikebean.app.db.type.types.sms_parser_types.WifiOff;
import de.bikebean.app.db.type.types.sms_parser_types.WifiOn;
import de.bikebean.app.db.sms.Sms;
import de.bikebean.app.ui.drawer.log.LogViewModel;
import de.bikebean.app.ui.drawer.status.StateViewModel;
import de.bikebean.app.ui.drawer.sms_history.SmsViewModel;

public class SmsParser extends AsyncTask<String, Void, Boolean> {

    private final @NonNull WeakReference<StateViewModel> statusViewModelReference;
    private final @NonNull WeakReference<SmsViewModel> smsViewModelReference;
    private final @NonNull WeakReference<LogViewModel> lv;

    private final @NonNull Sms sms;

    public SmsParser(final @NonNull Sms sms, final StateViewModel st,
                     final SmsViewModel sm, final LogViewModel lv) {
        this.sms = sms;
        statusViewModelReference = new WeakReference<>(st);
        smsViewModelReference = new WeakReference<>(sm);
        this.lv = new WeakReference<>(lv);
    }

    @Override
    protected @NonNull Boolean doInBackground(final @NonNull String... args) {
        /*
         Parse Sms to get which type it is
         */
        for (@NonNull SmsParserType type : getTypes()) {
            lv.get().w(String.format(
                    Locale.GERMANY,
                    "Detected Type %d (%s)",
                    type.getSmsType().ordinal(), type.getSmsType().name())
            );

            /*
             Add each status entry to the status viewModel ( -> database )
             */
            statusViewModelReference.get().insert(type);
        }

        return true;
    }

    @Override
    protected void onPostExecute(final @NonNull Boolean isDatabaseUpdated) {
        if (isDatabaseUpdated)
            smsViewModelReference.get().markParsed(sms);
    }

    public @NonNull SmsParserType[] getTypes() {
        final @NonNull SmsParserType[] types = appendToArray(
                Position.createIfMatches(sms, lv),
                StatusType.createIfMatches(sms, lv),
                WifiOn.createIfMatches(sms, lv),
                WifiOff.createIfMatches(sms, lv),
                WarningNumberType.createIfMatches(sms, lv),
                CellTowersType.createIfMatches(sms, lv),
                WifiList.createIfMatches(sms, lv),
                NoWifiList.createIfMatches(sms, lv),
                IntervalType.createIfMatches(sms, lv),
                LowBatteryType.createIfMatches(sms, lv)
        );

        if (types.length == 0)
            lv.get().w("Could not Parse SMS: " + sms.getBody());

        return types;
    }

    private static @NonNull SmsParserType[] appendToArray(final @NonNull SmsParserType... types) {
        SmsParserType[] smsParserTypes = new SmsParserType[getRealLength(types)];

        int i = 0;
        for (@Nullable SmsParserType type : types)
            if (type != null)
                smsParserTypes[i++] = type;

        return smsParserTypes;
    }

    private static int getRealLength(final @NonNull SmsParserType... types) {
        int i = 0;
        for (@Nullable SmsParserType type : types)
            if (type != null)
                i++;

        return i;
    }
}

