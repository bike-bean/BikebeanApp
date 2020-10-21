package de.bikebean.app.db.state;

import androidx.annotation.NonNull;

import de.bikebean.app.db.sms.Sms;

public abstract class StateFactory {

    public static @NonNull State createStateWithEnum(
            long timestamp,
            final @NonNull State.KEY key,
            final @NonNull Double value,
            final @NonNull String longValue,
            final @NonNull State.STATUS state,
            int smsId
    ) {
        return new State(
                timestamp,
                key.get(),
                value,
                longValue,
                state.ordinal(),
                smsId
        );
    }

    public static @NonNull State createSimplePendingState(
            final @NonNull Sms sms,
            final @NonNull State.KEY key,
            final double value
    ) {
        return new State(
                sms.getTimestamp(),
                key.get(),
                value,
                "",
                State.STATUS.PENDING.ordinal(),
                sms.getId()
        );
    }

    public static @NonNull State createPendingState(
            final @NonNull State.KEY key,
            double value
    ) {
        return new State(
                System.currentTimeMillis(),
                key.get(),
                value,
                "",
                State.STATUS.PENDING.ordinal(),
                0
        );
    }

    public static @NonNull State createNumberState(
            final @NonNull Sms sms,
            final @NonNull State.KEY key,
            double value,
            final @NonNull State.STATUS status
    ) {
        return new State(
                sms.getTimestamp(),
                key.get(),
                value,
                "",
                status.ordinal(),
                sms.getId()
        );
    }

    public static @NonNull State createStringState(
            final @NonNull Sms sms,
            final @NonNull State.KEY key,
            final @NonNull String string,
            final @NonNull State.STATUS status
    ) {
        return new State(
                sms.getTimestamp(),
                key.get(),
                0.0,
                string,
                status.ordinal(),
                sms.getId()
        );
    }

    public static @NonNull State createNullState() {
        return new State(
            0,
            "",
            0.0,
            "",
            0,
            0
        );
    }
}
