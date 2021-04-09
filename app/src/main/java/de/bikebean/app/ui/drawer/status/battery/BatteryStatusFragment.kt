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
import de.bikebean.app.ui.drawer.status.LastChangedView
import de.bikebean.app.ui.drawer.status.ProgressView
import de.bikebean.app.ui.drawer.status.SubStatusFragment
import de.bikebean.app.ui.drawer.status.sendSms
import de.bikebean.app.ui.drawer.status.settings.LiveDataTimerViewModel.TIMER
import de.bikebean.app.ui.utils.Utils

class BatteryStatusFragment : SubStatusFragment(), BatteryElementsSetter {

    private lateinit var st: BatteryStateViewModel
    private val t1 = TIMER.FOUR

    // UI Elements
    private var statusButton: Button? = null
    private var progressView: ProgressView? = null
    private var batteryView: BatteryView? = null
    private var buttonBack: ImageView? = null
    private var helpButton: ImageView? = null
    private var titleText: TextView? = null

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? = inflater
            .inflate(R.layout.fragment_status_battery, container, false)
            .apply {
                helpButton = findViewById(R.id.helpButton)
                lastChangedView = LastChangedView(
                        findViewById(R.id.lastChangedText),
                        findViewById(R.id.lastChangedIndicator)
                )
                statusButton = findViewById(R.id.sendButton)
                progressView = ProgressView(
                        findViewById(R.id.pendingStatusText),
                        findViewById(R.id.progressBar)
                )
                buttonBack = findViewById(R.id.moreInfoButton)
                titleText = findViewById(R.id.titleText)
                batteryView = BatteryView(
                        findViewById(R.id.batteryStatusText),
                        findViewById(R.id.batteryStatusImage),
                        findViewById(R.id.batteryEstimatedStatusText),
                        findViewById(R.id.batteryRuntimeEstimationText),
                        findViewById(R.id.batteryLastKnownStatusText)
                )
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
        helpButton!!.setOnClickListener(Utils::onHelpClick)
        initTransitionButton(buttonBack, helpButton, this, false)
        titleText!!.setText(R.string.heading_battery)
    }

    override fun resetElements() = Unit

    // unset
    override fun setBatteryElementsUnset(state: State) {
        tv.apply {
            getResidualTime(t1).removeObservers(this@BatteryStatusFragment)
            cancelTimer(t1)
        }
        lastChangedView.set(null, this)
        progressView!!.setVisibility(false)
        batteryView!!.apply {
            setStatus(requireContext(), st)
            setEstimationText(requireContext(), R.string.text_no_data)
        }
    }

    // confirmed
    override fun setBatteryElementsConfirmed(state: State) {
        tv.apply {
            getResidualTime(t1).removeObservers(this@BatteryStatusFragment)
            cancelTimer(t1)
        }
        lastChangedView.set(state, this)
        progressView!!.setVisibility(false)
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
        tv.apply {
            startTimer(t1, state.timestamp, st.confirmedIntervalSync).let { stopTime ->
                getResidualTime(t1).observe(this@BatteryStatusFragment) { s ->
                    updatePendingText(progressView!!, state.timestamp, stopTime, s)
                }
            }
        }

        lastChangedView.set(st.confirmedBatterySync, this)
        progressView!!.setVisibility(true)
        batteryView!!.setStatus(requireContext(), st)
    }
}