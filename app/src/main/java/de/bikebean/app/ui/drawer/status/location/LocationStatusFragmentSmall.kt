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
import de.bikebean.app.R
import de.bikebean.app.db.sms.Sms
import de.bikebean.app.db.state.State
import de.bikebean.app.db.state.StateFactory.createPendingState
import de.bikebean.app.db.type.types.LocationType
import de.bikebean.app.ui.drawer.status.SubStatusFragmentSmall
import de.bikebean.app.ui.drawer.status.insert
import de.bikebean.app.ui.drawer.status.sendSms
import de.bikebean.app.ui.utils.wapp.WappSorter

class LocationStatusFragmentSmall : SubStatusFragmentSmall(), LocationElementsSetter {

    lateinit var st: LocationStateViewModel

    // UI Elements
    private var buttonGetLocation: Button? = null
    private var moreInfoButton: ImageView? = null
    private var helpButton: ImageView? = null
    private var titleText: TextView? = null
    private var locationInformationViewSmall: LocationInformationViewSmall? = null

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? = inflater
            .inflate(R.layout.fragment_status_location_small, container, false)
            .apply {
                buttonGetLocation = findViewById(R.id.sendButton)
                moreInfoButton = findViewById(R.id.moreInfoButton)
                helpButton = findViewById(R.id.helpButton)
                titleText = findViewById(R.id.titleText)
                locationInformationViewSmall = LocationInformationViewSmall(
                        findViewById(R.id.lat),
                        findViewById(R.id.lng),
                        findViewById(R.id.acc),
                        findViewById(R.id.bikeMarker),
                        findViewById(R.id.locationNoData)
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
            wapp.observe(l, ::updateWapp)
        }
    }

    override fun initUserInteractionElements() {
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
        initTransitionButton(moreInfoButton, helpButton, this, true)
        titleText!!.setText(R.string.heading_location)
    }

    override fun resetElements() = Unit

    // unset
    override fun setLocationElementsUnset() {
        locationInformationViewSmall!!.apply {
            setVisible(false)
            setMarker(null, this@LocationStatusFragmentSmall)
        }
    }

    private var isLocationPending: Boolean = false

    override fun setLocationPending(pending: Boolean) {
        isLocationPending = pending
    }

    override fun setLocationElementsProgressTimeUnset() = Unit

    // confirmed
    override fun setLocationElementsConfirmed(state: State) {
        locationInformationViewSmall!!.apply {
            setVisible(true)
            setMarker(state, this@LocationStatusFragmentSmall)
        }
    }

    override fun setLatConfirmed(state: State) {
        locationInformationViewSmall!!.setLat(state)
    }

    override fun setLngConfirmed(state: State) {
        locationInformationViewSmall!!.setLng(state)
    }

    override fun setAccConfirmed(state: State) {
        locationInformationViewSmall!!.setAcc(state)
    }

    override fun setLocationElementsProgressTimeConfirmed() = Unit
    override fun setLocationElementsNumbersConfirmed(state: State) = Unit

    // pending
    override fun setLocationElementsPending(state: State) {
        // BB has responded, but no response from Google Maps API yet
        locationInformationViewSmall!!.apply {
            setVisible(false)
            setMarker(st.getConfirmedLocationSync(state), this@LocationStatusFragmentSmall)
        }
    }

    override fun setLocationElementsProgressTimePending(state: State) = Unit

    override fun setLocationElementsProgressTextPending() = Unit

    private fun updateWapp(states: List<State>) {
        WappSorter(states, st).wappStates.forEach { wappState ->
            if (wappState.isNull || isAlreadyParsed(wappState)) return

            LocationUpdater(
                    requireContext(), st, lv,
                    ::updateLatLngAcc, wappState
            ).execute()
        }
    }

    private fun updateLatLngAcc(locationType: LocationType?) {
        locationType ?: return

        sm.markParsed(locationType.wappState.sms)
        st.confirmWapp(locationType.wappState)
        insert(st, locationType)
    }
}