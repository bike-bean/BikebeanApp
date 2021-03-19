package de.bikebean.app.db.type.types.sms_parser_types

import de.bikebean.app.db.settings.settings.add_to_list_settings.Battery
import de.bikebean.app.db.settings.settings.add_to_list_settings.Wapp
import de.bikebean.app.db.settings.settings.add_to_list_settings.number_settings.WifiAccessPoints
import de.bikebean.app.db.sms.Sms
import de.bikebean.app.db.state.State
import de.bikebean.app.db.type.types.ParserPatterns.noWifiPatternAlt
import de.bikebean.app.db.type.types.ParserType
import de.bikebean.app.ui.drawer.log.LogViewModel
import java.lang.ref.WeakReference

class NoWifiListAlt(
        sms: Sms, lv: WeakReference<LogViewModel>
) : ParserType(TYPE.NO_WIFI_LIST_ALT, sms, lv) {

    override val matchers = listOf(
            noWifiPatternAlt.matcher(sms.body)
    )

    override val settings get() = listOf(
            WifiAccessPoints(sms, ""),
            Wapp(sms, State.WAPP_WIFI_ACCESS_POINTS),
            Battery(sms, matchers[0].battery)
    )

}