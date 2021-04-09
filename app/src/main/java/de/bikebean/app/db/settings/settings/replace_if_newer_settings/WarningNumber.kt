package de.bikebean.app.db.settings.settings.replace_if_newer_settings

import de.bikebean.app.db.settings.settings.ReplaceIfNewerSetting
import de.bikebean.app.db.sms.Sms
import de.bikebean.app.db.sms.SmsFactory
import de.bikebean.app.db.state.State
import de.bikebean.app.db.state.State.KEY
import de.bikebean.app.db.state.StateFactory

class WarningNumber private constructor(
        sms: Sms,
        warningNumber: String,
        status: State.STATUS
) : ReplaceIfNewerSetting(
        StateFactory.createStringState(sms, key, warningNumber, status)
) {

    constructor() : this(SmsFactory.createNullSms())

    constructor(sms: Sms) : this(sms, "", State.STATUS.UNSET)

    constructor(sms: Sms, warningNumber: String) : this(sms, warningNumber, State.STATUS.CONFIRMED)

    companion object {
        private val key = KEY.WARNING_NUMBER
    }

}