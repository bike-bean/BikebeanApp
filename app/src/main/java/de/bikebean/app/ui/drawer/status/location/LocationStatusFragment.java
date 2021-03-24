package de.bikebean.app.ui.drawer.status.location;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.snackbar.Snackbar;

import de.bikebean.app.MainActivity;
import de.bikebean.app.R;
import de.bikebean.app.db.state.StateFactory;
import de.bikebean.app.ui.drawer.status.LastChangedView;
import de.bikebean.app.ui.drawer.status.ProgressView;
import de.bikebean.app.db.sms.Sms;
import de.bikebean.app.db.state.State;
import de.bikebean.app.ui.drawer.status.SubStatusFragment;
import de.bikebean.app.ui.drawer.status.settings.LiveDataTimerViewModel;

import static de.bikebean.app.ui.drawer.status.SubStatusFragmentSmallExtKt.sendSms;
import static de.bikebean.app.ui.drawer.status.location.LocationStatusFragmentExtKt.startObservingWapp;

public class LocationStatusFragment extends SubStatusFragment implements LocationElementsSetter {

    LocationStateViewModel st;

    private final @NonNull LiveDataTimerViewModel.TIMER t1 = LiveDataTimerViewModel.TIMER.FIVE;

    // UI Elements
    private ProgressView progressView;
    private Button buttonGetLocation;
    private ImageView buttonBack, helpButton;
    private TextView titleText;

    private LocationInformationView locationInformationView;

    @Override
    public View onCreateView(final @NonNull LayoutInflater inflater,
                             final @Nullable ViewGroup container,
                             final @Nullable Bundle savedInstanceState) {
        final @NonNull View v = inflater.inflate(R.layout.fragment_status_location, container, false);

        helpButton = v.findViewById(R.id.helpButton);
        lastChangedView = new LastChangedView(
                v.findViewById(R.id.lastChangedText),
                v.findViewById(R.id.lastChangedIndicator)
        );

        buttonGetLocation = v.findViewById(R.id.sendButton);
        progressView = new ProgressView(
                v.findViewById(R.id.pendingStatusText),
                v.findViewById(R.id.progressBar)
        );
        buttonBack = v.findViewById(R.id.moreInfoButton);
        titleText = v.findViewById(R.id.titleText);

        locationInformationView = new LocationInformationView(
                v.findViewById(R.id.lat),
                v.findViewById(R.id.lng),
                v.findViewById(R.id.acc),
                v.findViewById(R.id.bikeMarker),
                v.findViewById(R.id.locationNoData),
                v.findViewById(R.id.buttonOpenMap),
                v.findViewById(R.id.routeButton),
                v.findViewById(R.id.shareButton),
                v.findViewById(R.id.noCellTowersText),
                v.findViewById(R.id.noWifiAccessPointsText)
        );

        return v;
    }

    @Override
    protected void setupListeners(final @NonNull LifecycleOwner l) {
        /*
         Observe any changes to
         lat, lng or acc in the database
         (i.e. after the location updater has written
         its stuff in there)
         */
        st = new ViewModelProvider(this).get(LocationStateViewModel.class);

        st.getStatusLocationLat().observe(l, this::setElements);
        st.getStatusLocationLng().observe(l, this::setElements);
        st.getStatusLocationAcc().observe(l, this::setElements);
        st.getCellTowers().observe(l, this::setElements);
        st.getWifiAccessPoints().observe(l, this::setElements);
        st.getLocation().observe(l, this::setElements);
        st.getStatusNumberCellTowers().observe(l, this::setElements);
        st.getStatusNumberWifiAccessPoints().observe(l, this::setElements);

        startObservingWapp(this, l);
    }

