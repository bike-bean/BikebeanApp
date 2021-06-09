package de.bikebean.app.db.type.types

import de.bikebean.app.db.sms.Sms
import de.bikebean.app.db.type.types.sms_parser_types.*
import de.bikebean.app.ui.drawer.log.LogViewModel
import java.lang.ref.WeakReference

object ParserTypeFactory {

    fun createList(sms: Sms, lv: WeakReference<LogViewModel>): List<ParserType> =
            typesToCheck(sms, lv).filter(::checkAllPatternsMatch)

    private fun typesToCheck(sms: Sms, lv: WeakReference<LogViewModel>) = listOf(
            Position(sms, lv),
            StatusTypeWifiOff(sms, lv),
            StatusTypeWifiOn(sms, lv),
            StatusTypeNoWarningNumberWifiOff(sms, lv),
            StatusTypeNoWarningNumberWifiOn(sms, lv),
            WifiOn(sms, lv),
            WifiOff(sms, lv),
            WarningNumberType(sms, lv),
            CellTowersType(sms, lv),
            WifiList(sms, lv),
            NoWifiList(sms, lv),
            NoWifiListAlt(sms, lv),
            IntervalType(sms, lv),
            LowBatteryType(sms, lv),
            VeryLowBatteryType(sms, lv)
    )

    private fun checkAllPatternsMatch(parserType: ParserType): Boolean =
            parserType.matchers.all { it.find(0) }

}