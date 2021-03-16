package de.bikebean.app.db.settings.settings.replace_if_newer_settings

import de.bikebean.app.db.settings.settings.ReplaceIfNewerSetting
import de.bikebean.app.db.sms.Sms
import de.bikebean.app.db.sms.SmsFactory
import de.bikebean.app.db.state.State
import de.bikebean.app.db.state.State.KEY
import de.bikebean.app.db.state.StateFactory

class Wifi : ReplaceIfNewerSetting {

    constructor(
            wifi: Boolean, sms: Sms
    ) : super(StateFactory.createNumberState(
            sms, key, when {
                wifi -> 1.0
                else -> 0.0
            }, status
    ))

    constructor(
            sms: Sms, wifiGetter: () -> Boolean
    ) : this(wifiGetter(), sms)

    constructor() : super(StateFactory.createNumberState(
            SmsFactory.createNullSms(), key, 0.0, status
    ))

    companion object {
        const val INITIAL_WIFI = false
        private val status = State.STATUS.CONFIRMED
        private val key = KEY.WIFI
    }
}