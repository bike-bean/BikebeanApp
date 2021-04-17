package de.bikebean.app.ui.drawer.status.location

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.snackbar.Snackbar
import de.bikebean.app.MainActivity
import de.bikebean.app.R
import de.bikebean.app.db.sms.Sms
import de.bikebean.app.db.state.State
import de.bikebean.app.db.state.StateFactory.createPendingState
import de.bikebean.app.db.type.types.LocationType
import de.bikebean.app.ui.drawer.status.*
import de.bikebean.app.ui.drawer.status.settings.LiveDataTimerViewModel.TIMER
import de.bikebean.app.ui.utils.wapp.WappSorter

class LocationStatusFragment : SubStatusFragment(), LocationElementsSetter {

    lateinit var st: LocationStateViewModel
    private val t1 = TIMER.FIVE

    // UI Elements
    private var progressView: ProgressView? = null
    private var buttonGetLocation: Button? = null
    private var historyButton: Button? = null
    private var buttonBack: ImageView? = null
    private var helpButton: ImageView? = null
    private var titleText: TextView? = null
    private var previousText: TextView? = null
    private var locationInformationView: LocationInformationView? = null

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? = inflater
            .inflate(R.layout.fragment_status_location, container, false)
            .apply {
                helpButton = findViewById(R.id.helpButton)
                lastChangedView = LastChangedView(
                        findViewById(R.id.lastChangedText),
                        findViewById(R.id.lastChangedIndicator)
                )
                buttonGetLocation = findViewById(R.id.sendButton)
                historyButton = findViewById(R.id.historyButton)
                progressView = ProgressView(
                        findViewById(R.id.pendingStatusText),
                        findViewById(R.id.progressBar)
                )
                buttonBack = findViewById(R.id.moreInfoButton)
                titleText = findViewById(R.id.titleText)
                previousText = findViewById(R.id.textPrevious)
                locationInformationView = LocationInformationView(
                        findViewById(R.id.lat),
                        findViewById(R.id.lng),
                        findViewById(R.id.acc),
                        findViewById(R.id.bikeMarker),
                        findViewById(R.id.locationNoData),
                        findViewById(R.id.buttonOpenMap),
                        findViewById(R.id.routeButton),
                        findViewById(R.id.shareButton),
                        findViewById(R.id.noCellTowersText),
                        findViewById(R.id.noWifiAccessPointsText)
                )
            }

    override fun setupListeners(l: LifecycleOwner) {
        /*
         Observe any changes to
         lat, lng or acc in the database
         (i.e. after the location updater has written
         its stuff in there)
         */
        st = ViewModelProvider(this).get(LocationStateViewModel::class.java).apply {
            statusLocationLat.observe(l, ::setElements)
            statusLocationLng.observe(l, ::setElements)
            statusLocationAcc.observe(l, ::setElements)
            cellTowers.observe(l, ::setElements)
            wifiAccessPoints.observe(l, ::setElements)
            location.observe(l, ::setElements)
            statusNumberCellTowers.observe(l, ::setElements)
            statusNumberWifiAccessPoints.observe(l, ::setElements)
            wapp.observe(l, ::updateWapp)
            statusLocationLat.observe(l, ::setHistoryNumber)
        }
    }

    override fun initUserInteractionElements() {
        /*
         Set the function of the buttons
         */
        locationInformationView!!.setOnClickListeners(
                { navigateToNext() },
                { onRouteClick() },
                { shareLocation() }
        )
        helpButton!!.setOnClickListener { v -> onHelpClick(v) }
        historyButton!!.setOnClickListener{ navigateToHistory() }

        /* Insert two new pending States to mark waiting for response */
        buttonGetLocation!!.setOnClickListener {
            sendSms(Sms.MESSAGE.WAPP,
                    listOf(
                            createPendingState(State.KEY.LOCATION, 0.0),
                            createPendingState(State.KEY.CELL_TOWERS, 0.0),
                            createPendingState(State.KEY.WIFI_ACCESS_POINTS, 0.0)
                    ),
                    isLocationPending
            )
        }
        initTransitionButton(buttonBack, helpButton, this, false)
        titleText!!.setText(R.string.heading_location)
    }

