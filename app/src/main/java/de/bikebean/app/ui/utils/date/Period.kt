package de.bikebean.app.ui.utils.date

import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.roundToInt

class Period(datetime: Long) {

    private val s: Long
    private val m: Long
    private val h: Int
    private val d: Int
    private val y: Int

    private val outputTime: String
    private val outputDate: String
    private val outputDateWithYear: String
    private val outputAll: String

    init {
        val now = Date()
        val then = Date(datetime)
        val ms = now.time - then.time
        s = ms / 1000
        m = s / 60
        h = m.toInt() / 60
        d = h / 24
        val yy = d / 365.0
        y = yy.toFloat().roundToInt()

        outputTime = formatDate("HH:mm", then)
        outputDate = formatDate("dd.MM", then)
        outputDateWithYear = formatDate("dd.MM.yy", then)
        outputAll = formatDate("dd.MM.yy", then)
    }

    private val periodMargin: Int = when {
        y > 1 -> 1
        d > 300 -> 2
        d > 1 -> 3
        d > 0 -> 4
        h > 1 -> 5
        h > 0 -> 6
        m > 1 -> 7
        m > 0 -> 8
        s > 1 -> 9
        s > 0 -> 10
        else -> 0
    }

    fun convertPeriodToHuman(): String = dateString + periodString

    val lastChangedString: String
        get() = when {
            periodMargin > 4 -> " um "
            else -> " am "
        }.let {
            "Zuletzt aktualisiert$it$dateString$periodString"
        }

    private val dateString: String
        get() = when {
            periodMargin in 1..2 -> outputDateWithYear
            periodMargin in 3..4 -> outputDate
            periodMargin >= 5 -> outputTime
            else -> outputAll
        }

    private val marginMap = mapOf(
            1 to " (Vor %d Jahren)",
            2 to " (Vor 1 Jahr)",
            3 to " (Vor %d Tagen)",
            4 to " (Vor 1 Tag)",
            5 to " (Vor %d Stunden)",
            6 to " (Vor 1 Stunde)",
            7 to " (Vor %d Minuten)",
            8 to " (Vor 1 Minute)",
            9 to " (Vor %d Sekunden)",
            10 to " (Vor 1 Sekunde)"
    )

    private val periodString: String
        get() = marginMap[periodMargin]?.let(::formatPeriodString) ?: ""

    private fun formatPeriodString(periodString: String): String = when (periodMargin) {
        1 -> formatPeriodString(periodString, y)
        3 -> formatPeriodString(periodString, d)
        5 -> formatPeriodString(periodString, h)
        7 -> formatPeriodString(periodString, m.toInt())
        9 -> formatPeriodString(periodString, s.toInt())
        else -> periodString
    }

    private fun formatPeriodString(periodString: String, period: Int): String =
            String.format(Locale.GERMANY, periodString, period)

    companion object {
        private fun formatDate(pattern: String, date: Date): String =
                SimpleDateFormat(pattern, Locale.GERMANY).format(date)
    }

}