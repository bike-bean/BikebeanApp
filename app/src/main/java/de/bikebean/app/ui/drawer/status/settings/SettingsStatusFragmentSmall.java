package de.bikebean.app.ui.drawer.status.settings;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SwitchCompat;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModelProvider;

import java.util.Locale;

import de.bikebean.app.R;
import de.bikebean.app.db.sms.Sms;
import de.bikebean.app.db.state.State;
import de.bikebean.app.db.state.StateFactory;
import de.bikebean.app.ui.drawer.status.SubStatusFragmentSmall;

public class SettingsStatusFragmentSmall extends SubStatusFragmentSmall
        implements SettingsElementsSetter {

    private SettingsStateViewModel st;

    // UI Elements
    private SwitchCompat wlanSwitch;
    private TextView intervalValue, warningNumberSummary;

    private ImageView wlanImage, intervalImage, warningNumberImage;

    @NonNull
    @Override
    public View onCreateView(final @NonNull LayoutInflater inflater,
                             final @Nullable ViewGroup container,
                             final @Nullable Bundle savedInstanceState) {
        final @NonNull View v = inflater.inflate(
                R.layout.fragment_status_settings_small, container, false
        );

        wlanSwitch = v.findViewById(R.id.wlanSwitch);
        intervalValue = v.findViewById(R.id.intervalValue);
        warningNumberSummary = v.findViewById(R.id.warningNumberSummary);

        wlanImage = v.findViewById(R.id.wlanImage);
        intervalImage = v.findViewById(R.id.intervalImage);
        warningNumberImage = v.findViewById(R.id.warningNumberImage);

        return v;
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

        wlanImage.setColorFilter(getCurrentIconColorFilter());
        intervalImage.setColorFilter(getCurrentIconColorFilter());
        warningNumberImage.setColorFilter(getCurrentIconColorFilter());
    }

    @Override
    protected void resetElements() {
        wlanSwitch.setChecked(st.getWifiStatusSync());
    }

    // unset
    @Override
    public void setWarningNumberElementsUnset(final @NonNull State state) {
        sendSms(Sms.MESSAGE.WARNING_NUMBER, new State[]{StateFactory.createPendingState(
                State.KEY.WARNING_NUMBER, 0.0)
        });

        warningNumberSummary.setText(state.getLongValue());
    }

    @Override
    public void setStatusElementsUnset() {}

    // confirmed
    @Override
    public void setIntervalElementsConfirmed(final @NonNull State state) {
        final @NonNull String interval = state.getValue().intValue() + " h";
        intervalValue.setText(interval);
    }

    @Override
    public void setWifiElementsConfirmed(final @NonNull State state) {
        if (state.getValue() > 0)
            wlanSwitch.setChecked(true);
        else
            wlanSwitch.setChecked(false);
    }

    @Override
    public void setWarningNumberElementsConfirmed(final @NonNull State state) {
        warningNumberSummary.setText(state.getLongValue());
    }

    @Override
    public void setStatusElementsConfirmed(final @NonNull State state) {}

    // pending
    @Override
    public void setIntervalElementsPending(final @NonNull State state) {
        final int oldValue = st.getIntervalStatusSync();

        final @NonNull String text = String.format(
                Locale.GERMANY,
                "%d h -> %d h", oldValue, state.getValue().intValue()
        );

        intervalValue.setText(text);
    }

    @Override
    public void setWifiElementsPending(final @NonNull State state) {
        if (state.getValue() > 0)
            wlanSwitch.setChecked(true);
        else
            wlanSwitch.setChecked(false);
    }

    @Override
    public void setWarningNumberElementsPending(final @NonNull State state) {
        warningNumberSummary.setText(R.string.warning_number_pending_text);
    }
}

