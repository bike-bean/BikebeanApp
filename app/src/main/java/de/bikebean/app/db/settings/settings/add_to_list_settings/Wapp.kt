package de.bikebean.app.db.settings.settings.add_to_list_settings

import de.bikebean.app.db.settings.settings.AddToListSetting
import de.bikebean.app.db.sms.Sms
import de.bikebean.app.db.state.State
import de.bikebean.app.db.state.StateFactory

open class Wapp(
        sms: Sms, value: Double
) : AddToListSetting(
        StateFactory.createSimplePendingState(sms, State.KEY.WAPP, value)
)