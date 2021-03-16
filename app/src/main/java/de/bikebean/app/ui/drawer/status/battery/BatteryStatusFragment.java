package de.bikebean.app.ui.drawer.status.battery;

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
import de.bikebean.app.db.state.StateFactory;
import de.bikebean.app.ui.drawer.status.LastChangedView;
import de.bikebean.app.ui.drawer.status.ProgressView;
import de.bikebean.app.ui.utils.Utils;
import de.bikebean.app.db.sms.Sms;
import de.bikebean.app.db.state.State;
import de.bikebean.app.ui.drawer.status.SubStatusFragment;
import de.bikebean.app.ui.drawer.status.settings.LiveDataTimerViewModel;

import static de.bikebean.app.ui.drawer.status.SubStatusFragmentSmallExtKt.sendSms;

public class BatteryStatusFragment extends SubStatusFragment implements BatteryElementsSetter {

    private BatteryStateViewModel st;

    private final @NonNull LiveDataTimerViewModel.TIMER t1 = LiveDataTimerViewModel.TIMER.FOUR;

    // UI Elements
    private Button statusButton;
    private ProgressView progressView;
    private BatteryView batteryView;
    private ImageView buttonBack, helpButton;
    private TextView titleText;

    @Override
    public View onCreateView(final @NonNull LayoutInflater inflater,
                             final @Nullable ViewGroup container,
                             final @Nullable Bundle savedInstanceState) {
        final @NonNull View v = inflater.inflate(R.layout.fragment_status_battery, container, false);

        helpButton = v.findViewById(R.id.helpButton);
        lastChangedView = new LastChangedView(
                v.findViewById(R.id.lastChangedText),
                v.findViewById(R.id.lastChangedIndicator)
        );

        statusButton = v.findViewById(R.id.sendButton);
        progressView = new ProgressView(
                v.findViewById(R.id.pendingStatusText),
                v.findViewById(R.id.progressBar)
        );
        buttonBack = v.findViewById(R.id.moreInfoButton);
        titleText = v.findViewById(R.id.titleText);

        batteryView = new BatteryView(
                v.findViewById(R.id.batteryStatusText),
                v.findViewById(R.id.batteryStatusImage),
                v.findViewById(R.id.batteryEstimatedStatusText),
                v.findViewById(R.id.batteryRuntimeEstimationText),
                v.findViewById(R.id.batteryLastKnownStatusText)
        );

        return v;
    }

    @Override
    protected void setupListeners(final @NonNull LifecycleOwner l) {
        st = new ViewModelProvider(this).get(BatteryStateViewModel.class);
        st.getStatusBattery().observe(l, this::setElements);
        st.getStatusInterval().observe(l, this::setElements);
        st.getStatusWifi().observe(l, this::setElements);
        st.getCellTowers().observe(l, this::setElements);
    }

    @Override
    protected void initUserInteractionElements() {
        statusButton.setOnClickListener(v ->
                sendSms(this, Sms.MESSAGE._STATUS, new State[]{
                        StateFactory.createPendingState(State.KEY.BATTERY, 0.0)
                })
        );
        helpButton.setOnClickListener(Utils::onHelpClick);

        initTransitionButton(buttonBack, helpButton, this, false);
        titleText.setText(R.string.battery_text);
    }

    @Override
    protected void resetElements() {
        assert true;
    }

    // unset
    @Override
    public void setBatteryElementsUnset(final @NonNull State state) {
        tv.getResidualTime(t1).removeObservers(this);
        tv.cancelTimer(t1);

        lastChangedView.set(null, this);
        progressView.setVisibility(false);

        batteryView.setStatus(requireContext(), st);
        batteryView.setEstimationText(R.string.no_data);
    }

    // confirmed
    @Override
    public void setBatteryElementsConfirmed(final @NonNull State state) {
        tv.getResidualTime(t1).removeObservers(this);
        tv.cancelTimer(t1);

        lastChangedView.set(state, this);
        progressView.setVisibility(false);

        batteryView.setStatus(requireContext(), st);
    }

    @Override
    public void setIntervalElementsConfirmed() {
        batteryView.setStatus(requireContext(), st);
    }

    @Override
    public void setWifiElementsConfirmed(final @NonNull State state) {
        batteryView.setStatus(requireContext(), st);
    }

    @Override
    public void setButtonEnabled() {
        if (!force)
            statusButton.setEnabled(true);
    }

    @Override
    public void setButtonDisabled() {
        if (!force)
            statusButton.setEnabled(false);
    }

    private boolean force = false;

    @Override
    public void setButtonForceEnabled() {
        force = false;
        statusButton.setEnabled(true);
    }

    @Override
    public void setButtonForceDisabled() {
        force = true;
        statusButton.setEnabled(false);
    }

    // pending
    @Override
    public void setBatteryElementsPending(final @NonNull State state) {
        long stopTime = tv.startTimer(t1, state.getTimestamp(), st.getConfirmedIntervalSync());
        tv.getResidualTime(t1).observe(this, s ->
                updatePendingText(progressView, state.getTimestamp(), stopTime, s)
        );

        final @Nullable State lastBatteryState = st.getConfirmedBatterySync();

        lastChangedView.set(lastBatteryState, this);
        progressView.setVisibility(true);

        batteryView.setStatus(requireContext(), st);
    }
}
