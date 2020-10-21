package de.bikebean.app.db.sms;

import android.database.Cursor;
import android.provider.Telephony;

import androidx.annotation.NonNull;

import de.bikebean.app.db.state.State;
import de.bikebean.app.ui.utils.date.DateUtils;
import de.bikebean.app.ui.utils.sms.send.SmsSender;

public class SmsFactory {

    public static @NonNull Sms createNewSentSms(
            int smsId,
            final @NonNull SmsSender smsSender
    ) {
        long timestamp = System.currentTimeMillis();

        return new Sms(
            smsId -1,
            smsSender.getAddress(),
            smsSender.getMessage().getMsg(),
            Telephony.Sms.MESSAGE_TYPE_SENT,
            Sms.STATUS.NEW.ordinal(),
            DateUtils.convertToTime(timestamp),
            timestamp
        );
    }

    public static @NonNull Sms createSmsFromState(
            final @NonNull State state
    ) {
        return new Sms(
                state.getSmsId(),
                "",
                "",
                0,
                0,
                DateUtils.convertToTime(state.getTimestamp()),
                state.getTimestamp()
        );
    }

    public static @NonNull Sms createSmsFromCursor(
            final @NonNull Cursor inbox,
            final @NonNull Sms.STATUS smsState
    ) {
        final @NonNull String id = inbox.getString(
                inbox.getColumnIndexOrThrow("_id")
        );
        final @NonNull String type = inbox.getString(
                inbox.getColumnIndexOrThrow("type")
        );
        final @NonNull String date = inbox.getString(
                inbox.getColumnIndexOrThrow("date")
        );
        final long timeStamp = Long.parseLong(date);

        return new Sms(
                Integer.parseInt(id),
                inbox.getString(inbox.getColumnIndexOrThrow("address")),
                inbox.getString(inbox.getColumnIndexOrThrow("body")),
                Integer.parseInt(type),
                smsState.ordinal(),
                DateUtils.convertToTime(timeStamp),
                Long.parseLong(date)
        );
    }

    public static @NonNull Sms createNullSms() {
        return new Sms(
                0,
                "",
                "",
                0,
                0,
                "",
                0
        );
    }
}
