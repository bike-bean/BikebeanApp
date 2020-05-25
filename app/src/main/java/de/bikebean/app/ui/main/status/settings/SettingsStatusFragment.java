package de.bikebean.app.ui.main.status.settings;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.snackbar.Snackbar;

import de.bikebean.app.MainActivity;
import de.bikebean.app.R;
import de.bikebean.app.ui.utils.Utils;
import de.bikebean.app.db.sms.Sms;
import de.bikebean.app.db.state.State;
import de.bikebean.app.ui.main.status.SubStatusFragment;

public class SettingsStatusFragment extends SubStatusFragment {

    private SettingsStateViewModel st;

    private final LiveDataTimerViewModel.TIMER t1 = LiveDataTimerViewModel.TIMER.ONE;
    private final LiveDataTimerViewModel.TIMER t2 = LiveDataTimerViewModel.TIMER.TWO;
    private final LiveDataTimerViewModel.TIMER t3 = LiveDataTimerViewModel.TIMER.THREE;

    // UI Elements
    private ImageButton helpButton;

    private TextView statusLastChangedText;

    private Spinner intervalDropdown;
    private TextView intervalPendingStatus, intervalSummary, nextUpdateEstimation;

    private Switch wlanSwitch;
    private TextView wlanPendingStatus, wlanSummary;

