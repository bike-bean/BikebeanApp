package de.bikebean.app.db.state

import de.bikebean.app.db.settings.settings.add_to_list_settings.WappState
import de.bikebean.app.db.sms.Sms
import de.bikebean.app.db.sms.SmsFactory
import de.bikebean.app.db.state.State.KEY

object StateFactory {

    fun createStateFromWappState(
            wappState: WappState,
            key: KEY,
            value: Double
    ): State {
        return State(
                wappState.sms.timestamp + 1,
                key.get(),
                value,
                "",
                State.STATUS.CONFIRMED.ordinal,
                wappState.sms.id
        )
    }

    fun createSimplePendingState(
            sms: Sms,
            key: KEY,
            value: Double
    ): State = State(
            sms.timestamp,
            key.get(),
            value,
            "",
            State.STATUS.PENDING.ordinal,
            sms.id
    )

    @JvmStatic
    fun createPendingState(
            key: KEY,
            value: Double
    ): State = State(
            System.currentTimeMillis(),
            key.get(),
            value,
            "",
            State.STATUS.PENDING.ordinal,
            0
    )

    fun createNumberState(
            sms: Sms,
            key: KEY,
            value: Double,
            status: State.STATUS
    ): State = State(
            sms.timestamp,
            key.get(),
            value,
            "",
            status.ordinal,
            sms.id
    )

    fun createUnsetState(
            key: KEY,
            value: Double
    ): State = SmsFactory.createNullSms().let {
        State(
                it.timestamp,
                key.get(),
                value,
                "",
                State.STATUS.UNSET.ordinal,
                it.id
        )
    }

    fun createStringState(
            sms: Sms,
            key: KEY,
            string: String,
            status: State.STATUS
    ): State = State(
            sms.timestamp,
            key.get(),
            0.0,
            string,
            status.ordinal,
            sms.id
    )

    @JvmStatic
    fun createNullState(): State = State(
            0,
            "",
            0.0,
            "",
            0,
            0
    )
}