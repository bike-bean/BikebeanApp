package de.bikebean.app.db.settings.settings.add_to_list_settings

import de.bikebean.app.db.settings.settings.AddToListSetting
import de.bikebean.app.db.sms.Sms
import de.bikebean.app.db.state.State
import de.bikebean.app.db.state.StateFactory

class LowBattery(
        sms: Sms,
        lowBattery: Double
) : AddToListSetting(
        StateFactory.createNumberState(
                sms, State.KEY.BATTERY, lowBattery,
                State.STATUS.CONFIRMED
        )
)