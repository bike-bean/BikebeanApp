package de.bikebean.app.db.settings.settings.add_to_list_settings

import de.bikebean.app.db.settings.settings.AddToListSetting
import de.bikebean.app.db.sms.Sms
import de.bikebean.app.db.state.State
import de.bikebean.app.db.state.State.KEY
import de.bikebean.app.db.state.StateFactory

abstract class LocationSetting : AddToListSetting {

    protected constructor(
            location: Double, wappState: WappState, key: KEY
    ) : super(StateFactory.createStateFromWappState(wappState, key, location))

    protected constructor(
            sms: Sms, key: KEY
    ) : super(StateFactory.createNumberState(sms, key, 0.0, State.STATUS.PENDING))

    protected constructor(
            key: KEY
    ) : super(StateFactory.createUnsetState(key, 0.0))

}