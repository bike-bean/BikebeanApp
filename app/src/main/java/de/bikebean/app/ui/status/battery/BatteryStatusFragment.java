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

import java.util.Objects;

import de.bikebean.app.R;
import de.bikebean.app.Utils;
import de.bikebean.app.db.state.State;
import de.bikebean.app.ui.status.StateViewModel;
import de.bikebean.app.ui.status.sms.SmsViewModel;
import de.bikebean.app.ui.status.sms.send.SmsSender;

public class BatteryStatusFragment extends Fragment {

    private Button buttonGetBattery;
    private TextView batteryStatusText, batteryEstimatedDaysText, batteryLastChangedText;

    private SmsSender smsSender;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_status_battery, container, false);

        buttonGetBattery = v.findViewById(R.id.button_get_battery);
        batteryStatusText = v.findViewById(R.id.batteryStatusText);
        batteryEstimatedDaysText = v.findViewById(R.id.batteryEstimatedStatusText);
        batteryLastChangedText = v.findViewById(R.id.datetimeText3);

        return v;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        StateViewModel st  = new ViewModelProvider(this).get(StateViewModel.class);
        SmsViewModel sm  = new ViewModelProvider(this).get(SmsViewModel.class);
        LifecycleOwner l = getViewLifecycleOwner();
        FragmentActivity act = getActivity();
        Context ctx = Objects.requireNonNull(act).getApplicationContext();

        smsSender = new SmsSender(ctx, act, sm, st);

        State statusState = new State(State.KEY_STATUS, 0.0);

        st.getStatusBattery().observe(l, statuses -> {
            st.getStatusBattery().getValue();
            if (statuses.size() == 0)
                return;

            State s = statuses.get(0);
            String batteryStatus = s.getValue() + " %";
            batteryStatusText.setText(batteryStatus);
            batteryStatusText.setCompoundDrawablesWithIntrinsicBounds(
                    Utils.getBatteryDrawable(ctx, s.getValue()), null, null, null
            );

            batteryEstimatedDaysText.setText(Utils.getEstimatedDaysText(st, s.getValue(), s.getTimestamp()));
            batteryLastChangedText.setText(Utils.convertToDateHuman(s.getTimestamp()));
        });

        buttonGetBattery.setOnClickListener(v -> smsSender.send("Status", statusState));
    }
}
