package de.bikebean.app.db.type.types.sms_parser_types

import de.bikebean.app.db.settings.settings.add_to_list_settings.Battery
import de.bikebean.app.db.settings.settings.add_to_list_settings.Wapp
import de.bikebean.app.db.settings.settings.add_to_list_settings.number_settings.WifiAccessPoints
import de.bikebean.app.db.sms.Sms
import de.bikebean.app.db.state.State
import de.bikebean.app.db.type.types.ParserPatterns.batteryPattern
import de.bikebean.app.db.type.types.ParserPatterns.wifiPattern
import de.bikebean.app.db.type.types.ParserType
import de.bikebean.app.ui.drawer.log.LogViewModel
import java.lang.ref.WeakReference

class WifiList(sms: Sms, lv: WeakReference<LogViewModel>) : ParserType(TYPE.WIFI_LIST, sms, lv) {

    init {
        wifiMatcher = wifiPattern.matcher(sms.body)
        batteryMatcher = batteryPattern.matcher(sms.body)
    }

    override val matchers = listOf(
            wifiMatcher,
            batteryMatcher
    )

    override val settings get() = listOf(
            WifiAccessPoints(sms) { wappWifiAccessPoints },
            Wapp(sms, State.WAPP_WIFI_ACCESS_POINTS),
            /* battery value is encoded differently in this case */
            Battery(sms) { battery }
    )

}