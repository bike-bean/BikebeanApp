package de.bikebean.app.ui.utils.date

import de.bikebean.app.db.state.State
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap
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

    fun getChargeDateString() : String {
        return if (remainingDaysFromNow > 2)
            " ($chargeDate)"
        else ""
    }

    fun getRemainingString() : String {
        if (remainingDaysFromNow > 2)
            return "${remainingDaysFromNow.toInt()} Tage"
        if (remainingDaysFromNow * 24 > 1)
            return "${(remainingDaysFromNow * 24).toInt()} Stunden"
        return if ((remainingDaysFromNow * 24).toInt() == 1)
            "1 Stunde"
        else ""
    }

    fun getReferenceString() : String {
        return "${referenceState.percent.toInt()} %"
    }

    fun getCurrentPercent() : Double {
        return max(getCurrentState().percent, 0.0)
    }

    private val chargeDate: String
        get() = SimpleDateFormat("dd.MM.yy", Locale.GERMANY).format(finalState.datetime)

    private val remainingDaysFromNow : Double
        get() = msToDays(finalState.datetime - getCurrentState().datetime)

    private val finalState : BatteryState
        get() = BatteryState(
                daysToMs(remainingDaysFromReference) + referenceState.datetime,
                10.0
        )

    private fun getCurrentState() : BatteryState {
        val msSinceReference = Date().time - referenceState.datetime

        return BatteryState(
                Date().time,
                referenceState.percent - msToDays(msSinceReference) * dischargeRate
        )
    }

    private val remainingDaysFromReference
        get() = (referenceState.percent - 10) / dischargeRate

    companion object {
        private val runtimeByInterval: Map<Int, Double> = object : HashMap<Int, Double>() {
            init {
                put(1, 175.0)
                put(2, 260.0)
                put(4, 346.0)
                put(8, 415.0)
                put(12, 444.0)
                put(24, 477.0)
            }
        }

        private fun getRatePerDays(days: Double?) : Double {
            return 100.0 / ( days ?: return Double.MAX_VALUE)
        }

        private fun daysToMs(days: Double): Long {
            return (days * 24 * 60).toLong() * 60 * 1000
        }

        private fun msToDays(ms: Long): Double {
            return (ms / 1000 / 60).toDouble() / 60 / 24
        }
    }

    class BatteryState(val datetime: Long, val percent: Double) {
        constructor(state: State) : this(state.timestamp, state.value)
    }
}