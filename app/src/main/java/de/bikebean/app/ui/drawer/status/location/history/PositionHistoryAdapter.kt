package de.bikebean.app.ui.drawer.status.location.history

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.TableLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import de.bikebean.app.MainActivity
import de.bikebean.app.R
import de.bikebean.app.db.DatabaseEntity
import de.bikebean.app.db.state.LocationState
import de.bikebean.app.ui.drawer.status.history.HistoryAdapter
import de.bikebean.app.ui.utils.date.DateUtils.convertPeriodToHuman

internal class PositionHistoryAdapter(
        context: Context,
        states: List<DatabaseEntity?>?
) : HistoryAdapter(context, states) {

    internal class PositionHistoryViewHolder(v: View) : HistoryViewHolder(v) {
        val table: TableLayout = v.findViewById(R.id.tableLayout2)
        val buttonOpenMap: CardView = v.findViewById(R.id.buttonOpenMap)
        val lat: TextView = v.findViewById(R.id.lat2)
        val lng: TextView = v.findViewById(R.id.lng2)
        val acc: TextView = v.findViewById(R.id.acc2)
        val dateTimeText: TextView = v.findViewById(R.id.dateTimeText6)
        val smsIdText: TextView = v.findViewById(R.id.smsIdText)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PositionHistoryViewHolder =
            PositionHistoryViewHolder(mInflater.inflate(
                    R.layout.recyclerview_item_position_history,
                    parent, false
            ))

    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {
        mStates?.let {
            (it[position] as LocationState).let { state ->
                (holder as PositionHistoryViewHolder).apply {
                    table.visibility = View.VISIBLE
                    lat.text = String.format("%.7f", state.lat)
                    lng.text = String.format("%.7f", state.lng)
                    acc.text = String.format("%.1f", state.acc)
                    dateTimeText.text = convertPeriodToHuman(state.timestamp)
                    smsIdText.text = String.format("SmsId: %d", state.smsId)
                    buttonOpenMap.setOnClickListener {
                        (ctx as MainActivity).navigateTo(R.id.map_action, state.args)
                    }
                }
            }
        } ?: run {
            (holder as PositionHistoryViewHolder).apply {
                table.visibility = View.INVISIBLE
                buttonOpenMap.visibility = View.INVISIBLE
                dateTimeText.visibility = View.GONE
                smsIdText.visibility = View.GONE
            }
        }
    }
}