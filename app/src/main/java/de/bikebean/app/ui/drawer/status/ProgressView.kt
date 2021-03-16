package de.bikebean.app.ui.drawer.status

import android.content.Context
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import de.bikebean.app.R
import de.bikebean.app.ui.utils.date.DateUtils.convertPeriodToHuman
import de.bikebean.app.ui.utils.date.DateUtils.convertToTime

class ProgressView(private val textView: TextView, private val progressBar: ProgressBar) {

    fun setVisibility(visible: Boolean) {
        if (!visible) {
            textView.text = ""
            textView.visibility = View.GONE
            progressBar.visibility = View.GONE
        } else {
            textView.visibility = View.VISIBLE
            progressBar.visibility = View.VISIBLE
        }
    }

    fun setText(text: String) {
        textView.text = text
    }

    fun setProgress(context: Context, startTime: Long, stopTime: Long, residualSeconds: Long) {
        val totalSeconds = ((stopTime - startTime) / 1000).toInt()
        val residualPercent = ((100 * residualSeconds) / totalSeconds).toInt()

        progressBar.progress = 100 - residualPercent
        progressBar.isIndeterminate = false

        val hours = residualSeconds.toInt() / 60 / 60
        val minutes = (residualSeconds / 60).toInt() - hours * 60

        val hoursPadded = if (hours < 10) "0$hours" else hours.toString()
        val minutesPadded = if (minutes < 10) "0$minutes" else minutes.toString()

        textView.text = context.getString(
                R.string.pending_text,
                "$hoursPadded:$minutesPadded",
                convertToTime(stopTime)
        )
    }

    fun setIndeterminateProgress(context: Context, stopTime: Long) {
        textView.text = context.getString(
                R.string.overdue,
                convertPeriodToHuman(stopTime)
        )

        progressBar.isIndeterminate = true
    }
}