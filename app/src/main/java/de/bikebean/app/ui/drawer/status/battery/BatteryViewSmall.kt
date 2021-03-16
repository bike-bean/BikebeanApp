package de.bikebean.app.ui.drawer.status.battery

import android.content.Context
import android.widget.ImageView
import android.widget.TextView

import de.bikebean.app.db.settings.settings.add_to_list_settings.Battery.Companion.UNSET_BATTERY
import de.bikebean.app.ui.utils.date.BatteryBehaviour
import de.bikebean.app.ui.utils.resource.ResourceUtils.getBatteryDrawable

open class BatteryViewSmall (
        private val statusText: TextView,
        private val statusImage: ImageView) {

    open fun setStatus(context: Context, st: BatteryStateViewModel) : BatteryBehaviour? {
        val batteryBehaviour = st.batteryBehaviour ?: run {
            setStatus(context, UNSET_BATTERY)
            return null
        }

        setStatus(context, batteryBehaviour.getCurrentPercent())
        return batteryBehaviour
    }

    private fun setStatus(context: Context, batteryStatus: Double) {
        if (batteryStatus == UNSET_BATTERY)
            statusText.text = ""
        else {
            val batteryStatusString = "${batteryStatus.toInt()} %"
            statusText.text = batteryStatusString
        }

        statusImage.setImageDrawable(getBatteryDrawable(context, batteryStatus))
    }
}