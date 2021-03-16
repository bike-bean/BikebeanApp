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

import de.bikebean.app.R;
import de.bikebean.app.db.sms.Sms;
import de.bikebean.app.db.state.State;
import de.bikebean.app.db.state.StateFactory;
import de.bikebean.app.ui.drawer.status.SubStatusFragmentSmall;

import static de.bikebean.app.ui.drawer.status.SubStatusFragmentSmallExtKt.sendSms;
import static de.bikebean.app.ui.drawer.status.location.LocationStatusFragmentSmallExtKt.startObservingWapp;

public class LocationStatusFragmentSmall extends SubStatusFragmentSmall
        implements LocationElementsSetter {

    LocationStateViewModel st;

    // UI Elements
    private Button buttonGetLocation;
    private ImageView moreInfoButton, helpButton;
    private TextView titleText;

    private LocationInformationViewSmall locationInformationViewSmall;

    @Override
    public View onCreateView(final @NonNull LayoutInflater inflater,
                             final @Nullable ViewGroup container,
                             final @Nullable Bundle savedInstanceState) {
        final @NonNull View v = inflater.inflate(R.layout.fragment_status_location_small, container, false);

        buttonGetLocation = v.findViewById(R.id.sendButton);
        moreInfoButton = v.findViewById(R.id.moreInfoButton);
        helpButton = v.findViewById(R.id.helpButton);
        titleText = v.findViewById(R.id.titleText);

        locationInformationViewSmall = new LocationInformationViewSmall(
                v.findViewById(R.id.lat),
                v.findViewById(R.id.lng),
                v.findViewById(R.id.acc),
                v.findViewById(R.id.bikeMarker),
                v.findViewById(R.id.locationNoData)
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

        startObservingWapp(this, l);
    }

    @Override
    protected void initUserInteractionElements() {
        /* Insert two new pending States to mark waiting for response */
        buttonGetLocation.setOnClickListener(v ->
                sendSms(this, Sms.MESSAGE.WAPP,
                        new State[] {
                                StateFactory.createPendingState(State.KEY.LOCATION, 0.0),
                                StateFactory.createPendingState(State.KEY.CELL_TOWERS, 0.0),
                                StateFactory.createPendingState(State.KEY.WIFI_ACCESS_POINTS, 0.0)
                        })
        );

        initTransitionButton(moreInfoButton, helpButton, this, true);
        titleText.setText(R.string.location_text);
    }

    @Override
    protected void resetElements() {
        assert true;
    }

    // unset
    @Override
    public void setLocationElementsUnset() {
        locationInformationViewSmall.setVisible(false);
        locationInformationViewSmall.setMarker(null, this);
    }

    @Override
    public void setButtonEnabled() {
        buttonGetLocation.setEnabled(true);
    }

    @Override
    public void setLocationElementsProgressTimeUnset() {}

    // confirmed
    @Override
    public void setLocationElementsConfirmed(final @NonNull State state) {
        locationInformationViewSmall.setVisible(true);
        locationInformationViewSmall.setMarker(state, this);
    }

    @Override
    public void setLatConfirmed(final @NonNull State state) {
        locationInformationViewSmall.setLat(state);
    }

    @Override
    public void setLngConfirmed(final @NonNull State state) {
        locationInformationViewSmall.setLng(state);
    }

    @Override
    public void setAccConfirmed(final @NonNull State state) {
        locationInformationViewSmall.setAcc(state);
    }

    @Override
    public void setLocationElementsProgressTimeConfirmed() {}

    @Override
    public void setLocationElementsNumbersConfirmed(@NonNull State state) {}

    // pending
    @Override
    public void setLocationElementsPending(final @NonNull State state) {
        // BB has responded, but no response from Google Maps API yet
        final @Nullable State lastLocationState = st.getConfirmedLocationSync(state);

        locationInformationViewSmall.setVisible(false);
        locationInformationViewSmall.setMarker(lastLocationState, this);
    }

    @Override
    public void setLocationElementsProgressTimePending(final @NonNull State state) {}

    @Override
    public void setButtonDisabled() {
        buttonGetLocation.setEnabled(false);
    }

    @Override
    public void setLocationElementsProgressTextPending() {}
}
