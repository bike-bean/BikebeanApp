package de.bikebean.app.ui.status.status;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModelProvider;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import de.bikebean.app.MainActivity;
import de.bikebean.app.R;
import de.bikebean.app.Utils;
import de.bikebean.app.db.state.State;
import de.bikebean.app.ui.status.StateViewModel;
import de.bikebean.app.ui.status.sms.SmsViewModel;
import de.bikebean.app.ui.status.sms.send.SmsSender;

public class StatusStatusFragment extends Fragment {

    private StateViewModel st;
    private LiveDataTimerViewModel tv;
    private FragmentActivity act;

    private SmsSender smsSender;

    // UI Elements
    private Button buttonGetStatus;
    private TextView statusLastChangedText;

    private Spinner intervalDropdown;
    private TextView intervalPendingStatus, intervalSummary;

    private Switch wlanSwitch;
    private TextView wlanPendingStatus, wlanSummary;

    private TextView warningNumberPendingStatus, warningNumberSummary;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_status_status, container, false);

        wlanSwitch = v.findViewById(R.id.wlanSwitch);
        wlanSummary = v.findViewById(R.id.wlanSummary);
        wlanPendingStatus = v.findViewById(R.id.wlanPendingStatus);

        intervalDropdown = v.findViewById(R.id.intervalDropdown);
        intervalSummary = v.findViewById(R.id.intervalSummary);
        intervalPendingStatus = v.findViewById(R.id.intervalPendingStatus);

        warningNumberSummary = v.findViewById(R.id.warningNumberSummary);
        warningNumberPendingStatus = v.findViewById(R.id.warningNumberPendingStatus);

        buttonGetStatus = v.findViewById(R.id.button_get_status);
        statusLastChangedText = v.findViewById(R.id.datetimeText2);

        return v;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        st = new ViewModelProvider(this).get(StateViewModel.class);
        tv = new ViewModelProvider(this).get(LiveDataTimerViewModel.class);
        SmsViewModel sm  = new ViewModelProvider(this).get(SmsViewModel.class);

        act = Objects.requireNonNull(getActivity());
        Context ctx = Objects.requireNonNull(act).getApplicationContext();

        LifecycleOwner l = getViewLifecycleOwner();

        smsSender = new SmsSender(ctx, act, sm, st);

        initIntervalDropdown();
        setupListeners(l);
        initUserInteractionElements();
    }

    private void initIntervalDropdown() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(act,
                R.array.interval_entries, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        intervalDropdown.setAdapter(adapter);
    }

    private void setupListeners(LifecycleOwner l) {
        st.getStatusWifi().observe(l, this::setElements);
        /*
        st.getStatus().observe(l, states -> {
            for (State state : states)
                statusLastChangedText.setText(Utils.convertToDateHuman(state.getTimestamp()));
        });
        */
        st.getStatusWarningNumber().observe(l, this::setElements);

        st.getStatusInterval().observe(l, this::setElements);
        st.getIntervalAborted().observe(l, this::handleIntervalAborted);
    }


    private void initUserInteractionElements() {
        // React to user interactions
        State statusState = new State(State.KEY_STATUS, 0.0);

        buttonGetStatus.setOnClickListener(v -> sendSms("State", statusState));
        intervalDropdown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String[] items = getResources().getStringArray(R.array.interval_values);
                String newValue = items[position];

                // See if the "new" value is actually just the placeholder.
                // In that case, set the text underneath to reflect the last known status
                if (newValue.equals("0"))
                    return;

                // Get the last confirmed interval status and
                // see if the value has changed from then.
                // If it has not changed, return
                String oldValue = String.valueOf(Utils.getIntervalStatusSync(st));
                if (newValue.equals(oldValue))
                    return;

                // if it has changed, create a new pending state and fire it into the db
                Log.d(MainActivity.TAG, "Setting Interval about to be changed to " + newValue);
                String msg = "Int " + newValue;
                sendSms(msg, new State(State.KEY_INTERVAL, Double.valueOf(newValue)));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });
        wlanSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            // get the last confirmed wlan status and see if the value has changed from then
            // if it has not changed, return
            if (isChecked == Utils.getWifiStatusSync(st))
                return;

            // if it has changed, create a new pending state and fire it into the db
            Log.d(MainActivity.TAG, "Setting Wifi about to be changed to " + isChecked);
            String msg = "Wifi " + (isChecked ? "on" : "off");
            sendSms(msg, new State(State.KEY_WIFI, isChecked ? 1.0 : 0.0));
        });
    }

    /*
    * Change the Text Views, Switches etc. (UI elements)
    * according to the states from the viewModel.
    * */
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

        switch (state.getState()) {
            case State.STATUS_UNSET:
                switch (state.getKey()) {
                    case State.KEY_INTERVAL:
                        setIntervalElementsConfirmed(state);
                        break;
                    case State.KEY_WIFI:
                        setWifiElementsConfirmed(state);
                        break;
                    case State.KEY_WARNING_NUMBER:
                        setWarningNumberElementsUnset(state);
                        break;
                }
                break;
            case State.STATUS_CONFIRMED:
                switch (state.getKey()) {
                    case State.KEY_INTERVAL:
                        setIntervalElementsConfirmed(state);
                        break;
                    case State.KEY_WIFI:
                        setWifiElementsConfirmed(state);
                        break;
                    case State.KEY_WARNING_NUMBER:
                        setWarningNumberElementsConfirmed(state);
                        break;
                }
                break;
            case State.STATUS_PENDING:
                switch (state.getKey()) {
                    case State.KEY_INTERVAL:
                        setIntervalElementsPending(state);
                        break;
                    case State.KEY_WIFI:
                        setWifiElementsPending(state);
                        break;
                    case State.KEY_WARNING_NUMBER:
                        setWarningNumberElementsPending(state);
                        break;
                }
                break;
        }
    }

    private void setIntervalElementsConfirmed(State state) {
        String intervalSummaryString =
                getResources().getString(R.string.interval_summary);

        assert state != null;

        tv.getResidualTime2().removeObservers(this);
        tv.cancelTimer2();

        String oldValue = String.valueOf(Utils.getIntervalStatusSync(st));

        intervalSummary.setText(String.format(intervalSummaryString, oldValue));
        intervalPendingStatus.setText("");
    }

    private void setWifiElementsConfirmed(State state) {
        tv.getResidualTime1().removeObservers(this);
        tv.cancelTimer1();

        if (state.getValue() > 0) {
            wlanSummary.setText(R.string.wlan_summary_on);
            wlanSwitch.setChecked(true);
        } else {
            wlanSummary.setText(R.string.wlan_summary_off);
            wlanSwitch.setChecked(false);
        }

        wlanPendingStatus.setText("");
    }

    private void setWarningNumberElementsConfirmed(State state) {
        tv.getResidualTime3().removeObservers(this);
        tv.cancelTimer3();

        warningNumberSummary.setText(String.format(
                getString(R.string.warning_number_summary),
                state.getLongValue())
        );
        warningNumberPendingStatus.setText("");
    }

    private void setIntervalElementsPending(State state) {
        String intervalTransitionString =
                getResources().getString(R.string.interval_switch_transition);

        long stopTime = tv.startTimer2(state.getTimestamp(), Utils.getConfirmedIntervalSync(st));
        tv.getResidualTime2().observe(this, s ->
                updatePendingText(intervalPendingStatus, stopTime, s)
        );

        intervalSummary.setText(
                String.format(intervalTransitionString, state.getValue().intValue())
        );
    }

    private void setWifiElementsPending(State state) {
        long stopTime = tv.startTimer1(state.getTimestamp(), Utils.getConfirmedIntervalSync(st));
        tv.getResidualTime1().observe(this, s ->
                updatePendingText(wlanPendingStatus, stopTime, s)
        );

        if (state.getValue() > 0) {
            wlanSummary.setText(R.string.wifi_switch_on_transition);
            wlanSwitch.setChecked(true);
        } else {
            wlanSummary.setText(R.string.wifi_switch_off_transition);
            wlanSwitch.setChecked(false);
        }
    }

    private void setWarningNumberElementsPending(State state) {
        long stopTime = tv.startTimer3(state.getTimestamp(), Utils.getConfirmedIntervalSync(st));
        tv.getResidualTime3().observe(this, s ->
                updatePendingText(warningNumberPendingStatus, stopTime, s)
        );

        warningNumberSummary.setText(R.string.warning_number_pending_text);
    }

    private void setWarningNumberElementsUnset(State state) {
        tv.getResidualTime3().removeObservers(this);
        tv.cancelTimer3();

        sendSms("Warningnumber", new State(State.KEY_WARNING_NUMBER, 0.0));
        warningNumberSummary.setText(state.getLongValue());
        warningNumberPendingStatus.setText("");
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

    private void handleIntervalAborted(boolean b) {
        if (b) {
            intervalDropdown.setSelection(0);
            st.notifyIntervalAbort(false);
        }
    }

    private void sendSms(String message, State update) {
        smsSender.send(message, update);
    }
}

