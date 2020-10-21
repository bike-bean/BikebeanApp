package de.bikebean.app.ui.drawer.status.location;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModelProvider;

import java.util.ArrayList;
import java.util.List;

import de.bikebean.app.MainActivity;
import de.bikebean.app.R;
import de.bikebean.app.db.settings.settings.WappState;
import de.bikebean.app.db.sms.Sms;
import de.bikebean.app.db.state.State;
import de.bikebean.app.db.state.StateFactory;
import de.bikebean.app.db.type.SmsType;
import de.bikebean.app.ui.drawer.status.SubStatusFragmentSmall;
import de.bikebean.app.ui.initialization.StateList;

public class LocationStatusFragmentSmall extends SubStatusFragmentSmall
        implements LocationElementsSetter {

    private LocationStateViewModel st;

    // UI Elements
    private Button buttonGetLocation;
    private TextView moreInfoButton;

    private LocationInformationViewSmall locationInformationViewSmall;

    @Override
    public View onCreateView(final @NonNull LayoutInflater inflater,
                             final @Nullable ViewGroup container,
                             final @Nullable Bundle savedInstanceState) {
        final @NonNull View v = inflater.inflate(R.layout.fragment_status_location_small, container, false);

        buttonGetLocation = v.findViewById(R.id.sendButton);
        moreInfoButton = v.findViewById(R.id.moreInfoButton0);

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
        st.getWapp().observe(l, this::updateWapp);
    }

    @Override
    protected void initUserInteractionElements() {
        /* Insert two new pending States to mark waiting for response */
        buttonGetLocation.setOnClickListener(v ->
                sendSms(Sms.MESSAGE.WAPP,
                        new State[] {
                                StateFactory.createPendingState(State.KEY.LOCATION, 0.0),
                                StateFactory.createPendingState(State.KEY.CELL_TOWERS, 0.0),
                                StateFactory.createPendingState(State.KEY.WIFI_ACCESS_POINTS, 0.0)
                        })
        );
        moreInfoButton.setOnClickListener(v ->
                ((MainActivity) requireActivity()).transitionSmallNormal(v)
        );
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

    // cached copy of parsed SMS
    private final List<Integer> parsedSms = new ArrayList<>();

    private void updateWapp(final List<State> states) {
        final @NonNull WappState wappState = new WappState(st, new StateList(states));
        if (wappState.getIsNull())
            return;

        int id1 = wappState.getCellTowers().id;
        int id2 = wappState.getWifiAccessPoints().id;

        if (parsedSms.contains(id1) || parsedSms.contains(id2))
            return;

        parsedSms.add(id1);
        parsedSms.add(id2);

        new LocationUpdater(
                requireContext(), st, lv,
                this::updateLatLngAcc, wappState
        ).execute();
    }

    private void updateLatLngAcc(final @NonNull WappState wappState, final @NonNull SmsType smsType) {
        sm.markParsed(wappState.getSms());
        st.confirmWapp(wappState);
        st.insert(smsType);
    }
}