    @Override
    protected void initUserInteractionElements() {
        /*
         Set the function of the buttons
         */
        locationInformationView.setOnClickListeners(
                this::navigateToNext, this::onRouteClick, this::shareLocation
        );
        helpButton.setOnClickListener(this::onHelpClick);

        /* Insert two new pending States to mark waiting for response */
        buttonGetLocation.setOnClickListener(v ->
                sendSms(this, Sms.MESSAGE.WAPP,
                        new State[] {
                                StateFactory.createPendingState(State.KEY.LOCATION, 0.0),
                                StateFactory.createPendingState(State.KEY.CELL_TOWERS, 0.0),
                                StateFactory.createPendingState(State.KEY.WIFI_ACCESS_POINTS, 0.0)
                        })
        );

        initTransitionButton(buttonBack, helpButton, this, false);
        titleText.setText(R.string.heading_location);
    }

    @Override
    protected void resetElements() {
        assert true;
    }

    // unset
    @Override
    public void setLocationElementsUnset() {
        progressView.setText("");
        progressView.setVisibility(false);
        lastChangedView.set(null, this);

        locationInformationView.setVisible(false);
        locationInformationView.setMarker(null, this);
    }

    @Override
    public void setButtonEnabled() {
        buttonGetLocation.setEnabled(true);
    }

    @Override
    public void setLocationElementsProgressTimeUnset() {
        tv.getResidualTime(t1).removeObservers(this);
        tv.cancelTimer(t1);
    }

    // confirmed
    @Override
    public void setLocationElementsConfirmed(final @NonNull State state) {
        progressView.setText("");
        progressView.setVisibility(false);
        lastChangedView.set(state, this);

        locationInformationView.setVisible(true);
        locationInformationView.setMarker(state, this);
    }

    @Override
    public void setLatConfirmed(final @NonNull State state) {
        locationInformationView.setLat(state);
    }

    @Override
    public void setLngConfirmed(final @NonNull State state) {
        locationInformationView.setLng(state);
    }

    @Override
    public void setAccConfirmed(final @NonNull State state) {
        locationInformationView.setAcc(state);
    }

    @Override
    public void setLocationElementsProgressTimeConfirmed() {
        tv.getResidualTime(t1).removeObservers(this);
        tv.cancelTimer(t1);
    }

    @Override
    public void setLocationElementsNumbersConfirmed(final @NonNull State state) {
        locationInformationView.setNumbers(state);
    }

    // pending
    @Override
    public void setLocationElementsPending(final @NonNull State state) {
        // BB has responded, but no response from Google Maps API yet
        final @Nullable State lastLocationState = st.getConfirmedLocationSync(state);
        progressView.setVisibility(true);

        lastChangedView.set(lastLocationState, this);

        locationInformationView.setVisible(false);
        locationInformationView.setMarker(lastLocationState, this);
    }

    @Override
    public void setLocationElementsProgressTimePending(final @NonNull State state) {
        /*
         User has clicked the update button, but no response from BB yet
         */
        long stopTime = tv.startTimer(t1, state.getTimestamp(), st.getConfirmedIntervalSync());
        tv.getResidualTime(t1).observe(this, s ->
                updatePendingText(progressView, state.getTimestamp(), stopTime, s)
        );
    }

    @Override
    public void setButtonDisabled() {
        buttonGetLocation.setEnabled(false);
    }

    @Override
    public void setLocationElementsProgressTextPending() {
        progressView.setText("Rohdaten empfangen, empfange genaue Position vom Server...");
    }

    private void navigateToNext(final @NonNull View v) {
        final @NonNull MainActivity act = (MainActivity) requireActivity();

        act.resumeToolbarAndBottomSheet();
        act.navigateTo(R.id.map_action, null);
    }

    private void shareLocation(final @NonNull View v) {
        mf.startShareIntent(this);
    }

    private void onHelpClick(final @NonNull View v) {
        Snackbar.make(v,
                R.string.text_help_location,
                Snackbar.LENGTH_LONG
        ).show();
    }

    private void onRouteClick(final @NonNull View v) {
        mf.startRouteIntent(this);
    }

}
