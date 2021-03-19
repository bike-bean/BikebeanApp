package de.bikebean.app.db.type.types.sms_parser_types

import de.bikebean.app.db.settings.settings.add_to_list_settings.Battery
import de.bikebean.app.db.settings.settings.replace_if_newer_settings.Interval
import de.bikebean.app.db.settings.settings.replace_if_newer_settings.Status
import de.bikebean.app.db.sms.Sms
import de.bikebean.app.db.type.types.ParserPatterns.intervalChangedPattern
import de.bikebean.app.db.type.types.ParserPatterns.statusBatteryStatusPattern
import de.bikebean.app.db.type.types.ParserType
import de.bikebean.app.ui.drawer.log.LogViewModel
import java.lang.ref.WeakReference

class IntervalType(sms: Sms, lv: WeakReference<LogViewModel>) : ParserType(TYPE.INT, sms, lv) {

    private val intervalChangedMatcher = intervalChangedPattern.matcher(sms.body)
    private val statusBatteryStatusMatcher = statusBatteryStatusPattern.matcher(sms.body)

    override val matchers = listOf(
            intervalChangedMatcher,
            statusBatteryStatusMatcher
    )

    override val settings get() = listOf(
            Battery(sms, statusBatteryStatusMatcher.battery),
            Interval(sms, intervalChangedMatcher.interval),
            Status(sms)
    )
}