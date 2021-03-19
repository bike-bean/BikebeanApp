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
    protected val Matcher.battery: Double get() = result.toDoubleSafe()
    protected val Matcher.wifi : Boolean get() = result == "on"
    protected val Matcher.interval : Int get() = result.toInt()

    protected fun String.toDoubleSafe() = when {
        isEmpty() -> 0.0
        else -> toDouble()
    }

    protected fun Matcher.groupStrings(): String = StringBuilder().let {
        reset()
        while (find()) {
            it.append(group())
            it.append("\n")
        }
        it.toString()
    }

    protected val Matcher.result : String
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

    enum class TYPE {
        POSITION, STATUS, STATUS_NO_WARNING_NUMBER, WIFI_ON, WIFI_OFF,
        WARNING_NUMBER, CELL_TOWERS, WIFI_LIST, NO_WIFI_LIST, NO_WIFI_LIST_ALT,
        INT, LOW_BATTERY, VERY_LOW_BATTERY
    }

}