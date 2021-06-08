package de.bikebean.app.db.type.types.sms_parser_types

import de.bikebean.app.db.settings.settings.add_to_list_settings.Battery
import de.bikebean.app.db.settings.settings.replace_if_newer_settings.Interval
import de.bikebean.app.db.settings.settings.replace_if_newer_settings.Status
import de.bikebean.app.db.settings.settings.replace_if_newer_settings.WarningNumber
import de.bikebean.app.db.settings.settings.replace_if_newer_settings.Wifi
import de.bikebean.app.db.sms.Sms
import de.bikebean.app.db.type.types.ParserPatterns.statusBatteryStatusPatternShort
import de.bikebean.app.db.type.types.ParserPatterns.statusIntervalPattern
import de.bikebean.app.db.type.types.ParserPatterns.statusNoWarningNumberPattern
import de.bikebean.app.db.type.types.ParserPatterns.statusWifiStatusPattern
import de.bikebean.app.db.type.types.ParserType
import de.bikebean.app.ui.drawer.log.LogViewModel
import java.lang.ref.WeakReference

// This class is a (temporary?) fix to cope with https://github.com/bike-bean/Bike-Bean/pull/6

class StatusTypeNoWarningNumberWifiOn(
        sms: Sms, lv: WeakReference<LogViewModel>
) : ParserType(TYPE.STATUS_NO_WARNING_NUMBER_WIFI_ON, sms, lv) {

    private val statusNoWarningNumberMatcher = statusNoWarningNumberPattern.matcher(sms.body)
    private val statusIntervalMatcher = statusIntervalPattern.matcher(sms.body)
    private val statusWifiStatusMatcher = statusWifiStatusPattern.matcher(sms.body)
    private val statusBatteryStatusMatcherShort = statusBatteryStatusPatternShort.matcher(sms.body)

    override val matchers = listOf(
            statusNoWarningNumberMatcher,
            statusBatteryStatusMatcherShort,
            statusIntervalMatcher,
            statusWifiStatusMatcher
    )

    override val settings get() = listOf(
            WarningNumber(sms),
            Interval(sms, statusIntervalMatcher.interval),
            Wifi(sms, statusWifiStatusMatcher.wifi),
            Battery(sms, statusBatteryStatusMatcherShort.batterySimple),
            Status(sms)
    )
}