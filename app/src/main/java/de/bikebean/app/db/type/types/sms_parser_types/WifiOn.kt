package de.bikebean.app.db.type.types.sms_parser_types

import de.bikebean.app.db.settings.settings.add_to_list_settings.Battery
import de.bikebean.app.db.settings.settings.replace_if_newer_settings.Status
import de.bikebean.app.db.settings.settings.replace_if_newer_settings.Wifi
import de.bikebean.app.db.sms.Sms
import de.bikebean.app.db.type.types.ParserPatterns.statusBatteryStatusPattern
import de.bikebean.app.db.type.types.ParserPatterns.wifiStatusOnPattern
import de.bikebean.app.db.type.types.ParserType
import de.bikebean.app.ui.drawer.log.LogViewModel
import java.lang.ref.WeakReference

class WifiOn(sms: Sms, lv: WeakReference<LogViewModel>) : ParserType(TYPE.WIFI_ON, sms, lv) {

    override val matchers = listOf(
            wifiStatusOnPattern.matcher(sms.body),
            statusBatteryStatusPattern.matcher(sms.body)
    )

    override val settings get() = listOf(
            Battery(sms, matchers[1].battery),
            Wifi(sms, true),
            Status(sms)
    )
}