    override fun resetElements() = Unit

    // unset
    override fun setLocationElementsUnset() {
        progressView!!.apply {
            setText("")
            setVisibility(false)
        }
        lastChangedView.set(null, this)
        locationInformationView!!.apply {
            setVisible(false)
            setMarker(null, this@LocationStatusFragment)
        }
    }

    private var isLocationPending: Boolean = false

    override fun setLocationPending(pending: Boolean) {
        isLocationPending = pending
    }

    override fun setLocationElementsProgressTimeUnset() {
        tv.apply {
            getResidualTime(t1).removeObservers(this@LocationStatusFragment)
            cancelTimer(t1)
        }
    }

    // confirmed
    override fun setLocationElementsConfirmed(state: State) {
        progressView!!.apply {
            setText("")
            setVisibility(false)
        }
        lastChangedView.set(state, this)
        locationInformationView!!.apply {
            setVisible(true)
            setMarker(state, this@LocationStatusFragment)
        }
    }

    override fun setLatConfirmed(state: State) {
        locationInformationView!!.setLat(state)
    }

    override fun setLngConfirmed(state: State) {
        locationInformationView!!.setLng(state)
    }

    override fun setAccConfirmed(state: State) {
        locationInformationView!!.setAcc(state)
    }

    override fun setLocationElementsProgressTimeConfirmed() {
        tv.apply {
            getResidualTime(t1).removeObservers(this@LocationStatusFragment)
            cancelTimer(t1)
        }
    }

    override fun setLocationElementsNumbersConfirmed(state: State) {
        locationInformationView!!.setNumbers(state)
    }

    // pending
    override fun setLocationElementsPending(state: State) {
        // BB has responded, but no response from Google Maps API yet
        st.getConfirmedLocationSync(state).let { lastLocationState ->
            progressView!!.setVisibility(true)
            lastChangedView.set(lastLocationState, this)
            locationInformationView!!.apply {
                setVisible(false)
                setMarker(lastLocationState, this@LocationStatusFragment)
            }
        }
    }

    override fun setLocationElementsProgressTimePending(state: State) {
        /*
         User has clicked the update button, but no response from BB yet
         */
        tv.apply {
            startTimer(t1, state.timestamp, st.confirmedIntervalSync).let { stopTime ->
                getResidualTime(t1).observe(this@LocationStatusFragment) { s ->
                    updatePendingText(progressView!!, state.timestamp, stopTime, s)
                }
            }
        }
    }

    override fun setLocationElementsProgressTextPending() {
        progressView!!.setText("Rohdaten empfangen, empfange genaue Position vom Server...")
    }

    private fun updateWapp(states: List<State>) {
        WappSorter(states, st).wappStates.forEach { wappState ->
            if (wappState.isNull || isAlreadyParsed(wappState)) return

            LocationUpdater(
                    requireContext(), st, lv,
                    ::updateLatLngAcc, wappState
            ).execute()
        }
    }

    private fun setHistoryNumber(states: List<State>) {
        previousText?.text =
                String.format("Anzahl der bisher gefundenen Standorte: ${states.filter { 
                    State.STATUS.getValue(it) == State.STATUS.CONFIRMED 
                }.size}")
    }

    private fun updateLatLngAcc(locationType: LocationType?) {
        locationType ?: return

        sm.markParsed(locationType.wappState.sms)
        st.confirmWapp(locationType.wappState)
        insert(st, locationType)
    }

    private fun navigateToNext() {
        (requireActivity() as MainActivity).apply {
            resumeToolbarAndBottomSheet()
            navigateTo(R.id.map_action, null)
        }
    }

    private fun shareLocation() {
        mf.startShareIntent(this)
    }

    private fun onHelpClick(v: View) {
        Snackbar.make(v,
                R.string.text_help_location,
                Snackbar.LENGTH_LONG
        ).show()
    }

    private fun onRouteClick() {
        mf.startRouteIntent(this)
    }

    private fun navigateToHistory() {
        (requireActivity() as MainActivity).apply {
            resumeToolbarAndBottomSheet()
            navigateTo(R.id.position_action, null)
        }
    }

}