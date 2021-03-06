package de.bikebean.app.db.type.types.sms_parser_types

import de.bikebean.app.db.settings.settings.add_to_list_settings.LowBattery
import de.bikebean.app.db.sms.Sms
import de.bikebean.app.db.type.types.ParserPatterns.lowBatteryPattern
import de.bikebean.app.db.type.types.ParserType
import de.bikebean.app.ui.drawer.log.LogViewModel
import java.lang.ref.WeakReference

class LowBatteryType(
        sms: Sms, lv: WeakReference<LogViewModel>
) : ParserType(TYPE.LOW_BATTERY, sms, lv) {

    override val matchers = listOf(
            lowBatteryPattern.matcher(sms.body)
    )

    override val settings get() = listOf(
            LowBattery(sms, matchers[0].battery)
    )
}