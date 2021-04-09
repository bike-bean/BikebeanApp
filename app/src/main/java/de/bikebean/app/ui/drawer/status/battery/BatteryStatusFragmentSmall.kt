package de.bikebean.app.ui.drawer.status.battery

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModelProvider
import de.bikebean.app.R
import de.bikebean.app.db.sms.Sms
import de.bikebean.app.db.state.State
import de.bikebean.app.db.state.StateFactory.createPendingState
import de.bikebean.app.ui.drawer.status.SubStatusFragmentSmall
import de.bikebean.app.ui.drawer.status.sendSms

class BatteryStatusFragmentSmall : SubStatusFragmentSmall(), BatteryElementsSetter {

    private lateinit var st: BatteryStateViewModel

    // UI Elements
    private var statusButton: Button? = null
    private var batteryView: BatteryViewSmall? = null
    private var buttonBack: ImageView? = null
    private var helpButton: ImageView? = null
    private var titleText: TextView? = null

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? = inflater
            .inflate(R.layout.fragment_status_battery_small, container, false)
            .apply {
                statusButton = findViewById(R.id.sendButton)
                batteryView = BatteryViewSmall(
                        findViewById(R.id.batteryStatusText),
                        findViewById(R.id.batteryStatusImage)
                )
                buttonBack = findViewById(R.id.moreInfoButton)
                helpButton = findViewById(R.id.helpButton)
                titleText = findViewById(R.id.titleText)
            }

    override fun setupListeners(l: LifecycleOwner) {
        st = ViewModelProvider(this).get(BatteryStateViewModel::class.java).apply {
            statusBattery.observe(l, ::setElements)
            statusInterval.observe(l, ::setElements)
            statusWifi.observe(l, ::setElements)
            cellTowers.observe(l, ::setElements)
        }
    }

    override fun initUserInteractionElements() {
        statusButton!!.setOnClickListener {
            sendSms(Sms.MESSAGE._STATUS,
                    listOf(createPendingState(State.KEY.BATTERY, 0.0)),
                    isLocationPending,
                    isBatteryPending
            )
        }
        titleText!!.setText(R.string.heading_battery)
        initTransitionButton(buttonBack, helpButton, this, true)
    }

    override fun resetElements() = Unit

    // unset
    override fun setBatteryElementsUnset(state: State) {
        batteryView!!.setStatus(requireContext(), st)
    }

    // confirmed
    override fun setBatteryElementsConfirmed(state: State) {
        batteryView!!.setStatus(requireContext(), st)
    }

    override fun setIntervalElementsConfirmed() {
        batteryView!!.setStatus(requireContext(), st)
    }

    override fun setWifiElementsConfirmed(state: State) {
        batteryView!!.setStatus(requireContext(), st)
    }

    private var isLocationPending: Boolean = false
    private var isBatteryPending: Boolean = false

    override fun setBatteryPending(pending: Boolean) {
        isBatteryPending = pending
    }

    override fun setLocationPending(pending: Boolean) {
        isLocationPending = pending
    }

    // pending
    override fun setBatteryElementsPending(state: State) {
        batteryView!!.setStatus(requireContext(), st)
    }
}