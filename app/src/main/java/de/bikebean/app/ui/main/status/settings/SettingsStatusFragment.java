package de.bikebean.app.ui.main.status.settings;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SwitchCompat;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.snackbar.Snackbar;

import de.bikebean.app.R;
import de.bikebean.app.ui.utils.Utils;
import de.bikebean.app.db.sms.Sms;
import de.bikebean.app.db.state.State;
import de.bikebean.app.ui.main.status.SubStatusFragment;

public class SettingsStatusFragment extends SubStatusFragment {

    private SettingsStateViewModel st;

    private final @NonNull LiveDataTimerViewModel.TIMER t1 = LiveDataTimerViewModel.TIMER.ONE;
    private final @NonNull LiveDataTimerViewModel.TIMER t2 = LiveDataTimerViewModel.TIMER.TWO;
    private final @NonNull LiveDataTimerViewModel.TIMER t3 = LiveDataTimerViewModel.TIMER.THREE;

    // UI Elements
    private ImageButton helpButton;

    private TextView statusLastChangedText;

    private Spinner intervalDropdown;
    private TextView intervalPendingStatus, intervalSummary, nextUpdateEstimation;

    private SwitchCompat wlanSwitch;
    private TextView wlanPendingStatus, wlanSummary;

    private TextView warningNumberPendingStatus, warningNumberSummary;

    @NonNull
    @Override
    public View onCreateView(final @NonNull LayoutInflater inflater,
                             final @Nullable ViewGroup container,
                             final @Nullable Bundle savedInstanceState) {
        final @NonNull View v = inflater.inflate(R.layout.fragment_status_settings, container, false);

        helpButton = v.findViewById(R.id.helpButton3);

        wlanSwitch = v.findViewById(R.id.wlanSwitch);
        wlanSummary = v.findViewById(R.id.wlanSummary);
        wlanPendingStatus = v.findViewById(R.id.wlanPendingStatus);

        intervalDropdown = v.findViewById(R.id.intervalDropdown);
        intervalSummary = v.findViewById(R.id.intervalSummary);
        intervalPendingStatus = v.findViewById(R.id.intervalPendingStatus);
        nextUpdateEstimation = v.findViewById(R.id.nextUpdateEstimation);

        warningNumberSummary = v.findViewById(R.id.warningNumberSummary);
        warningNumberPendingStatus = v.findViewById(R.id.warningNumberPendingStatus);

        statusLastChangedText = v.findViewById(R.id.datetimeText2);

        return v;
    }

    @Override
    public void onActivityCreated(final @Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        initIntervalDropdown();
    }

    private void initIntervalDropdown() {
        final @NonNull ArrayAdapter<CharSequence> adapter =
                ArrayAdapter.createFromResource(
                        requireActivity(),
                        R.array.interval_entries,
                        android.R.layout.simple_spinner_item
                );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        intervalDropdown.setAdapter(adapter);
    }

    @Override
    protected void setupListeners(final @NonNull LifecycleOwner l) {
        st = new ViewModelProvider(this).get(SettingsStateViewModel.class);

        st.getStatusWifi().observe(l, this::setElements);
        st.getStatus().observe(l, this::setElements);
        st.getStatusWarningNumber().observe(l, this::setElements);
        st.getStatusInterval().observe(l, this::setElements);
    }

