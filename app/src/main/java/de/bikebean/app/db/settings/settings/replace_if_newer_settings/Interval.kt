package de.bikebean.app.db.settings.settings.replace_if_newer_settings

import de.bikebean.app.db.settings.settings.ReplaceIfNewerSetting
import de.bikebean.app.db.sms.Sms
import de.bikebean.app.db.sms.SmsFactory
import de.bikebean.app.db.state.State
import de.bikebean.app.db.state.State.KEY
import de.bikebean.app.db.state.StateFactory

class Interval private constructor(
        sms: Sms,
        interval: Int,
        status: State.STATUS
) : ReplaceIfNewerSetting(
        StateFactory.createNumberState(
                sms, key, interval.toDouble(), status
        )
) {

    constructor(sms: Sms, interval: Int) : this(sms, interval, State.STATUS.CONFIRMED)

    constructor() : this(SmsFactory.createNullSms(), INITIAL_INTERVAL, State.STATUS.UNSET)

    companion object {
        const val INITIAL_INTERVAL = 1
        private val key = KEY.INTERVAL
    }

}