package de.bikebean.app.ui.main.status.location;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TableLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import de.bikebean.app.R;
import de.bikebean.app.db.settings.settings.WappState;
import de.bikebean.app.db.type.Type;
import de.bikebean.app.ui.initialization.StateList;
import de.bikebean.app.ui.main.map.MapFragmentViewModel;
import de.bikebean.app.ui.utils.Utils;
import de.bikebean.app.db.sms.Sms;
import de.bikebean.app.db.state.State;
import de.bikebean.app.ui.main.status.SubStatusFragment;
import de.bikebean.app.ui.main.status.settings.LiveDataTimerViewModel;

public class LocationStatusFragment extends SubStatusFragment {

    private LocationStateViewModel st;

    private final LiveDataTimerViewModel.TIMER t1 = LiveDataTimerViewModel.TIMER.FIVE;

    // UI Elements
    private Button buttonGetLocation, buttonOpenMap;
    private ImageButton helpButton, shareButton;
    private ProgressBar progressBar;
    private TableLayout tableLayout;

    private TextView latText, lngText, accText;
    private TextView locationLastChangedText, locationPendingStatus, locationNoData;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_status_location, container, false);

        buttonGetLocation = v.findViewById(R.id.button_get_location);
        buttonOpenMap = v.findViewById(R.id.button_open_map);
        helpButton = v.findViewById(R.id.helpButton);
        shareButton = v.findViewById(R.id.shareButton);

        progressBar = v.findViewById(R.id.progressBar);

        tableLayout = v.findViewById(R.id.tableLayout);

        locationPendingStatus = v.findViewById(R.id.locationPendingStatusText);
        locationNoData = v.findViewById(R.id.locationNoData);
        locationLastChangedText = v.findViewById(R.id.datetimeText1);

        latText = v.findViewById(R.id.lat);
        lngText = v.findViewById(R.id.lng);
        accText = v.findViewById(R.id.acc);

        return v;
    }

    @Override
    protected void setupListeners(LifecycleOwner l) {
        // Observe any changes to
        // lat, lng or acc in the database
        // (i.e. after the location updater has written
        // its stuff in there)
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
        /*
         Set the function of the buttons
         */
        buttonOpenMap.setOnClickListener(this::navigateToNext);
        shareButton.setOnClickListener(this::shareLocation);
        helpButton.setOnClickListener(this::onHelpClick);
        /* Insert two new pending States to mark waiting for response */
        buttonGetLocation.setOnClickListener(v ->
                sendSms(Sms.MESSAGE.WAPP,
                        new State[] {
                                new State(State.KEY.CELL_TOWERS, 0.0),
                                new State(State.KEY.WIFI_ACCESS_POINTS, 0.0)
                        })
        );
    }

    @Override
    protected void resetElements() {
        assert true;
    }

    // unset
    protected void setBatteryElementsUnset(@NonNull State state) {}
    protected void setWarningNumberElementsUnset(@NonNull State state) {}
    protected void setStatusElementsUnset(@NonNull State state) {}

    @Override
    protected void setLocationElementsUnset() {
        tableLayout.setVisibility(View.GONE);
        locationNoData.setVisibility(View.VISIBLE);
        locationNoData.setText(R.string.no_data);

        buttonOpenMap.setEnabled(false);

        progressBar.setVisibility(ProgressBar.GONE);

        locationLastChangedText.setText(R.string.no_data);
    }

    @Override
    protected void setLocationElementsTempUnset() {
        tv.getResidualTime(t1).removeObservers(this);
        tv.cancelTimer(t1);

        locationPendingStatus.setText("");
        locationPendingStatus.setVisibility(View.GONE);

        buttonGetLocation.setEnabled(true);
    }

    // confirmed
    protected void setBatteryElementsConfirmed(@NonNull State state) {}
    protected void setIntervalElementsConfirmed(@NonNull State state) {}
    protected void setWifiElementsConfirmed(@NonNull State state) {}
    protected void setWarningNumberElementsConfirmed(@NonNull State state) {}
    protected void setStatusElementsConfirmed(@NonNull State state) {}

    @Override
    protected void setLocationElementsConfirmed(@NonNull State state) {
        tableLayout.setVisibility(View.VISIBLE);
        locationNoData.setVisibility(View.GONE);
        locationNoData.setText("");

        buttonOpenMap.setEnabled(true);

        progressBar.setVisibility(ProgressBar.GONE);

        locationLastChangedText.setText(Utils.ConvertPeriodToHuman(state.getTimestamp()));
    }

    @Override
    protected void setLatConfirmed(@NonNull State state) {
        latText.setText(String.format(Locale.GERMANY, "%.7f", state.getValue()));
    }

    @Override
    protected void setLngConfirmed(@NonNull State state) {
        lngText.setText(String.format(Locale.GERMANY, "%.7f", state.getValue()));
    }

    @Override
    protected void setAccConfirmed(@NonNull State state) {
        accText.setText(String.format(Locale.GERMANY, "%.1f", state.getValue()));
    }

    // pending
    protected void setBatteryElementsPending(@NonNull State state) {}
    protected void setIntervalElementsPending(@NonNull State state) {}
    protected void setWifiElementsPending(@NonNull State state) {}
    protected void setWarningNumberElementsPending(@NonNull State state) {}

    @Override
    protected void setLocationElementsPending(@NonNull State state) {
        // BB has responded, but not response from Google Maps API yet
        tv.getResidualTime(t1).removeObservers(this);
        tv.cancelTimer(t1);

        locationPendingStatus.setText("");
        locationPendingStatus.setVisibility(View.GONE);

        buttonGetLocation.setEnabled(true);

        tableLayout.setVisibility(View.GONE);
        locationNoData.setVisibility(View.GONE);
        locationNoData.setText("");

        progressBar.setVisibility(ProgressBar.VISIBLE);

        State lastLocationState = st.getConfirmedLocationSync(state);
        if (lastLocationState != null) {
            buttonOpenMap.setEnabled(true);
            locationLastChangedText.setText(
                    Utils.ConvertPeriodToHuman(lastLocationState.getTimestamp()));
        } else {
            buttonOpenMap.setEnabled(false);
            locationLastChangedText.setText(R.string.no_data);
        }
    }

    @Override
    protected void setLocationElementsTempPending(@NonNull State state) {
        /*
         User has clicked the update button, but no response from BB yet
         */
        long stopTime = tv.startTimer(t1, state.getTimestamp(), st.getConfirmedIntervalSync());
        tv.getResidualTime(t1).observe(this, s ->
                updatePendingText(locationPendingStatus, stopTime, s)
        );

        locationPendingStatus.setVisibility(View.VISIBLE);

        buttonGetLocation.setEnabled(false);
    }

    // cached copy of parsed SMS
    private final List<Integer> parsedSms = new ArrayList<>();

    private void updateWapp(List<State> states) {
        WappState wappState = new WappState(st, new StateList(states));
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

    private void updateLatLngAcc(@NonNull WappState wappState, Type type) {
        sm.markParsed(wappState.getSms());
        st.confirmWapp(wappState);
        st.insert(type);
    }

    private void navigateToNext(View v) {
        Navigation.findNavController(v).navigate(R.id.map_action);
    }

    private void shareLocation(@NonNull View v) {
        if (v.isEnabled())
            assert true;
        else
            assert true;

        new ViewModelProvider(this).get(MapFragmentViewModel.class)
                .newShareIntent(this);
    }

    private void onHelpClick(View v) {
        Snackbar.make(v, R.string.help1, Snackbar.LENGTH_LONG).show();
    }
}