    @Override
    protected void initUserInteractionElements() {
        // React to user interactions
        intervalDropdown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(final @NonNull AdapterView<?> parent,
                                       final @NonNull View view, int position, long id) {
                final @NonNull String newValue = getIntervalString(position);

                // See if the "new" value is actually just the placeholder.
                // In that case, set the text underneath to reflect the last known status
                if (newValue.equals("0"))
                    return;

                // Get the last confirmed interval status and
                // see if the value has changed from then.
                // If it has not changed, return
                if (position == getIntervalPosition(st.getIntervalStatusSync()))
                    return;

                // if it has changed, create a new pending state and fire it into the db
                lv.d("Setting Interval about to be changed to " + newValue);
                final @NonNull Sms.MESSAGE msg = Sms.MESSAGE.INT;
                msg.setValue("Int " + newValue);

                sendSms(msg, new State[]{new State(State.KEY.INTERVAL, Double.parseDouble(newValue))});
            }

            @Override
            public void onNothingSelected(final @NonNull AdapterView<?> parent) {
                // Do nothing
            }
        });
        wlanSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            // get the last confirmed wlan status and see if the value has changed from then
            // if it has not changed, return
            if (isChecked == st.getWifiStatusSync())
                return;

            // if it has changed, create a new pending state and fire it into the db
            lv.d("Setting Wifi about to be changed to " + isChecked);
            final @NonNull Sms.MESSAGE msg = Sms.MESSAGE.WIFI;
            msg.setValue("Wifi " + (isChecked ? "on" : "off"));

            sendSms(msg, new State[]{new State(State.KEY.WIFI, isChecked ? 1.0 : 0.0)});
        });
        helpButton.setOnClickListener(this::onHelpClick);
    }

    private @NonNull String getIntervalString(int position) {
        final @NonNull String[] items = getResources().getStringArray(R.array.interval_values);
        return items[position];
    }

    private int getIntervalPosition(int intervalValue) {
        final @NonNull String[] items = getResources().getStringArray(R.array.interval_values);

        for (int i=0; i<items.length; i++)
            if (items[i].equals(String.valueOf(intervalValue)))
                return i;

        return 0;
    }

    @Override
    protected void resetElements() {
        wlanSwitch.setChecked(st.getWifiStatusSync());
        intervalDropdown.setSelection(getIntervalPosition(st.getIntervalStatusSync()));
    }

    // unset
    protected void setBatteryElementsUnset(final @NonNull State state) {}

    @Override
    protected void setWarningNumberElementsUnset(final @NonNull State state) {
        tv.getResidualTime(t3).removeObservers(this);
        tv.cancelTimer(t3);

        sendSms(Sms.MESSAGE.WARNING_NUMBER, new State[]{new State(State.KEY.WARNING_NUMBER, 0.0)});

        warningNumberSummary.setText(state.getLongValue());
        warningNumberPendingStatus.setText("");
        warningNumberPendingStatus.setVisibility(View.GONE);
    }

    @Override
    protected void setStatusElementsUnset() {
        statusLastChangedText.setText(R.string.no_data);
    }

    protected void setLocationElementsUnset() {}
    protected void setLocationElementsTempUnset() {}

    // confirmed
    protected void setBatteryElementsConfirmed(final @NonNull State state) {}

    @Override
    protected void setIntervalElementsConfirmed() {
        final @NonNull String intervalSummaryString = getString(R.string.interval_summary);

        tv.getResidualTime(t2).removeObservers(this);
        tv.cancelTimer(t2);

        final @NonNull String oldValue = String.valueOf(st.getIntervalStatusSync());

        intervalSummary.setText(String.format(intervalSummaryString, oldValue));
        intervalPendingStatus.setText("");
        intervalPendingStatus.setVisibility(View.GONE);
    }

    @Override
    protected void setWifiElementsConfirmed(final @NonNull State state) {
        tv.getResidualTime(t1).removeObservers(this);
        tv.cancelTimer(t1);

        if (state.getValue() > 0) {
            wlanSummary.setText(R.string.wlan_summary_on);
            wlanSwitch.setChecked(true);
        } else {
            wlanSummary.setText(R.string.wlan_summary_off);
            wlanSwitch.setChecked(false);
        }

        wlanPendingStatus.setText("");
        wlanPendingStatus.setVisibility(View.GONE);
    }

    @Override
    protected void setWarningNumberElementsConfirmed(final @NonNull State state) {
        tv.getResidualTime(t3).removeObservers(this);
        tv.cancelTimer(t3);

        warningNumberSummary.setText(String.format(
                getString(R.string.warning_number_summary),
                state.getLongValue())
        );
        warningNumberPendingStatus.setText("");
        warningNumberPendingStatus.setVisibility(View.GONE);
    }

    @Override
    protected void setStatusElementsConfirmed(final @NonNull State state) {
        statusLastChangedText.setText(Utils.ConvertPeriodToHuman(state.getTimestamp()));
    }

    protected void setLocationElementsConfirmed(final @NonNull State state) {}
    protected void setLatConfirmed(final @NonNull State state) {}
    protected void setLngConfirmed(final @NonNull State state) {}
    protected void setAccConfirmed(final @NonNull State state) {}

    // pending
    protected void setBatteryElementsPending(final @NonNull State state) {}

    @Override
    protected void setIntervalElementsPending(final @NonNull State state) {
        final @NonNull String intervalTransitionString = getString(R.string.interval_switch_transition);

        long stopTime = tv.startTimer(t2, state.getTimestamp(), st.getConfirmedIntervalSync());
        tv.getResidualTime(t2).observe(this, s ->
                updatePendingText(intervalPendingStatus, stopTime, s)
        );

        intervalSummary.setText(
                String.format(intervalTransitionString, state.getValue().intValue())
        );
        intervalPendingStatus.setVisibility(View.VISIBLE);

        // nextUpdateEstimation.setText("Nächstes Aufwachen ca." + Utils.convertToTime(dt) + "  " + getStringInt(n) + " " + getStringInt(e));
        nextUpdateEstimation.setVisibility(View.GONE);
    }

    @Override
    protected void setWifiElementsPending(final @NonNull State state) {
        long stopTime = tv.startTimer(t1, state.getTimestamp(), st.getConfirmedIntervalSync());
        tv.getResidualTime(t1).observe(this, s ->
                updatePendingText(wlanPendingStatus, stopTime, s)
        );

        if (state.getValue() > 0) {
            wlanSummary.setText(R.string.wifi_switch_on_transition);
            wlanSwitch.setChecked(true);
        } else {
            wlanSummary.setText(R.string.wifi_switch_off_transition);
            wlanSwitch.setChecked(false);
        }

        wlanPendingStatus.setVisibility(View.VISIBLE);
    }

    @Override
    protected void setWarningNumberElementsPending(final @NonNull State state) {
        long stopTime = tv.startTimer(t3, state.getTimestamp(), st.getConfirmedIntervalSync());
        tv.getResidualTime(t3).observe(this, s ->
                updatePendingText(warningNumberPendingStatus, stopTime, s)
        );

        warningNumberSummary.setText(R.string.warning_number_pending_text);
        warningNumberPendingStatus.setVisibility(View.VISIBLE);
    }

    protected void setLocationElementsPending(final @NonNull State state) {}
    protected void setLocationElementsTempPending(final @NonNull State state) {}

    private void onHelpClick(final @NonNull View v) {
        Snackbar.make(v, R.string.help3, Snackbar.LENGTH_LONG).show();
    }
}

