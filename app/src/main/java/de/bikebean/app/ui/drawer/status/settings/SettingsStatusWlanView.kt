package de.bikebean.app.ui.drawer.status.settings

import android.view.View
import android.widget.*
import androidx.appcompat.widget.SwitchCompat
import androidx.lifecycle.LifecycleOwner
import com.google.android.material.card.MaterialCardView
import de.bikebean.app.R
import de.bikebean.app.db.sms.Sms.MESSAGE
import de.bikebean.app.db.state.State
import de.bikebean.app.db.state.StateFactory.createPendingState
import de.bikebean.app.ui.drawer.status.ProgressView
import de.bikebean.app.ui.drawer.status.sendSms
import de.bikebean.app.ui.drawer.status.settings.LiveDataTimerViewModel.TIMER

class SettingsStatusWlanView(
        cardView: MaterialCardView,
        icon: ImageView,
        private val wlanSwitch: SwitchCompat,
        subTitle: TextView,
        progressView: ProgressView
) : SettingsStatusSubView(cardView, icon, subTitle, progressView, TIMER.ONE) {

    override val helpResId: Int = R.string.text_help_wifi
    override val titleResId: Int = R.string.heading_wifi
    override val onCardViewClickListener: (View) -> Unit = { wlanSwitch.toggle() }

    override fun setupListeners(l: LifecycleOwner, f: SettingsStatusFragment) {
        f.st.statusWifi.observe(l, f::setElements)
    }

    override fun resetElements(f: SettingsStatusFragment) {
        wlanSwitch.isChecked = f.st.wifiStatusSync
    }

    override fun initUserInteractionElements(f: SettingsStatusFragment) {
        super.initUserInteractionElements(f)

        wlanSwitch.setOnCheckedChangeListener { _: CompoundButton?, isChecked: Boolean ->
            // get the last confirmed wlan status and see if the value has changed from then
            if (isChecked != f.st.wifiStatusSync) {
                // if it has changed, create a new pending state and fire it into the db
                f.lv.d("Setting Wifi about to be changed to $isChecked")

                with(MESSAGE.WIFI) {
                    setValue("Wifi " + if (isChecked) "on" else "off")

                    f.sendSms(this, listOf(createPendingState(
                            State.KEY.WIFI, if (isChecked) 1.0 else 0.0)
                    ))
                }
            }
        }
    }

    // unset

    // confirmed
    fun setWifiElementsConfirmed(state: State, f: SettingsStatusFragment) {
        f.tv.apply {
            getResidualTime(t).removeObservers(f)
            cancelTimer(t)
        }
        if (state.value > 0) {
            subTitle.setText(R.string.text_wifi_summary_on)
            wlanSwitch.isChecked = true
        } else {
            subTitle.setText(R.string.text_wifi_summary_off)
            wlanSwitch.isChecked = false
        }
        progressView.setVisibility(false)
    }

    // pending
    fun setWifiElementsPending(state: State, f: SettingsStatusFragment) {
        val stopTime = f.tv.startTimer(t, state.timestamp, f.st.confirmedIntervalSync)
        f.tv.getResidualTime(t).observe(f) { s ->
            f.updatePendingText(progressView, state.timestamp, stopTime, s!!)
        }

        if (state.value > 0) {
            subTitle.setText(R.string.text_wifi_on_transition)
            wlanSwitch.isChecked = true
        } else {
            subTitle.setText(R.string.text_wifi_off_transition)
            wlanSwitch.isChecked = false
        }

        progressView.setVisibility(true)
    }

}