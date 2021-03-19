package de.bikebean.app.db.type.types.sms_parser_types

import de.bikebean.app.db.settings.settings.replace_if_newer_settings.Interval
import de.bikebean.app.db.sms.Sms
import de.bikebean.app.db.type.types.ParserPatterns.lowBatteryPattern
import de.bikebean.app.db.type.types.ParserPatterns.veryLowBatteryPattern
import de.bikebean.app.db.type.types.ParserType
import de.bikebean.app.ui.drawer.log.LogViewModel
import java.lang.ref.WeakReference

class VeryLowBatteryType(
        sms: Sms, lv: WeakReference<LogViewModel>
) : ParserType(TYPE.VERY_LOW_BATTERY, sms, lv) {

    override val matchers = listOf(
            lowBatteryPattern.matcher(sms.body),
            veryLowBatteryPattern.matcher(sms.body)
    )

    override val settings get() = listOf(
            Interval(sms, 24)
    )
}