package de.bikebean.app.ui.drawer.status.settings;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SwitchCompat;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.snackbar.Snackbar;

import de.bikebean.app.R;
import de.bikebean.app.db.state.StateFactory;
import de.bikebean.app.db.sms.Sms;
import de.bikebean.app.db.state.State;
import de.bikebean.app.ui.drawer.status.LastChangedView;
import de.bikebean.app.ui.drawer.status.ProgressView;
import de.bikebean.app.ui.drawer.status.SubStatusFragment;

public class SettingsStatusFragment extends SubStatusFragment implements SettingsElementsSetter {

    private SettingsStateViewModel st;

    private final @NonNull LiveDataTimerViewModel.TIMER t1 = LiveDataTimerViewModel.TIMER.ONE;
    private final @NonNull LiveDataTimerViewModel.TIMER t2 = LiveDataTimerViewModel.TIMER.TWO;
    private final @NonNull LiveDataTimerViewModel.TIMER t3 = LiveDataTimerViewModel.TIMER.THREE;

    // UI Elements
    private ImageButton helpButton;

    private Spinner intervalDropdown;
    private TextView intervalSummary, nextUpdateEstimation;
    private ProgressView intervalProgressView;

    private SwitchCompat wlanSwitch;
    private TextView wlanSummary;
    private ProgressView wlanProgressView;

    private TextView warningNumberSummary;
    private ProgressView warningNumberProgressView;

    private ImageView wlanImage, intervalImage, warningNumberImage;

