package de.bikebean.app.ui.main.status.battery;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModelProvider;

import de.bikebean.app.R;
import de.bikebean.app.ui.utils.Utils;
import de.bikebean.app.db.sms.Sms;
import de.bikebean.app.db.state.State;
import de.bikebean.app.ui.main.status.SubStatusFragment;
import de.bikebean.app.ui.main.status.status.LiveDataTimerViewModel;

public class BatteryStatusFragment extends SubStatusFragment {

    private BatteryStateViewModel st;

    private final LiveDataTimerViewModel.TIMER t1 = LiveDataTimerViewModel.TIMER.FOUR;

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
    protected void setupListeners(LifecycleOwner l) {
        st = new ViewModelProvider(this).get(BatteryStateViewModel.class);
        st.getStatusBattery().observe(l, this::setElements);
    }

    @Override
    protected void initUserInteractionElements() {
        statusButton.setOnClickListener(v ->
                sendSms(Sms.MESSAGE._STATUS, new State[]{new State(State.KEY.BATTERY, 0.0)})
        );
    }

    @Override
    protected void resetElements() {
        assert true;
    }

    // unset
    @Override
    protected void setBatteryElementsUnset(State state) {
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

    protected void setWarningNumberElementsUnset(State state) {

    }
    protected void setStatusElementsUnset(State state) {

    }
    protected void setLocationElementsUnset() {

    }
    protected void setLocationElementsTempUnset() {

    }

    // confirmed
    @Override
    protected void setBatteryElementsConfirmed(State state) {
        tv.getResidualTime(t1).removeObservers(this);
        tv.cancelTimer(t1);

        String batteryStatus = state.getValue() + " %";
        batteryStatusText.setText(batteryStatus);
        batteryStatusText.setCompoundDrawablesWithIntrinsicBounds(
                Utils.getBatteryDrawable(ctx, state.getValue()), null, null, null
        );

        batteryLastChangedText.setText(Utils.convertToDateHuman(state.getTimestamp()));
        batteryEstimatedDaysText.setText(BatteryStateViewModel.getEstimatedDaysText(st));

        statusButton.setEnabled(true);

        statusPendingStatus.setText("");
        statusPendingStatus.setVisibility(View.GONE);
    }

    @Override
    protected void setIntervalElementsConfirmed(State state) {
        assert state != null;

        batteryEstimatedDaysText.setText(BatteryStateViewModel.getEstimatedDaysText(st));
    }

    @Override
    protected void setWifiElementsConfirmed(State state) {
        assert state != null;

        batteryEstimatedDaysText.setText(
                BatteryStateViewModel.getEstimatedDaysText(st));
    }

    protected void setWarningNumberElementsConfirmed(State state) {

    }
    protected void setStatusElementsConfirmed(State state) {

    }
    protected void setLocationElementsConfirmed(State state) {

    }
    protected void setLatConfirmed(State state) {

    }
    protected void setLngConfirmed(State state) {

    }
    protected void setAccConfirmed(State state) {

    }

    // pending
    @Override
    protected void setBatteryElementsPending(State state) {
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
            batteryEstimatedDaysText.setText(BatteryStateViewModel.getEstimatedDaysText(st));
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

    protected void setIntervalElementsPending(State state) {

    }
    protected void setWifiElementsPending(State state) {

    }
    protected void setWarningNumberElementsPending(State state) {

    }
    protected void setLocationElementsPending(State state) {

    }
    protected void setLocationElementsTempPending(State state) {

    }
}
