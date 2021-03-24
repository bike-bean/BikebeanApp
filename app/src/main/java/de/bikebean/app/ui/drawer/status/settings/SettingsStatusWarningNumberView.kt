package de.bikebean.app.ui.drawer.status.settings

import android.view.View
import android.widget.*
import androidx.lifecycle.LifecycleOwner
import com.google.android.material.card.MaterialCardView
import de.bikebean.app.R
import de.bikebean.app.db.sms.Sms
import de.bikebean.app.db.state.State
import de.bikebean.app.db.state.StateFactory.createPendingState
import de.bikebean.app.ui.drawer.status.ProgressView
import de.bikebean.app.ui.drawer.status.sendSms
import de.bikebean.app.ui.drawer.status.settings.LiveDataTimerViewModel.TIMER

class SettingsStatusWarningNumberView(
        private val sendButton: Button,
        subTitle: TextView,
        progressView: ProgressView,
        icon: ImageView,
        cardView: MaterialCardView
) : SettingsStatusSubView(cardView, icon, subTitle, progressView, TIMER.THREE) {

    override val helpResId: Int = R.string.text_help_warning_number
    override val titleResId: Int = R.string.heading_warning_number
    override val onCardViewClickListener: (View) -> Unit = { sendButton.callOnClick() }

    override fun setupListeners(l: LifecycleOwner, f: SettingsStatusFragment) {
        f.st.statusWarningNumber.observe(l, f::setElements)
    }

    override fun resetElements(f: SettingsStatusFragment) = Unit

    override fun initUserInteractionElements(f: SettingsStatusFragment) {
        super.initUserInteractionElements(f)

        sendButton.setOnClickListener {
            f.sendSms(Sms.MESSAGE.WARNING_NUMBER, arrayOf(createPendingState(
                    State.KEY.WARNING_NUMBER, 0.0)
            ))
        }
    }

    // unset
    fun setWarningNumberElementsUnset(f: SettingsStatusFragment) {
        f.tv.getResidualTime(t).removeObservers(f)
        f.tv.cancelTimer(t)
        subTitle.text = f.getString(R.string.text_warning_number_not_set)
        progressView.setVisibility(false)
    }

    // confirmed
    fun setWarningNumberElementsConfirmed(state: State, f: SettingsStatusFragment) {
        f.tv.getResidualTime(t).removeObservers(f)
        f.tv.cancelTimer(t)
        subTitle.text = state.longValue
        progressView.setVisibility(false)
    }

    // pending
    fun setWarningNumberElementsPending(state: State, f: SettingsStatusFragment) {
        val stopTime = f.tv.startTimer(t, state.timestamp, f.st.confirmedIntervalSync)
        f.tv.getResidualTime(t).observe(f, { s ->
            f.updatePendingText(progressView, state.timestamp, stopTime, s!!) }
        )
        subTitle.setText(R.string.text_warning_number_pending)
        progressView.setVisibility(true)
    }

}