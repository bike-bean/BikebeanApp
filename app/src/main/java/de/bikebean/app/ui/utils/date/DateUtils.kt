package de.bikebean.app.ui.utils.date

import android.os.Bundle
import de.bikebean.app.db.state.State
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

object DateUtils {

    const val DAYS_SINCE_LAST_STATE = "daysSinceLastState"

    @JvmStatic
    fun getDaysSinceState(state: State?): Double {
        state ?: return Double.MAX_VALUE

        return (Date().time - state.timestamp) / 1000.0 / 60 / 60 / 24
    }

    @JvmStatic
    fun getDaysSinceState(args: Bundle?): Double =
            args?.getDouble(DAYS_SINCE_LAST_STATE, 0.0) ?: 0.0

    @JvmStatic
    fun getLastChanged(state: State): String = Period(state.timestamp).lastChangedString

    @JvmStatic
    fun convertToDateHuman(): String = Period(Date().time).convertPeriodToHuman()

    @JvmStatic
    fun convertPeriodToHuman(datetime: Long): String = Period(datetime).convertPeriodToHuman()

    @JvmStatic
    fun convertToTimeLog(datetime: Long): String = formatDate("dd.MM.yy HH:mm", datetime)

    @JvmStatic
    fun convertToTime(datetime: Long): String = formatDate("dd.MM HH:mm", datetime)

    private fun formatDate(pattern: String, datetime: Long): String =
            SimpleDateFormat(pattern, Locale.GERMANY).format(Date(datetime))

    @JvmStatic
    fun getDateFromUTCString(UTCString: String): Date =
            with (SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.GERMANY)) {
                try {
                    parse(UTCString)
                } catch (e: ParseException) {
                    Date(1)
                } ?: Date(1)
            }
}