    @NonNull
    @Override
    public View onCreateView(final @NonNull LayoutInflater inflater,
                             final @Nullable ViewGroup container,
                             final @Nullable Bundle savedInstanceState) {
        final @NonNull View v = inflater.inflate(R.layout.fragment_status_settings, container, false);

        helpButton = v.findViewById(R.id.helpButton);

        lastChangedView = new LastChangedView(
                v.findViewById(R.id.lastChangedText),
                v.findViewById(R.id.lastChangedIndicator)
        );

        ProgressBar progressBar = v.findViewById(R.id.progressBar);

        wlanSwitch = v.findViewById(R.id.wlanSwitch);
        wlanSummary = v.findViewById(R.id.wlanSummary);
        wlanProgressView = new ProgressView(
                v.findViewById(R.id.wlanPendingStatus), progressBar
        );

        intervalDropdown = v.findViewById(R.id.intervalDropdown);
        intervalSummary = v.findViewById(R.id.intervalSummary);
        intervalProgressView = new ProgressView(
                v.findViewById(R.id.intervalPendingStatus), progressBar
        );
        nextUpdateEstimation = v.findViewById(R.id.nextUpdateEstimation);

        warningNumberSummary = v.findViewById(R.id.warningNumberSummary);
        warningNumberProgressView = new ProgressView(
                v.findViewById(R.id.warningNumberPendingStatus), progressBar
        );

        wlanImage = v.findViewById(R.id.wlanImage);
        intervalImage = v.findViewById(R.id.intervalImage);
        warningNumberImage = v.findViewById(R.id.warningNumberImage);

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

                sendSms(msg, new State[]{StateFactory.createPendingState(
                                State.KEY.INTERVAL,
                                Double.parseDouble(newValue))
                });
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

            sendSms(msg, new State[]{StateFactory.createPendingState(
                    State.KEY.WIFI, isChecked ? 1.0 : 0.0)
            });
        });
        helpButton.setOnClickListener(this::onHelpClick);

        wlanImage.setColorFilter(getCurrentIconColorFilter());
        intervalImage.setColorFilter(getCurrentIconColorFilter());
        warningNumberImage.setColorFilter(getCurrentIconColorFilter());
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
    @Override
    public void setWarningNumberElementsUnset(final @NonNull State state) {
        tv.getResidualTime(t3).removeObservers(this);
        tv.cancelTimer(t3);

        sendSms(Sms.MESSAGE.WARNING_NUMBER, new State[]{StateFactory.createPendingState(
                State.KEY.WARNING_NUMBER, 0.0)
        });

        warningNumberSummary.setText(state.getLongValue());
        warningNumberProgressView.setVisibility(false);
    }

    @Override
    public void setStatusElementsUnset() {
        lastChangedView.set(null, this);
    }

    // confirmed
    @Override
    public void setIntervalElementsConfirmed(final @NonNull State state) {
        final @NonNull String intervalSummaryString = getString(R.string.interval_summary);

        tv.getResidualTime(t2).removeObservers(this);
        tv.cancelTimer(t2);

        final @NonNull String oldValue = String.valueOf(state.getValue().intValue());

        intervalSummary.setText(String.format(intervalSummaryString, oldValue));
        intervalProgressView.setVisibility(false);
    }

    @Override
    public void setWifiElementsConfirmed(final @NonNull State state) {
        tv.getResidualTime(t1).removeObservers(this);
        tv.cancelTimer(t1);

        if (state.getValue() > 0) {
            wlanSummary.setText(R.string.wlan_summary_on);
            wlanSwitch.setChecked(true);
        } else {
            wlanSummary.setText(R.string.wlan_summary_off);
            wlanSwitch.setChecked(false);
        }

        wlanProgressView.setVisibility(false);
    }

    @Override
    public void setWarningNumberElementsConfirmed(final @NonNull State state) {
        tv.getResidualTime(t3).removeObservers(this);
        tv.cancelTimer(t3);

        warningNumberSummary.setText(String.format(
                getString(R.string.warning_number_summary),
                state.getLongValue())
        );
        warningNumberProgressView.setVisibility(false);
    }

    @Override
    public void setStatusElementsConfirmed(final @NonNull State state) {
        lastChangedView.set(state, this);
    }

    // pending
    @Override
    public void setIntervalElementsPending(final @NonNull State state) {
        final @NonNull String intervalTransitionString = getString(R.string.interval_switch_transition);

        long stopTime = tv.startTimer(t2, state.getTimestamp(), st.getConfirmedIntervalSync());
        tv.getResidualTime(t2).observe(this, s ->
                updatePendingText(intervalProgressView, state.getTimestamp(), stopTime, s)
        );

        intervalSummary.setText(
                String.format(intervalTransitionString, state.getValue().intValue())
        );
        intervalProgressView.setVisibility(true);

        // nextUpdateEstimation.setText("Nächstes Aufwachen ca." + Utils.convertToTime(dt) + "  " + getStringInt(n) + " " + getStringInt(e));
        nextUpdateEstimation.setVisibility(View.GONE);
    }

    @Override
    public void setWifiElementsPending(final @NonNull State state) {
        long stopTime = tv.startTimer(t1, state.getTimestamp(), st.getConfirmedIntervalSync());
        tv.getResidualTime(t1).observe(this, s ->
                updatePendingText(wlanProgressView, state.getTimestamp(), stopTime, s)
        );

        if (state.getValue() > 0) {
            wlanSummary.setText(R.string.wifi_switch_on_transition);
            wlanSwitch.setChecked(true);
        } else {
            wlanSummary.setText(R.string.wifi_switch_off_transition);
            wlanSwitch.setChecked(false);
        }

        wlanProgressView.setVisibility(true);
    }

    @Override
    public void setWarningNumberElementsPending(final @NonNull State state) {
        long stopTime = tv.startTimer(t3, state.getTimestamp(), st.getConfirmedIntervalSync());
        tv.getResidualTime(t3).observe(this, s ->
                updatePendingText(warningNumberProgressView, state.getTimestamp(), stopTime, s)
        );

        warningNumberSummary.setText(R.string.warning_number_pending_text);
        warningNumberProgressView.setVisibility(true);
    }

    private void onHelpClick(final @NonNull View v) {
        Snackbar.make(
                v,
                R.string.help3,
                Snackbar.LENGTH_LONG
        ).show();
    }
}

