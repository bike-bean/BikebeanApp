package de.bikebean.app.ui.drawer.status.battery

import android.content.Context
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.StringRes
import de.bikebean.app.R
import de.bikebean.app.ui.utils.date.BatteryBehaviour

class BatteryView (
        statusText: TextView, statusImage: ImageView,
        private val estimatedStatus: TextView,
        private val runtimeEstimation: TextView,
        private val lastKnownStatus: TextView)
    : BatteryViewSmall(statusText, statusImage) {

    override fun setStatus(context: Context, st: BatteryStateViewModel) : BatteryBehaviour? {
        val batteryBehaviour = super.setStatus(context, st) ?: run {
            setEstimationText(context, R.string.no_data)
            return null
        }

        lastKnownStatus.text = context.getString(
                R.string.last_known_text,
                batteryBehaviour.getReferenceString()
        )

        if (batteryBehaviour.referenceState.percent < 10) {
            setEstimationText(context, R.string.battery_warning_string)
            return batteryBehaviour
        }

        if (batteryBehaviour.getRemainingString().isEmpty()) {
            setEstimationText(context, R.string.battery_warning_string)
            return batteryBehaviour
        }

        estimatedStatus.setText(R.string.estimated_charge_text)
        runtimeEstimation.visibility = View.VISIBLE
        runtimeEstimation.text = context.getString(
                R.string.estimated_remaining_text,
                batteryBehaviour.getRemainingString(), batteryBehaviour.getChargeDateString()
        )

        return batteryBehaviour
    }

    fun setEstimationText(context: Context, @StringRes stringRes: Int) {
        runtimeEstimation.visibility = View.GONE
        estimatedStatus.text = context.getString(stringRes, "")
    }
}