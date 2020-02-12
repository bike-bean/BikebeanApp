package de.bikebean.app.ui.status.battery;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModelProvider;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import de.bikebean.app.R;
import de.bikebean.app.Utils;
import de.bikebean.app.db.state.State;
import de.bikebean.app.ui.status.StateViewModel;
import de.bikebean.app.ui.status.sms.SmsViewModel;
import de.bikebean.app.ui.status.sms.send.SmsSender;
import de.bikebean.app.ui.status.status.LiveDataTimerViewModel;

public class BatteryStatusFragment extends Fragment {

    private LiveDataTimerViewModel tv;
    private StateViewModel st;

    private Context ctx;

    private SmsSender smsSender;
    
    private final int t1 = LiveDataTimerViewModel.TIMER_FOUR;

    // UI Elements
    private Button statusButton;
    private TextView statusPendingStatus;
    private TextView batteryStatusText, batteryEstimatedDaysText, batteryLastChangedText;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_status_battery, container, false);

        statusButton = v.findViewById(R.id.statusButton);
        statusPendingStatus = v.findViewById(R.id.statusPendingStatus);

        batteryStatusText = v.findViewById(R.id.batteryStatusText);
        batteryEstimatedDaysText = v.findViewById(R.id.batteryEstimatedStatusText);
        batteryLastChangedText = v.findViewById(R.id.datetimeText3);

        return v;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        st = new ViewModelProvider(this).get(StateViewModel.class);
        tv = new ViewModelProvider(this).get(LiveDataTimerViewModel.class);
        SmsViewModel sm  = new ViewModelProvider(this).get(SmsViewModel.class);

        LifecycleOwner l = getViewLifecycleOwner();
        FragmentActivity act = getActivity();
        ctx = Objects.requireNonNull(act).getApplicationContext();

        smsSender = new SmsSender(ctx, act, sm, st);

        List<State> statusStates = new ArrayList<>();
        statusStates.add(new State(State.KEY_BATTERY, 0.0));

        statusButton.setOnClickListener(v -> smsSender.send("Status", statusStates));
        st.getStatusBattery().observe(l, this::setElements);
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
                    case State.KEY_BATTERY:
                        setBatteryElementsUnset(state);
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
                    case State.KEY_BATTERY:
                        setBatteryElementsConfirmed(state);
                        break;
                }
                break;
            case State.STATUS_PENDING:
                switch (state.getKey()) {
                    case State.KEY_INTERVAL: // And
                    case State.KEY_WIFI:
                        break;
                    case State.KEY_BATTERY:
                        setBatteryElementsPending(state);
                        break;
                }
                break;
        }
    }

    // confirmed
    private void setIntervalElementsConfirmed(State state) {
        assert state != null;

        batteryEstimatedDaysText.setText(Utils.getEstimatedDaysText(st));
    }

    private void setWifiElementsConfirmed(State state) {
        assert state != null;

        batteryEstimatedDaysText.setText(Utils.getEstimatedDaysText(st));
    }

    private void setBatteryElementsConfirmed(State state) {
        tv.getResidualTime(t1).removeObservers(this);
        tv.cancelTimer(t1);

        String batteryStatus = state.getValue() + " %";
        batteryStatusText.setText(batteryStatus);
        batteryStatusText.setCompoundDrawablesWithIntrinsicBounds(
                Utils.getBatteryDrawable(ctx, state.getValue()), null, null, null
        );

        batteryLastChangedText.setText(Utils.convertToDateHuman(state.getTimestamp()));
        batteryEstimatedDaysText.setText(Utils.getEstimatedDaysText(st));

        statusButton.setEnabled(true);

        statusPendingStatus.setText("");
        statusPendingStatus.setVisibility(View.GONE);
    }

    // pending
    private void setBatteryElementsPending(State state) {
        long stopTime = tv.startTimer(t1, state.getTimestamp(), st.getConfirmedIntervalSync());
        tv.getResidualTime(t1).observe(this, s ->
                updatePendingText(statusPendingStatus, stopTime, s)
        );

        State lastBatteryState = st.getConfirmedBatterySync();
        if (lastBatteryState != null) {
            double batteryValue = lastBatteryState.getValue();
            String batteryStatus = batteryValue + " %";

            batteryLastChangedText.setText(
                    Utils.convertToDateHuman(lastBatteryState.getTimestamp()));
            batteryEstimatedDaysText.setText(
                    Utils.getEstimatedDaysText(st));
            batteryStatusText.setText(batteryStatus);
            batteryStatusText.setCompoundDrawablesWithIntrinsicBounds(
                    Utils.getBatteryDrawable(ctx, batteryValue), null, null, null
            );
        } else {
            batteryStatusText.setText("");
            batteryStatusText.setCompoundDrawablesWithIntrinsicBounds(
                    Utils.getBatteryDrawable(ctx, -1.0), null, null, null
            );

            batteryLastChangedText.setText(R.string.no_data);
            batteryEstimatedDaysText.setText(R.string.no_data);
        }

        statusButton.setEnabled(false);
        statusPendingStatus.setVisibility(View.VISIBLE);
    }

    // unset
    private void setBatteryElementsUnset(State state) {
        tv.getResidualTime(t1).removeObservers(this);
        tv.cancelTimer(t1);

        batteryStatusText.setText("");
        batteryStatusText.setCompoundDrawablesWithIntrinsicBounds(
                Utils.getBatteryDrawable(ctx, state.getValue()), null, null, null
        );

        batteryLastChangedText.setText(R.string.no_data);
        batteryEstimatedDaysText.setText(R.string.no_data);

        statusButton.setEnabled(true);

        statusPendingStatus.setText("");
        statusPendingStatus.setVisibility(View.GONE);
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
}
