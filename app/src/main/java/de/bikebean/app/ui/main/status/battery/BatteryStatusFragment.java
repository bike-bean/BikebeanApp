package de.bikebean.app.ui.main.status.battery;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModelProvider;

import de.bikebean.app.R;
import de.bikebean.app.ui.utils.Utils;
import de.bikebean.app.db.sms.Sms;
import de.bikebean.app.db.state.State;
import de.bikebean.app.ui.main.status.SubStatusFragment;
import de.bikebean.app.ui.main.status.settings.LiveDataTimerViewModel;

public class BatteryStatusFragment extends SubStatusFragment {

    private BatteryStateViewModel st;

    private final @NonNull LiveDataTimerViewModel.TIMER t1 = LiveDataTimerViewModel.TIMER.FOUR;

    // UI Elements
    private Button statusButton;
    private ImageButton helpButton;
    private TextView statusPendingStatus;
    private TextView batteryStatusText, batteryEstimatedDaysText, batteryLastChangedText;

    @Override
    public View onCreateView(final @NonNull LayoutInflater inflater,
                             final @Nullable ViewGroup container,
                             final @Nullable Bundle savedInstanceState) {
        final @NonNull View v = inflater.inflate(R.layout.fragment_status_battery, container, false);

        statusButton = v.findViewById(R.id.statusButton);
        helpButton = v.findViewById(R.id.helpButton2);
        statusPendingStatus = v.findViewById(R.id.statusPendingStatus);

        batteryStatusText = v.findViewById(R.id.batteryStatusText);
        batteryEstimatedDaysText = v.findViewById(R.id.batteryEstimatedStatusText);
        batteryLastChangedText = v.findViewById(R.id.datetimeText3);

        return v;
    }

    @Override
    protected void setupListeners(final @NonNull LifecycleOwner l) {
        st = new ViewModelProvider(this).get(BatteryStateViewModel.class);
        st.getStatusBattery().observe(l, this::setElements);
    }

    @Override
    protected void initUserInteractionElements() {
        statusButton.setOnClickListener(v ->
                sendSms(Sms.MESSAGE._STATUS, new State[]{new State(State.KEY.BATTERY, 0.0)})
        );
        helpButton.setOnClickListener(Utils::onHelpClick);
    }

    @Override
    protected void resetElements() {
        assert true;
    }

    // unset
    @Override
    protected void setBatteryElementsUnset(final @NonNull State state) {
        tv.getResidualTime(t1).removeObservers(this);
        tv.cancelTimer(t1);

        batteryStatusText.setText("");
        batteryStatusText.setCompoundDrawablesWithIntrinsicBounds(
                Utils.getBatteryDrawable(requireContext(), state.getValue()),
                null, null, null
        );

        batteryLastChangedText.setText(R.string.no_data);
        batteryEstimatedDaysText.setText(R.string.no_data);

        statusButton.setEnabled(true);

        statusPendingStatus.setText("");
        statusPendingStatus.setVisibility(View.GONE);
    }

    protected void setWarningNumberElementsUnset(final @NonNull State state) {}
    protected void setStatusElementsUnset() {}
    protected void setLocationElementsUnset() {}
    protected void setLocationElementsTempUnset() {}

    // confirmed
    @Override
    protected void setBatteryElementsConfirmed(final @NonNull State state) {
        tv.getResidualTime(t1).removeObservers(this);
        tv.cancelTimer(t1);

        final @NonNull String batteryStatus = state.getValue() + " %";
        batteryStatusText.setText(batteryStatus);
        batteryStatusText.setCompoundDrawablesWithIntrinsicBounds(
                Utils.getBatteryDrawable(requireContext(), state.getValue()),
                null, null, null
        );

        batteryLastChangedText.setText(Utils.ConvertPeriodToHuman(state.getTimestamp()));
        batteryEstimatedDaysText.setText(BatteryStateViewModel.getEstimatedDaysText(st));

        statusButton.setEnabled(true);

        statusPendingStatus.setText("");
        statusPendingStatus.setVisibility(View.GONE);
    }

    @Override
    protected void setIntervalElementsConfirmed() {
        batteryEstimatedDaysText.setText(BatteryStateViewModel.getEstimatedDaysText(st));
    }

    @Override
    protected void setWifiElementsConfirmed(final @NonNull State state) {
        batteryEstimatedDaysText.setText(
                BatteryStateViewModel.getEstimatedDaysText(st));
    }

    protected void setWarningNumberElementsConfirmed(final @NonNull State state) {}
    protected void setStatusElementsConfirmed(final @NonNull State state) {}
    protected void setLocationElementsConfirmed(final @NonNull State state) {}
    protected void setLatConfirmed(final @NonNull State state) {}
    protected void setLngConfirmed(final @NonNull State state) {}
    protected void setAccConfirmed(final @NonNull State state) {}

    // pending
    @Override
    protected void setBatteryElementsPending(final @NonNull State state) {
        long stopTime = tv.startTimer(t1, state.getTimestamp(), st.getConfirmedIntervalSync());
        tv.getResidualTime(t1).observe(this, s ->
                updatePendingText(statusPendingStatus, stopTime, s)
        );

        final @Nullable State lastBatteryState = st.getConfirmedBatterySync();
        if (lastBatteryState != null) {
            double batteryValue = lastBatteryState.getValue();
            final @NonNull String batteryStatus = batteryValue + " %";

            batteryLastChangedText.setText(
                    Utils.ConvertPeriodToHuman(lastBatteryState.getTimestamp()));
            batteryEstimatedDaysText.setText(BatteryStateViewModel.getEstimatedDaysText(st));
            batteryStatusText.setText(batteryStatus);
            batteryStatusText.setCompoundDrawablesWithIntrinsicBounds(
                    Utils.getBatteryDrawable(requireContext(), batteryValue),
                    null, null, null
            );
        } else {
            batteryStatusText.setText("");
            batteryStatusText.setCompoundDrawablesWithIntrinsicBounds(
                    Utils.getBatteryDrawable(requireContext(), -1.0),
                    null, null, null
            );

            batteryLastChangedText.setText(R.string.no_data);
            batteryEstimatedDaysText.setText(R.string.no_data);
        }

        statusButton.setEnabled(false);
        statusPendingStatus.setVisibility(View.VISIBLE);
    }

    protected void setIntervalElementsPending(final @NonNull State state) {}
    protected void setWifiElementsPending(final @NonNull State state) {}
    protected void setWarningNumberElementsPending(final @NonNull State state) {}
    protected void setLocationElementsPending(final @NonNull State state) {}
    protected void setLocationElementsTempPending(final @NonNull State state) {}
}
