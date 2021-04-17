package de.bikebean.app.ui.drawer.status.battery.history

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import de.bikebean.app.R
import de.bikebean.app.db.DatabaseEntity
import de.bikebean.app.db.state.State
import de.bikebean.app.ui.drawer.status.history.HistoryAdapter
import de.bikebean.app.ui.utils.date.DateUtils.convertPeriodToHuman
import de.bikebean.app.ui.utils.resource.ResourceUtils.getBatteryDrawable

internal class BatteryHistoryAdapter(
        context: Context,
        states: List<DatabaseEntity?>?
) : HistoryAdapter(context, states) {

    internal class BatteryHistoryViewHolder(v: View) : HistoryViewHolder(v) {
        val batteryValue: TextView = v.findViewById(R.id.batteryValue)
        val dateTimeText: TextView = v.findViewById(R.id.dateTimeText)
        val smsIdText: TextView = v.findViewById(R.id.smsIdText)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BatteryHistoryViewHolder =
            BatteryHistoryViewHolder(mInflater.inflate(
                    R.layout.recyclerview_item_battery_history,
                    parent, false
            ))

    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {
        mStates?.let {
            (it[position] as State).let { state ->
                (holder as BatteryHistoryViewHolder).apply {
                    batteryValue.apply {
                        text = String.format("${state.value.toInt()} %%")
                        setCompoundDrawablesWithIntrinsicBounds(
                                getBatteryDrawable(ctx, state.value),
                                null, null, null
                        )
                    }
                    dateTimeText.text = convertPeriodToHuman(state.timestamp)
                    smsIdText.text = String.format("SmsId: ${state.smsId}")
                }
            }
        } ?: run {
            (holder as BatteryHistoryViewHolder).apply {
                batteryValue.visibility = View.INVISIBLE
                dateTimeText.visibility = View.GONE
                smsIdText.visibility = View.GONE
            }
        }
    }
}