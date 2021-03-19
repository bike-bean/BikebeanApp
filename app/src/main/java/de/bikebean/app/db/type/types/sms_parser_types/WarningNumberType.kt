package de.bikebean.app.db.type.types.sms_parser_types

import de.bikebean.app.db.settings.settings.add_to_list_settings.Battery
import de.bikebean.app.db.settings.settings.replace_if_newer_settings.Status
import de.bikebean.app.db.settings.settings.replace_if_newer_settings.WarningNumber
import de.bikebean.app.db.sms.Sms
import de.bikebean.app.db.type.types.ParserPatterns.statusBatteryStatusPattern
import de.bikebean.app.db.type.types.ParserPatterns.warningNumberPattern
import de.bikebean.app.db.type.types.ParserType
import de.bikebean.app.ui.drawer.log.LogViewModel
import java.lang.ref.WeakReference

class WarningNumberType(
        sms: Sms, lv: WeakReference<LogViewModel>
) : ParserType(TYPE.WARNING_NUMBER, sms, lv) {

    private val statusBatteryStatusMatcher = statusBatteryStatusPattern.matcher(sms.body)
    private val warningNumberMatcher = warningNumberPattern.matcher(sms.body)

    override val matchers = listOf(
            warningNumberMatcher,
            statusBatteryStatusMatcher
    )

    override val settings get() = listOf(
            Battery(sms, statusBatteryStatusMatcher.battery),
            WarningNumber(sms, warningNumberMatcher.result),
            Status(sms)
    )
}