    private TextView warningNumberPendingStatus, warningNumberSummary;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_status_settings, container, false);

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
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        initIntervalDropdown();
    }

    private void initIntervalDropdown() {
        ArrayAdapter<CharSequence> adapter =
                ArrayAdapter.createFromResource(
                        requireActivity(),
                        R.array.interval_entries,
                        android.R.layout.simple_spinner_item
                );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        intervalDropdown.setAdapter(adapter);
    }

    @Override
    protected void setupListeners(LifecycleOwner l) {
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
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String newValue = getIntervalString(position);

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
                Log.d(MainActivity.TAG, "Setting Interval about to be changed to " + newValue);
                Sms.MESSAGE msg = Sms.MESSAGE.INT;
                msg.setValue("Int " + newValue);

                sendSms(msg, new State[]{new State(State.KEY.INTERVAL, Double.valueOf(newValue))});
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });
        wlanSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            // get the last confirmed wlan status and see if the value has changed from then
            // if it has not changed, return
            if (isChecked == st.getWifiStatusSync())
                return;

            // if it has changed, create a new pending state and fire it into the db
            Log.d(MainActivity.TAG, "Setting Wifi about to be changed to " + isChecked);
            Sms.MESSAGE msg = Sms.MESSAGE.WIFI;
            msg.setValue("Wifi " + (isChecked ? "on" : "off"));

            sendSms(msg, new State[]{new State(State.KEY.WIFI, isChecked ? 1.0 : 0.0)});
        });
        helpButton.setOnClickListener(this::onHelpClick);
    }

    private String getIntervalString(int position) {
        String[] items = getResources().getStringArray(R.array.interval_values);
        return items[position];
    }

    private int getIntervalPosition(int intervalValue) {
        String[] items = getResources().getStringArray(R.array.interval_values);

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
    protected void setBatteryElementsUnset(State state) {

    }

    @Override
    protected void setWarningNumberElementsUnset(State state) {
        tv.getResidualTime(t3).removeObservers(this);
        tv.cancelTimer(t3);

        sendSms(Sms.MESSAGE.WARNING_NUMBER, new State[]{new State(State.KEY.WARNING_NUMBER, 0.0)});

        warningNumberSummary.setText(state.getLongValue());
        warningNumberPendingStatus.setText("");
        warningNumberPendingStatus.setVisibility(View.GONE);
    }

    @Override
    protected void setStatusElementsUnset(State state) {
        assert state != null;

        statusLastChangedText.setText(R.string.no_data);
    }

    protected void setLocationElementsUnset() {

    }
    protected void setLocationElementsTempUnset() {

    }

    // confirmed
    protected void setBatteryElementsConfirmed(State state) {

    }

    @Override
    protected void setIntervalElementsConfirmed(State state) {
        String intervalSummaryString =
                getResources().getString(R.string.interval_summary);

        assert state != null;

        tv.getResidualTime(t2).removeObservers(this);
        tv.cancelTimer(t2);

        String oldValue = String.valueOf(st.getIntervalStatusSync());

        intervalSummary.setText(String.format(intervalSummaryString, oldValue));
        intervalPendingStatus.setText("");
        intervalPendingStatus.setVisibility(View.GONE);

        /*
        long dt = st.getIntervalLastChangeDate();
        List<Sms> ls = sm.getAllSinceDate(dt);

        List<Integer> n = new ArrayList<>();
        List<Integer> e = new ArrayList<>();

        final int I = st.getConfirmedIntervalSync() * 60; // Interval in min

        for (int i=1; i<ls.size(); i++) {
            long t = ls.get(i-1).getTimestamp() - ls.get(ls.size()-1).getTimestamp();
            double h = t / 1000.0 / 60 / 10;
            double j = h / (double) I;
            int _n = (int) Math.round(j);
            n.add(_n);
            e.add(30 + (int) (t / 1000.0 / 60 / 10) - ((_n) * I));
        }

        nextUpdateEstimation.setText("Nächstes Aufwachen ca." + Utils.convertToTime(dt) + "  " + getStringInt(n) + " " + getStringInt(e));
        */
    }

    @Override
    protected void setWifiElementsConfirmed(State state) {
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
    protected void setWarningNumberElementsConfirmed(State state) {
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
    protected void setStatusElementsConfirmed(State state) {
        statusLastChangedText.setText(Utils.convertToDateHuman(state.getTimestamp()));
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
    protected void setBatteryElementsPending(State state) {

    }

    @Override
    protected void setIntervalElementsPending(State state) {
        String intervalTransitionString =
                getResources().getString(R.string.interval_switch_transition);

        long stopTime = tv.startTimer(t2, state.getTimestamp(), st.getConfirmedIntervalSync());
        tv.getResidualTime(t2).observe(this, s ->
                updatePendingText(intervalPendingStatus, stopTime, s)
        );

        intervalSummary.setText(
                String.format(intervalTransitionString, state.getValue().intValue())
        );
        intervalPendingStatus.setVisibility(View.VISIBLE);

        /*
        long dt = st.getIntervalLastChangeDate();
        List<Sms> ls = sm.getAllSinceDate(dt);

        List<Integer> n = new ArrayList<>();
        List<Integer> e = new ArrayList<>();

        final int I = st.getConfirmedIntervalSync() * 6; // Interval in 6min

        for (int i=1; i<ls.size(); i++) {
            long t = ls.get(i-1).getTimestamp() - ls.get(ls.size()-1).getTimestamp();
            double h = t / 1000.0 / 60 / 10;
            double j = h / (double) I;
            int _n = (int) Math.round(j);
            n.add(_n);
            e.add(5 + (int) (t / 1000.0 / 60 / 10) - ((_n) * I));
        }
        */

        // List<Integer> s = new ArrayList<>();

        // CrossProduct crossProduct = new CrossProduct(new int[]{125, 5, 5, 5, 5}, e);

        /*
        for (int i=0; i<crossProduct.getMax(); i++)
            if (a + b == e.get(0) && a + c == e.get(1)
                    && a + d == e.get(2) && a + f == e.get(3))
                s.add(a);
         */

        // nextUpdateEstimation.setText("Nächstes Aufwachen ca." + Utils.convertToTime(dt) + "  " + getStringInt(n) + " " + getStringInt(e));
        nextUpdateEstimation.setVisibility(View.GONE);
    }

    /*
    class CrossProduct {

        int[] bases;
        long max;
        List<Integer> e;

        long current;
        String sCurrent;

        CrossProduct(int[] bases, List<Integer> e) {
            this.bases = bases;

            long j = 1;
            StringBuilder s = new StringBuilder();

            for (int i : bases) {
                j *= i;
                s.append("0");
            }

            this.sCurrent = s.toString();
            this.max = j;
            this.e = e;
        }

        long start() {
            this.current = 0;
            return this.current;
        }

        long getNext() {
            for (char c : sCurrent) {

            }
            return this.current + 1;
        }

        long getMax() {
            return this.max;
        }

        boolean checkConstraints(long current) {

        }

        int decode() {

        }
    }
    */

    /*
    private String getStringInt(List<Integer> l) {
        StringBuilder s = new StringBuilder();

        for (int d : l) {
            s.append(d);
            s.append("\n");
        }

        return s.toString();
    }
    */

    @Override
    protected void setWifiElementsPending(State state) {
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
    protected void setWarningNumberElementsPending(State state) {
        long stopTime = tv.startTimer(t3, state.getTimestamp(), st.getConfirmedIntervalSync());
        tv.getResidualTime(t3).observe(this, s ->
                updatePendingText(warningNumberPendingStatus, stopTime, s)
        );

        warningNumberSummary.setText(R.string.warning_number_pending_text);
        warningNumberPendingStatus.setVisibility(View.VISIBLE);
    }

    protected void setLocationElementsPending(State state) {

    }
    protected void setLocationElementsTempPending(State state) {

    }

    private void onHelpClick(View v) {
        Snackbar.make(v, R.string.help3, Snackbar.LENGTH_LONG).show();
    }
}

