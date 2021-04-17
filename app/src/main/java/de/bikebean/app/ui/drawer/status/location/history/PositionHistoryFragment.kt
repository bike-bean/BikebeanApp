package de.bikebean.app.ui.drawer.status.location.history

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import de.bikebean.app.R
import de.bikebean.app.db.state.LocationState
import de.bikebean.app.db.state.State
import de.bikebean.app.db.state.State.KEY
import de.bikebean.app.ui.drawer.sms_history.SmsViewModel
import de.bikebean.app.ui.drawer.status.history.HistoryAdapter
import de.bikebean.app.ui.drawer.status.history.HistoryFragment
import de.bikebean.app.ui.drawer.status.history.HistoryViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class PositionHistoryFragment : HistoryFragment() {

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View = inflater
            .inflate(R.layout.fragment_history_position, container, false)
            .apply {
                recyclerView = findViewById(R.id.recyclerView2)
                noDataText = findViewById(R.id.noDataText)
            }.also {
                super.onCreateView(inflater, container, savedInstanceState)
            }

    override val newStateViewModel: HistoryViewModel get() =
            ViewModelProvider(this).get(PositionHistoryViewModel::class.java)

    override fun setupListeners() {
        ViewModelProvider(this).get(SmsViewModel::class.java).allIds
                .observe(this, ::updateStates)
    }

    override fun getNewAdapter(ctx: Context): HistoryAdapter = PositionHistoryAdapter(ctx,
            (st as PositionHistoryViewModel).locationStates.value
    )

    private fun updateStates(smsIdList: List<Int>) {
        val st = st as PositionHistoryViewModel
        lifecycleScope.launch {
            withContext(Dispatchers.Default) {
                st.setLocationsState(
                        List(smsIdList.size) {
                            updateLocationStates(st.getAllLocation(smsIdList[it]))
                        }.filterNotNull()
                )
            }
        }
        st.locationStates.removeObservers(this)
        st.locationStates.observe(this, ::setStatesToAdapter)
    }

    private fun updateLocationStates(states: List<State>): LocationState? = listOfNotNull(
            states.firstOrNull(KEY::isLat),
            states.firstOrNull(KEY::isLng),
            states.firstOrNull(KEY::isAcc),
            states.firstOrNull(KEY::isNoCellTowers),
            states.firstOrNull(KEY::isNoWifiAccessPoints)
    ).let { list ->
        if (list.size == 5) LocationState(
                list.component1(),
                list.component2(),
                list.component3(),
                list.component4(),
                list.component5()
        ) else null
    }
}