package de.bikebean.app.ui.drawer.status.battery.history

import android.content.Context
import de.bikebean.app.ui.drawer.status.history.HistoryFragment
import android.view.LayoutInflater
import android.view.ViewGroup
import android.os.Bundle
import android.view.View
import de.bikebean.app.R
import de.bikebean.app.ui.drawer.status.history.HistoryViewModel
import androidx.lifecycle.ViewModelProvider
import de.bikebean.app.ui.drawer.status.history.HistoryAdapter

class BatteryHistoryFragment : HistoryFragment() {

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View = inflater
            .inflate(R.layout.fragment_history_battery, container, false)
            .apply {
                recyclerView = findViewById(R.id.recyclerView3)
                noDataText = findViewById(R.id.noDataText3)
            }.also {
                super.onCreateView(inflater, container, savedInstanceState)
            }

    override val newStateViewModel: HistoryViewModel get() =
            ViewModelProvider(this).get(BatteryHistoryViewModel::class.java)

    override fun setupListeners() {
        (st as BatteryHistoryViewModel).batteryConfirmed.observe(this, ::setStatesToAdapter)
    }

    override fun getNewAdapter(ctx: Context): HistoryAdapter = BatteryHistoryAdapter(ctx,
            (st as BatteryHistoryViewModel).batteryConfirmed.value)

}