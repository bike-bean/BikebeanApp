package de.bikebean.app.ui.drawer.status.battery;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModelProvider;

import de.bikebean.app.MainActivity;
import de.bikebean.app.R;
import de.bikebean.app.db.sms.Sms;
import de.bikebean.app.db.state.State;
import de.bikebean.app.db.state.StateFactory;
import de.bikebean.app.ui.drawer.status.SubStatusFragmentSmall;

public class BatteryStatusFragmentSmall extends SubStatusFragmentSmall
        implements BatteryElementsSetter {

    private BatteryStateViewModel st;

    // UI Elements
    private Button statusButton;
    private BatteryViewSmall batteryView;

    @Override
    public View onCreateView(final @NonNull LayoutInflater inflater,
                             final @Nullable ViewGroup container,
                             final @Nullable Bundle savedInstanceState) {
        final @NonNull View v = inflater.inflate(
                R.layout.fragment_status_battery_small, container, false
        );

        statusButton = v.findViewById(R.id.sendButton);

        batteryView = new BatteryViewSmall(
                v.findViewById(R.id.batteryStatusText),
                v.findViewById(R.id.batteryStatusImage)
        );

        v.findViewById(R.id.moreInfoButton1).setOnClickListener(view ->
                ((MainActivity) requireActivity()).transitionSmallNormal(view)
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
                sendSms(Sms.MESSAGE._STATUS, new State[]{
                        StateFactory.createPendingState(State.KEY.BATTERY, 0.0)
                })
        );
    }

    @Override
    protected void resetElements() {
        assert true;
    }

    // unset
    @Override
    public void setBatteryElementsUnset(final @NonNull State state) {
        batteryView.setStatus(requireContext(), st);
    }

    // confirmed
    @Override
    public void setBatteryElementsConfirmed(final @NonNull State state) {
        batteryView.setStatus(requireContext(), st);
    }

    @Override
    public void setIntervalElementsConfirmed() {
        batteryView.setStatus(requireContext(), st);
    }

    @Override
    public void setWifiElementsConfirmed(@NonNull State state) {
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
        batteryView.setStatus(requireContext(), st);
    }

}
