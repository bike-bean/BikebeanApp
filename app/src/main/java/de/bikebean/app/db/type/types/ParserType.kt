package de.bikebean.app.db.type.types

import de.bikebean.app.db.settings.Setting
import de.bikebean.app.db.sms.Sms
import de.bikebean.app.db.type.Type
import de.bikebean.app.ui.drawer.log.LogViewModel
import java.lang.ref.WeakReference
import java.util.regex.Matcher

abstract class ParserType(
        val type: TYPE,
        val sms: Sms,
        private val lv: WeakReference<LogViewModel>
) : Type() {

    abstract val matchers: List<Matcher>

    fun addToConversationList(conversationList: MutableList<Setting>) {
        /* update the conversationList, checking if the information from the SMS
           is newer than already stored information. */
        settings.forEach {
            it.conversationListAdder(conversationList, it)
        }
    }

    /* Get results of Sms Parser */
    val statusWifi: Boolean get() = statusWifiStatusMatcher.result == "on"

    val interval: Int get() = intervalChangedMatcher.result.toInt()
    val statusInterval: Int get() = statusIntervalMatcher.result.toInt()

    val warningNumber: String get() = warningNumberMatcher.result

    val statusWarningNumber: String get() = statusWarningNumberMatcher.result

    val wappCellTowers: String get() = positionMatcher.groupStrings()
    val wappWifiAccessPoints: String get() = wifiMatcher.groupStrings()

    val battery: Double
        get() {
            var result = ""
            batteryMatcher.reset()
            while (batteryMatcher.find()) {
                // use the last entry that matches battery specs
                result = batteryMatcher.group()
            }
            return result.toDoubleSafe()
        }

    val statusBattery: Double get() = statusBatteryStatusMatcher.result.toDoubleSafe()
    val batteryNoWifi: Double get() = noWifiMatcher.result.toDoubleSafe()
    val batteryNoWifiAlt: Double get() = noWifiMatcherAlt.result.toDoubleSafe()
    val lowBattery: Double get() = lowBatteryMatcher.result.toDoubleSafe()

    private fun String.toDoubleSafe() = when {
        isEmpty() -> 0.0
        else -> toDouble()
    }

    private fun Matcher.groupStrings(): String = StringBuilder().let {
        reset()
        while (find()) {
            it.append(group())
            it.append("\n")
        }
        it.toString()
    }

    private val Matcher.result : String
        get() {
            var count = 0
            var result = ""

            reset()
            while (find()) {
                count++
                result = group(2) ?: "".also {
                    lv.get()?.e(
                            "Failed to parse SMS. Matcher: $this, SMS: ${sms.body}"
                    )
                }
                if (count > 1)
                    lv.get()?.e(
                            "There should only be one instance per message!"
                    )
            }

            return result
        }

    /* MATCHERS */
    lateinit var positionMatcher: Matcher
    lateinit var statusWarningNumberMatcher: Matcher
    lateinit var statusNoWarningNumberMatcher: Matcher
    lateinit var statusBatteryStatusMatcher: Matcher
    lateinit var statusIntervalMatcher: Matcher
    lateinit var statusWifiStatusMatcher: Matcher
    lateinit var warningNumberMatcher: Matcher
    lateinit var wifiStatusOnMatcher: Matcher
    lateinit var wifiStatusOffMatcher: Matcher
    lateinit var wifiMatcher: Matcher
    lateinit var batteryMatcher: Matcher
    lateinit var noWifiMatcher: Matcher
    lateinit var noWifiMatcherAlt: Matcher
    lateinit var intervalChangedMatcher: Matcher
    lateinit var lowBatteryMatcher: Matcher

    enum class TYPE {
        POSITION, STATUS, STATUS_NO_WARNING_NUMBER, WIFI_ON, WIFI_OFF,
        WARNING_NUMBER, CELL_TOWERS, WIFI_LIST, NO_WIFI_LIST, NO_WIFI_LIST_ALT,
        INT, LOW_BATTERY
    }

}