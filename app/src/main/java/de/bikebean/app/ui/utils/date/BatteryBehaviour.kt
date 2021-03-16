package de.bikebean.app.ui.utils.date

import de.bikebean.app.db.state.State
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.max

class BatteryBehaviour(
        private val dischargeRate: Double,
        val referenceState: BatteryState) {

    constructor(isWifiOn: Boolean, interval: Int, state: State) : this (
            isWifiOn, interval, BatteryState(state)
    )

    constructor(isWifiOn: Boolean, interval: Int, referenceState: BatteryState) : this (
            getRatePerDays(when {
                isWifiOn -> 1.7
                runtimeByInterval.containsKey(interval) -> runtimeByInterval[interval]
                else -> null
            }),
            referenceState
    )

    fun getChargeDateString() : String = when {
        remainingDaysFromNow > 2 -> " ($chargeDate)"
        else -> ""
    }

    fun getRemainingString() : String = when {
        remainingDaysFromNow > 2 -> "${remainingDaysFromNow.toInt()} Tage"
        remainingDaysFromNow * 24 > 1 -> "${(remainingDaysFromNow * 24).toInt()} Stunden"
        (remainingDaysFromNow * 24).toInt() == 1 -> "1 Stunde"
        else -> ""
    }

    fun getReferenceString() : String = "${referenceState.percent.toInt()} %"

    fun getCurrentPercent() : Double = max(getCurrentState().percent, 0.0)

    private val chargeDate: String
        get() = SimpleDateFormat("dd.MM.yy", Locale.GERMANY).format(finalState.datetime)

    private val remainingDaysFromNow : Double
        get() = msToDays(finalState.datetime - getCurrentState().datetime)

    private val finalState : BatteryState
        get() = BatteryState(
                daysToMs(remainingDaysFromReference) + referenceState.datetime,
                10.0
        )

    private fun getCurrentState() : BatteryState = with (
            Date().time - referenceState.datetime) {
        BatteryState(
                Date().time,
                referenceState.percent - msToDays(this) * dischargeRate
        )
    }

    private val remainingDaysFromReference
        get() = (referenceState.percent - 10) / dischargeRate

    companion object {
        private val runtimeByInterval = mapOf(
                1 to 175.0,
                2 to 260.0,
                4 to 346.0,
                8 to 415.0,
                12 to 444.0,
                24 to 477.0,
        )

        private fun getRatePerDays(days: Double?) : Double {
            return 100.0 / ( days ?: return Double.MAX_VALUE)
        }

        private fun daysToMs(days: Double): Long = (days * 24 * 60).toLong() * 60 * 1000

        private fun msToDays(ms: Long): Double = (ms / 1000 / 60).toDouble() / 60 / 24
    }

    class BatteryState(val datetime: Long, val percent: Double) {
        constructor(state: State) : this(state.timestamp, state.value)
    }
}