package de.bikebean.app.ui.drawer.status.settings

import android.view.View
import android.widget.*
import android.widget.AdapterView.OnItemSelectedListener
import androidx.lifecycle.LifecycleOwner
import com.google.android.material.card.MaterialCardView
import de.bikebean.app.R
import de.bikebean.app.db.sms.Sms.MESSAGE
import de.bikebean.app.db.state.State
import de.bikebean.app.db.state.StateFactory.createPendingState
import de.bikebean.app.ui.drawer.status.ProgressView
import de.bikebean.app.ui.drawer.status.sendSms
import de.bikebean.app.ui.drawer.status.settings.LiveDataTimerViewModel.TIMER
import de.bikebean.app.ui.utils.resource.ResourceUtils.getIntervalString

class SettingsStatusIntervalView(
        private val intervalDropdown: Spinner,
        intervalSummary: TextView,
        private val nextUpdateEstimation: TextView,
        intervalProgressView: ProgressView,
        icon: ImageView,
        cardView: MaterialCardView
) : SettingsStatusSubView(cardView, icon, intervalSummary, intervalProgressView, TIMER.TWO) {

    override val helpResId: Int = R.string.text_help_interval
    override val titleResId: Int = R.string.heading_interval
    override val onCardViewClickListener: (View) -> Unit = { intervalDropdown.performClick() }

    override fun setupListeners(l: LifecycleOwner, f: SettingsStatusFragment) {
        f.st.statusInterval.observe(l, f::setElements)
    }

    override fun resetElements(f: SettingsStatusFragment) {
        intervalDropdown.setSelection(getIntervalPosition(f.st.intervalStatusSync, f))
    }

    override fun initUserInteractionElements(f: SettingsStatusFragment) {
        super.initUserInteractionElements(f)

        // React to user interactions
        intervalDropdown.onItemSelectedListener = object : OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>,
                                        view: View, position: Int, id: Long) {
                val newValue = getIntervalString(position, f.requireContext())

                // See if the "new" value is actually just the placeholder.
                // In that case, set the text underneath to reflect the last known status
                if (newValue == "0") return

                // Get the last confirmed interval status and
                // see if the value has changed from then.
                // If it has not changed, return
                if (position == getIntervalPosition(f.st.intervalStatusSync, f)) return

                // if it has changed, create a new pending state and fire it into the db
                f.lv.d("Setting Interval about to be changed to $newValue")

                with(MESSAGE.INT) {
                    setValue("Int $newValue")

                    f.sendSms(this, arrayOf(createPendingState(
                            State.KEY.INTERVAL, newValue.toDouble())
                    ))
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // Do nothing
            }
        }
    }

    private fun getIntervalPosition(intervalValue: Int, f: SettingsStatusFragment): Int {
        val items = f.resources.getStringArray(R.array.interval_values)
        items.forEachIndexed { index, i -> if (i.equals("$intervalValue")) return index }
        return 0
    }

    fun initIntervalDropdown(f: SettingsStatusFragment) {
        intervalDropdown.adapter = ArrayAdapter.createFromResource(
                f.requireActivity(),
                R.array.interval_entries,
                android.R.layout.simple_spinner_item
        ).apply {
            setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }
    }

    // unset
    fun setIntervalElementsUnset(state: State, f: SettingsStatusFragment) {
        val intervalSummaryString = f.getString(R.string.text_interval_summary)
        f.tv.getResidualTime(t).removeObservers(f)
        f.tv.cancelTimer(t)
        val oldValue = state.value.toInt().toString()
        subTitle.text = String.format(intervalSummaryString, oldValue)
        progressView.setVisibility(false)
    }

    // confirmed
    fun setIntervalElementsConfirmed(state: State, f: SettingsStatusFragment) {
        val intervalSummaryString = f.getString(R.string.text_interval_summary)
        f.tv.getResidualTime(t).removeObservers(f)
        f.tv.cancelTimer(t)
        val oldValue = state.value.toInt().toString()
        subTitle.text = String.format(intervalSummaryString, oldValue)
        progressView.setVisibility(false)
    }

    // pending
    fun setIntervalElementsPending(state: State, f: SettingsStatusFragment) {
        val intervalTransitionString = f.getString(R.string.text_interval_transition)
        val stopTime = f.tv.startTimer(t, state.timestamp, f.st.confirmedIntervalSync)
        f.tv.getResidualTime(t).observe(f) { s ->
            f.updatePendingText(progressView, state.timestamp, stopTime, s!!)
        }
        subTitle.text = String.format(intervalTransitionString, state.value.toInt())
        progressView.setVisibility(true)

        // nextUpdateEstimation.setText("Nächstes Aufwachen ca." + Utils.convertToTime(dt) + "  " + getStringInt(n) + " " + getStringInt(e));
        nextUpdateEstimation.visibility = View.GONE
    }

}