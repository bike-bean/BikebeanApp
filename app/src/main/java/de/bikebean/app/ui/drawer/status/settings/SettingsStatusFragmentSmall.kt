package de.bikebean.app.ui.drawer.status.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.SwitchCompat
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModelProvider
import de.bikebean.app.MainActivity
import de.bikebean.app.R
import de.bikebean.app.db.sms.Sms.MESSAGE
import de.bikebean.app.db.state.State
import de.bikebean.app.db.state.StateFactory.createPendingState
import de.bikebean.app.ui.drawer.status.SubStatusFragmentSmall
import de.bikebean.app.ui.drawer.status.sendSms
import de.bikebean.app.ui.utils.resource.ResourceUtils.getCurrentIconColorFilter

class SettingsStatusFragmentSmall : SubStatusFragmentSmall(), SettingsElementsSetter {

    private lateinit var st: SettingsStateViewModel

    // UI Elements
    private var wlanSwitch: SwitchCompat? = null
    private var intervalValue: TextView? = null
    private var warningNumberSummary: TextView? = null
    private var titleText: TextView? = null
    private var moreInfoButton: ImageView? = null
    private var helpButton: ImageView? = null
    private var wlanImage: ImageView? = null
    private var intervalImage: ImageView? = null
    private var warningNumberImage: ImageView? = null

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View = inflater
            .inflate(R.layout.fragment_status_settings_small, container, false)
            .apply {
                wlanSwitch = findViewById(R.id.wlanSwitch)
                intervalValue = findViewById(R.id.intervalValue)
                warningNumberSummary = findViewById(R.id.warningNumberSummary)
                wlanImage = findViewById(R.id.wlanImage)
                intervalImage = findViewById(R.id.intervalImage)
                warningNumberImage = findViewById(R.id.warningNumberImage)
                titleText = findViewById(R.id.titleText)
                moreInfoButton = findViewById(R.id.moreInfoButton)
                helpButton = findViewById(R.id.helpButton)
            }

    override fun setupListeners(l: LifecycleOwner) {
        st = ViewModelProvider(this).get(SettingsStateViewModel::class.java).apply {
            statusWifi.observe(l, ::setElements)
            status.observe(l, ::setElements)
            statusWarningNumber.observe(l, ::setElements)
            statusInterval.observe(l, ::setElements)
        }
    }

    override fun initUserInteractionElements() {
        // React to user interactions
        wlanSwitch!!.setOnCheckedChangeListener { _: CompoundButton?, isChecked: Boolean ->
            // get the last confirmed wlan status and see if the value has changed from then
            // if it has not changed, return
            if (isChecked == st.wifiStatusSync) return@setOnCheckedChangeListener

            // if it has changed, create a new pending state and fire it into the db
            lv.d("Setting Wifi about to be changed to $isChecked")

            with(MESSAGE.WIFI) {
                setValue("Wifi " + if (isChecked) "on" else "off")
                sendSms(this, listOf(createPendingState(
                        State.KEY.WIFI, if (isChecked) 1.0 else 0.0)
                ))
            }
        }

        listOf(wlanImage, intervalImage, warningNumberImage).forEach {
            it!!.colorFilter = getCurrentIconColorFilter((requireActivity() as MainActivity))
        }

        initTransitionButton(moreInfoButton, helpButton, this, true)
        titleText!!.setText(R.string.heading_settings)
    }

    override fun resetElements() {
        wlanSwitch!!.isChecked = st.wifiStatusSync
    }

    // unset
    override fun setIntervalElementsUnset(state: State) {
        val interval = state.value.toInt().toString() + " h"
        intervalValue!!.text = interval
    }

    override fun setWarningNumberElementsUnset() {
        warningNumberSummary!!.text = getString(R.string.text_warning_number_not_set)
    }

    override fun setStatusElementsUnset() {}

    // confirmed
    override fun setIntervalElementsConfirmed(state: State) {
        val interval = state.value.toInt().toString() + " h"
        intervalValue!!.text = interval
    }

    override fun setWifiElementsConfirmed(state: State) {
        wlanSwitch!!.isChecked = state.value > 0
    }

    override fun setWarningNumberElementsConfirmed(state: State) {
        warningNumberSummary!!.text = state.longValue
    }

    override fun setStatusElementsConfirmed(state: State) {}

    // pending
    override fun setIntervalElementsPending(state: State) {
        val oldValue = st.intervalStatusSync
        val text = "$oldValue h -> ${state.value.toInt()} h"
        intervalValue!!.text = text
    }

    override fun setWifiElementsPending(state: State) {
        wlanSwitch!!.isChecked = state.value > 0
    }

    override fun setWarningNumberElementsPending(state: State) {
        warningNumberSummary!!.setText(R.string.text_warning_number_pending)
    }
}