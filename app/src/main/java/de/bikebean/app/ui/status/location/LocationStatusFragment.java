package de.bikebean.app.ui.status.location;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TableLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import de.bikebean.app.R;
import de.bikebean.app.Utils;
import de.bikebean.app.db.state.State;
import de.bikebean.app.ui.status.sms.SmsViewModel;
import de.bikebean.app.ui.status.sms.send.SmsSender;
import de.bikebean.app.ui.status.status.LiveDataTimerViewModel;

public class LocationStatusFragment extends Fragment {

    private LiveDataTimerViewModel tv;
    private LocationStateViewModel st;
    private SmsViewModel sm;

    private Context ctx;

    private SmsSender smsSender;

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
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        st = new ViewModelProvider(this).get(LocationStateViewModel.class);
        tv = new ViewModelProvider(this).get(LiveDataTimerViewModel.class);
        sm = new ViewModelProvider(this).get(SmsViewModel.class);

        LifecycleOwner l = getViewLifecycleOwner();
        FragmentActivity act = getActivity();
        ctx = Objects.requireNonNull(act).getApplicationContext();

        smsSender = new SmsSender(ctx, act, sm, st);

        setupListeners(l);
        initUserInteractionElements();
    }

    private void setupListeners(LifecycleOwner l) {
        // Observe any changes to
        // lat, lng or acc in the database
        // (i.e. after the location updater has written
        // its stuff in there)
        st.getStatusLocationLat().observe(l, this::setElements);
        st.getStatusLocationLng().observe(l, this::setElements);
        st.getStatusLocationAcc().observe(l, this::setElements);
        st.getCellTowers().observe(l, this::setElements);
        st.getWifiAccessPoints().observe(l, this::setElements);
        st.getLocation().observe(l, this::setElements);
        st.getWapp().observe(l, this::updateWapp);
    }

    private void initUserInteractionElements() {
        List<State> locationStates = new ArrayList<>();
        locationStates.add(new State(State.KEY.CELL_TOWERS, 0.0));
        locationStates.add(new State(State.KEY.WIFI_ACCESS_POINTS, 0.0));

        // Buttons
        buttonGetLocation.setOnClickListener(v -> smsSender.send("Wapp", locationStates));
        buttonOpenMap.setOnClickListener(this::navigateToNext);
    }

    // Cached copy of parsed sms
    private final List<Integer> parsedSms = new ArrayList<>();

    private void setElements(List<State> states) {
        if (states.size() == 0)
            return;

        State state = states.get(0);

        int id = state.id;

        if (parsedSms.contains(id))
            return;

        parsedSms.add(id);

        State.KEY key = State.KEY.getValue(state.getKey());
        switch (State.STATUS.values()[state.getState()]) {
            case UNSET:
                switch (key) {
                    case LAT: // And
                    case LNG: // And
                    case ACC: // And
                        break;
                    case LOCATION:
                        setLocationElementsUnset();
                        break;
                    case CELL_TOWERS: // And
                    case WIFI_ACCESS_POINTS:
                        setLocationElementsTempUnset();
                        break;
                }
                break;
            case PENDING:
                switch (key) {
                    case LAT: // And
                    case LNG: // And
                    case ACC: // And
                        break;
                    case LOCATION:
                        setLocationElementsPending(state);
                        break;
                    case CELL_TOWERS: // And
                    case WIFI_ACCESS_POINTS:
                        setLocationElementsTempPending(state);
                        break;
                }
                break;
            case CONFIRMED:
                switch (key) {
                    case LAT:
                        setLocationTableElementsConfirmed(state, latText, 7);
                        break;
                    case LNG:
                        setLocationTableElementsConfirmed(state, lngText, 7);
                        break;
                    case ACC:
                        setLocationTableElementsConfirmed(state, accText, 1);
                        break;
                    case LOCATION:
                        setLocationElementsConfirmed(state);
                    case CELL_TOWERS: // And
                    case WIFI_ACCESS_POINTS:
                        break;
                }
                break;
        }
    }

    // confirmed
    private void setLocationElementsConfirmed(State state) {
        tableLayout.setVisibility(View.VISIBLE);
        locationNoData.setVisibility(View.GONE);
        locationNoData.setText("");

        buttonOpenMap.setEnabled(true);

        progressBar.setVisibility(ProgressBar.GONE);

        locationLastChangedText.setText(Utils.convertToDateHuman(state.getTimestamp()));
    }

    private void setLocationTableElementsConfirmed(State state, TextView textView, int precision) {
        final String formatStr = "%." + precision + "f";
        textView.setText(String.format(Locale.GERMANY, formatStr, state.getValue()));
    }

    // pending
    private void setLocationElementsPending(State state) {
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

    private void setLocationElementsTempPending(State state) {
        // User has clicked the update button, but no response from BB yet
        long stopTime = tv.startTimer(t1, state.getTimestamp(), st.getConfirmedIntervalSync());
        tv.getResidualTime(t1).observe(this, s ->
                updatePendingText(locationPendingStatus, stopTime, s)
        );

        locationPendingStatus.setVisibility(View.VISIBLE);

        buttonGetLocation.setEnabled(false);
    }

    // unset
    private void setLocationElementsUnset() {
        tableLayout.setVisibility(View.GONE);
        locationNoData.setVisibility(View.VISIBLE);
        locationNoData.setText(R.string.no_data);

        buttonOpenMap.setEnabled(false);

        progressBar.setVisibility(ProgressBar.GONE);

        locationLastChangedText.setText(R.string.no_data);
    }

    private void setLocationElementsTempUnset() {
        tv.getResidualTime(t1).removeObservers(this);
        tv.cancelTimer(t1);

        locationPendingStatus.setText("");
        locationPendingStatus.setVisibility(View.GONE);

        buttonGetLocation.setEnabled(true);
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

            st.confirmWapp(wappCellTowers, wappWifiAccessPoints);

            new LocationUpdater(ctx, st, sm, wappCellTowers.getSmsId())
                    .execute(wifiAccessPointsState.getLongValue(), cellTowersState.getLongValue());

            if (lastWifiAccessPointsState == null || lastCellTowersState == null) {
                st.insert(new State(State.KEY.LOCATION, 0.0, wappCellTowers.getTimestamp()));
                return;
            }

            if (lastWifiAccessPointsState.id == wifiAccessPointsState.id
                    && lastCellTowersState.id == cellTowersState.id)
                st.insert(new State(State.KEY.LOCATION, 0.0, wappCellTowers.getTimestamp()));
        }
    }

    private void updatePendingText(TextView textView, long stopTime, long residualSeconds) {
        if (residualSeconds < 0) {
            textView.setText(getResources().getString(R.string.overdue,
                    Utils.convertToDateHuman(stopTime))
            );
            return;
        }

        int hours = (int) residualSeconds / 60 / 60;
        int minutes = (int) (residualSeconds / 60 ) - (hours * 60);

        String hoursPadded = (hours < 10) ? "0" + hours : String.valueOf(hours);
        String minutesPadded = (minutes < 10) ? "0" + minutes : String.valueOf(minutes);

        textView.setText(getResources().getString(R.string.pending_text,
                hoursPadded + ":" + minutesPadded,
                Utils.convertToTime(stopTime))
        );
    }

    private void navigateToNext(View v) {
        Navigation.findNavController(v).navigate(R.id.map_action);
    }
}
