package de.bikebean.app.ui.main.status.location;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TableLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import java.util.List;
import java.util.Locale;

import de.bikebean.app.R;
import de.bikebean.app.ui.utils.Utils;
import de.bikebean.app.db.sms.Sms;
import de.bikebean.app.db.state.State;
import de.bikebean.app.ui.main.status.SubStatusFragment;
import de.bikebean.app.ui.main.status.status.LiveDataTimerViewModel;

public class LocationStatusFragment extends SubStatusFragment {

    private LocationStateViewModel st;

    private final LiveDataTimerViewModel.TIMER t1 = LiveDataTimerViewModel.TIMER.FIVE;

    // UI Elements
    private Button buttonGetLocation, buttonOpenMap;
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
        State[] locationStates = {
                new State(State.KEY.CELL_TOWERS, 0.0),
                new State(State.KEY.WIFI_ACCESS_POINTS, 0.0)
        };

        // Buttons
        buttonGetLocation.setOnClickListener(v -> sendSms(Sms.MESSAGE.WAPP, locationStates));
        buttonOpenMap.setOnClickListener(this::navigateToNext);
    }

    @Override
    protected void resetElements() {
        assert true;
    }

    // unset
    protected void setBatteryElementsUnset(State state) {

    }
    protected void setWarningNumberElementsUnset(State state) {

    }
    protected void setStatusElementsUnset(State state) {

    }

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
    protected void setBatteryElementsConfirmed(State state) {

    }
    protected void setIntervalElementsConfirmed(State state) {

    }
    protected void setWifiElementsConfirmed(State state) {

    }
    protected void setWarningNumberElementsConfirmed(State state) {

    }
    protected void setStatusElementsConfirmed(State state) {

    }

    @Override
    protected void setLocationElementsConfirmed(State state) {
        tableLayout.setVisibility(View.VISIBLE);
        locationNoData.setVisibility(View.GONE);
        locationNoData.setText("");

        buttonOpenMap.setEnabled(true);

        progressBar.setVisibility(ProgressBar.GONE);

        locationLastChangedText.setText(Utils.convertToDateHuman(state.getTimestamp()));
    }

    @Override
    protected void setLatConfirmed(State state) {
        latText.setText(String.format(Locale.GERMANY, "%.7f", state.getValue()));
    }

    @Override
    protected void setLngConfirmed(State state) {
        lngText.setText(String.format(Locale.GERMANY, "%.7f", state.getValue()));
    }

    @Override
    protected void setAccConfirmed(State state) {
        accText.setText(String.format(Locale.GERMANY, "%.1f", state.getValue()));
    }

    // pending
    protected void setBatteryElementsPending(State state) {

    }
    protected void setIntervalElementsPending(State state) {

    }
    protected void setWifiElementsPending(State state) {

    }
    protected void setWarningNumberElementsPending(State state) {

    }

    @Override
    protected void setLocationElementsPending(State state) {
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
                    Utils.convertToDateHuman(lastLocationState.getTimestamp()));
        } else {
            buttonOpenMap.setEnabled(false);
            locationLastChangedText.setText(R.string.no_data);
        }
    }

    @Override
    protected void setLocationElementsTempPending(State state) {
        // User has clicked the update button, but no response from BB yet
        long stopTime = tv.startTimer(t1, state.getTimestamp(), st.getConfirmedIntervalSync());
        tv.getResidualTime(t1).observe(this, s ->
                updatePendingText(locationPendingStatus, stopTime, s)
        );

        locationPendingStatus.setVisibility(View.VISIBLE);

        buttonGetLocation.setEnabled(false);
    }

    private void updateWapp(List<State> states) {
        if (states.size() == 0)
            return;

        State wappWifiAccessPoints = null;
        State wappCellTowers = null;

        if (states.size() > 1) {
            for (State s : states)
                if (s.getValue() == State.WAPP_CELL_TOWERS) {
                    wappCellTowers = s;
                    break;
                }
            for (State s : states)
                if (s.getValue() == State.WAPP_WIFI_ACCESS_POINTS) {
                    wappWifiAccessPoints = s;
                    break;
                }
        }

        if (wappCellTowers != null && wappWifiAccessPoints != null) {
            State wifiAccessPointsState =
                    st.getWifiAccessPointsBySmsIdSync(wappWifiAccessPoints.getSmsId());
            State cellTowersState =
                    st.getCellTowersBySmsIdSync(wappCellTowers.getSmsId());
            State lastWifiAccessPointsState =
                    st.getConfirmedLocationSync(wifiAccessPointsState);
            State lastCellTowersState =
                    st.getConfirmedLocationSync(cellTowersState);

            new LocationUpdater(ctx, st,
                    wappCellTowers.getSmsId(),
                    this::updateLatLngAcc,
                    this::updateNumbers,
                    wappCellTowers,
                    wappWifiAccessPoints
            ).execute(wifiAccessPointsState.getLongValue(), cellTowersState.getLongValue());

            if (lastWifiAccessPointsState == null || lastCellTowersState == null) {
                st.insert(new State(State.KEY.LOCATION, 0.0, wappCellTowers.getTimestamp()));
                return;
            }

            if (lastWifiAccessPointsState.id == wifiAccessPointsState.id
                    && lastCellTowersState.id == cellTowersState.id)
                st.insert(new State(State.KEY.LOCATION, 0.0, wappCellTowers.getTimestamp()));
        }
    }

    private void updateNumbers(int numberCellTowers, int numberWifiAccessPoints, int smsId) {
        Sms sms = sm.getSmsByIdSync(smsId);

        st.insert(new State(
                sms.getTimestamp(), State.KEY.NO_WIFI_ACCESS_POINTS,
                (double) numberWifiAccessPoints, "",
                State.STATUS.CONFIRMED, sms.getId())
        );

        st.insert(new State(
                sms.getTimestamp(), State.KEY.NO_CELL_TOWERS, (double) numberCellTowers,
                "", State.STATUS.CONFIRMED, sms.getId())
        );
    }

    private void updateLatLngAcc(double lat, double lng, double acc, int smsId,
                                 State finalWappCellTowers, State finalWappWifiAccessPoints) {
        Sms sms = sm.getSmsByIdSync(smsId);

        sm.markParsed(smsId);
        st.confirmWapp(finalWappCellTowers, finalWappWifiAccessPoints);

        st.insert(new State(
                sms.getTimestamp() + 1, State.KEY.LAT, lat,
                "", State.STATUS.CONFIRMED, sms.getId())
        );
        st.insert(new State(
                sms.getTimestamp() + 1, State.KEY.LNG, lng,
                "", State.STATUS.CONFIRMED, sms.getId())
        );
        st.insert(new State(
                sms.getTimestamp() + 1, State.KEY.ACC, acc,
                "", State.STATUS.CONFIRMED, sms.getId())
        );
        st.insert(new State(
                sms.getTimestamp() + 1, State.KEY.LOCATION, 0.0,
                "", State.STATUS.CONFIRMED, sms.getId())
        );
    }

    private void navigateToNext(View v) {
        Navigation.findNavController(v).navigate(R.id.map_action);
    }
}
