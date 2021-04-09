package de.bikebean.app.ui.drawer.status.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.snackbar.Snackbar
import de.bikebean.app.R
import de.bikebean.app.db.state.State
import de.bikebean.app.ui.drawer.status.LastChangedView
import de.bikebean.app.ui.drawer.status.ProgressView
import de.bikebean.app.ui.drawer.status.SubStatusFragment

class SettingsStatusFragment : SubStatusFragment(), SettingsElementsSetter {

    lateinit var st: SettingsStateViewModel

    // UI Elements
    private var titleText: TextView? = null
    private var backButton: ImageView? = null
    private var helpButton: ImageView? = null
    private var settingsStatusWlanView: SettingsStatusWlanView? = null
    private var settingsStatusIntervalView: SettingsStatusIntervalView? = null
    private var settingsStatusWarningNumberView: SettingsStatusWarningNumberView? = null
    private var settingsStatusSubViews: List<SettingsStatusSubView>? = null

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View = inflater
            .inflate(R.layout.fragment_status_settings, container, false)
            .apply {
                lastChangedView = LastChangedView(
                        findViewById(R.id.lastChangedText),
                        findViewById(R.id.lastChangedIndicator)
                )
                titleText = findViewById(R.id.titleText)
                backButton = findViewById(R.id.moreInfoButton)
                helpButton = findViewById(R.id.helpButton)

                findViewById<ProgressBar>(R.id.progressBar).let { progressBar ->
                    val wlanProgressView = ProgressView(
                            findViewById(R.id.wlanPendingStatus), progressBar
                    )
                    val intervalProgressView = ProgressView(
                            findViewById(R.id.intervalPendingStatus), progressBar
                    )
                    val warningNumberProgressView = ProgressView(
                            findViewById(R.id.warningNumberPendingStatus), progressBar
                    )
                    settingsStatusWlanView = SettingsStatusWlanView(
                            findViewById(R.id.wlanCardView),
                            findViewById(R.id.wlanImage),
                            findViewById(R.id.wlanSwitch),
                            findViewById(R.id.wlanSummary),
                            wlanProgressView
                    )
                    settingsStatusIntervalView = SettingsStatusIntervalView(
                            findViewById(R.id.intervalDropdown),
                            findViewById(R.id.intervalSummary),
                            findViewById(R.id.nextUpdateEstimation),
                            intervalProgressView,
                            findViewById(R.id.intervalImage),
                            findViewById(R.id.intervalCardView)
                    )
                    settingsStatusWarningNumberView = SettingsStatusWarningNumberView(
                            findViewById(R.id.sendButton),
                            findViewById(R.id.warningNumberSummary),
                            warningNumberProgressView,
                            findViewById(R.id.warningNumberImage),
                            findViewById(R.id.warningNumberCardView)
                    )
                }

                settingsStatusSubViews = listOf(
                        settingsStatusWlanView!!,
                        settingsStatusIntervalView!!,
                        settingsStatusWarningNumberView!!
                )
            }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        settingsStatusIntervalView!!.initIntervalDropdown(this)
    }

    override fun setupListeners(l: LifecycleOwner) {
        st = ViewModelProvider(this).get(SettingsStateViewModel::class.java)
        st.status.observe(l, ::setElements)
        for (s in settingsStatusSubViews!!) s.setupListeners(l, this)
    }

    override fun initUserInteractionElements() {
        // React to user interactions
        helpButton!!.setOnClickListener { v -> onHelpClick(v) }
        initTransitionButton(backButton, helpButton, this, false)
        titleText!!.setText(R.string.heading_settings)
        for (s in settingsStatusSubViews!!) s.initUserInteractionElements(this)
    }

    override fun resetElements() {
        for (s in settingsStatusSubViews!!) s.resetElements(this)
    }

    // unset
    override fun setWarningNumberElementsUnset() {
        settingsStatusWarningNumberView!!.setWarningNumberElementsUnset(this)
    }

    override fun setIntervalElementsUnset(state: State) {
        settingsStatusIntervalView!!.setIntervalElementsUnset(state, this)
    }

    override fun setStatusElementsUnset() {
        lastChangedView.set(null, this)
    }

    // confirmed
    override fun setIntervalElementsConfirmed(state: State) {
        settingsStatusIntervalView!!.setIntervalElementsConfirmed(state, this)
    }

    override fun setWifiElementsConfirmed(state: State) {
        settingsStatusWlanView!!.setWifiElementsConfirmed(state, this)
    }

    override fun setWarningNumberElementsConfirmed(state: State) {
        settingsStatusWarningNumberView!!.setWarningNumberElementsConfirmed(state, this)
    }

    override fun setStatusElementsConfirmed(state: State) {
        lastChangedView.set(state, this)
    }

    // pending
    override fun setIntervalElementsPending(state: State) {
        settingsStatusIntervalView!!.setIntervalElementsPending(state, this)
    }

    override fun setWifiElementsPending(state: State) {
        settingsStatusWlanView!!.setWifiElementsPending(state, this)
    }

    override fun setWarningNumberElementsPending(state: State) {
        settingsStatusWarningNumberView!!.setWarningNumberElementsPending(state, this)
    }

    private fun onHelpClick(v: View) {
        Snackbar.make(
                v,
                R.string.text_help_settings,
                Snackbar.LENGTH_LONG
        ).show()
    }
}