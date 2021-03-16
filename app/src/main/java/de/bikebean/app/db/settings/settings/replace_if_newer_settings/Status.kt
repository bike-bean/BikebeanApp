package de.bikebean.app.db.settings.settings.replace_if_newer_settings

import de.bikebean.app.db.settings.settings.ReplaceIfNewerSetting
import de.bikebean.app.db.sms.Sms
import de.bikebean.app.db.state.State
import de.bikebean.app.db.state.State.KEY
import de.bikebean.app.db.state.StateFactory

class Status : ReplaceIfNewerSetting {

    constructor(sms: Sms) : super(
            StateFactory.createNumberState(sms, key, 0.0, State.STATUS.CONFIRMED)
    )

    constructor() : super(StateFactory.createUnsetState(key, 0.0))

    companion object {
        private val key = KEY._STATUS
    }
}