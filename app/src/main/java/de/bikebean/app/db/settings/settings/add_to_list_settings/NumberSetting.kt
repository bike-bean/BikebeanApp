package de.bikebean.app.db.settings.settings.add_to_list_settings

import de.bikebean.app.db.settings.settings.AddToListSetting
import de.bikebean.app.db.sms.Sms
import de.bikebean.app.db.sms.SmsFactory
import de.bikebean.app.db.state.State
import de.bikebean.app.db.state.State.KEY
import de.bikebean.app.db.state.StateFactory

abstract class NumberSetting : AddToListSetting {

    val numberState: State

    protected constructor(
            wappString: String, sms: Sms, key: KEY,
            _numberState: State
    ) : super(
            StateFactory.createStringState(sms, key, wappString, State.STATUS.CONFIRMED)
    ) {
        numberState = _numberState
    }

    constructor(wappString: String, key: KEY,
                _numberState: State
    ) : super(
            StateFactory.createStringState(
                    SmsFactory.createNullSms(), key,
                    wappString, State.STATUS.UNSET)
    ) {
        numberState = _numberState
    }

    val number: Int
        get() = numberState.value.toInt()

    abstract class RawNumberSettings
    abstract val list: List<RawNumberSettings?>

    companion object {
        fun getNumberFromString(string: String): Int {
            return if (string.isEmpty()) 0 else string.split("\n").size
        }
    }
}