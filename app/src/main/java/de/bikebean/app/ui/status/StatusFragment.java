package de.bikebean.app.ui.status;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.preference.PreferenceManager;

import java.util.Objects;

import de.bikebean.app.MainActivity;
import de.bikebean.app.R;
import de.bikebean.app.Utils;
import de.bikebean.app.db.status.Status;
import de.bikebean.app.ui.status.location.LocationUpdater;
import de.bikebean.app.ui.status.location.UpdateChecker;
import de.bikebean.app.ui.status.settings.SettingsActivity;
import de.bikebean.app.ui.status.sms.SmsActivity;
import de.bikebean.app.ui.status.sms.SmsViewModel;
import de.bikebean.app.ui.status.sms.send.SmsSender;

public class StatusFragment extends Fragment {

    private Context ctx;
    private FragmentActivity act;
    private SharedPreferences sharedPreferences;
    private SmsViewModel smsViewModel;
    private StatusViewModel statusViewModel;

    private String numberBikeBean;

    // Ui Elements
    private Button buttonCreateSmsView, buttonAdditionalSettings, buttonGetLocation, buttonGetStatus;
    private TextView statusLastChangedText, batteryLastChangedText, batteryStatusText;
    private TextView latText, lngText, accText, locationLastChangedText;
    private ProgressBar progressBar;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState)
    {
        View root = inflater.inflate(R.layout.fragment_status, container, false);

        // UI Elements
        latText = root.findViewById(R.id.lat);
        lngText = root.findViewById(R.id.lng);
        accText = root.findViewById(R.id.acc);
        progressBar = root.findViewById(R.id.progressBar);
        locationLastChangedText = root.findViewById(R.id.datetimeText1);
        statusLastChangedText = root.findViewById(R.id.datetimeText2);
        batteryLastChangedText = root.findViewById(R.id.datetimeText3);
        batteryStatusText = root.findViewById(R.id.batteryStatusText);
        buttonAdditionalSettings = root.findViewById(R.id.button_additional_settings);
        buttonCreateSmsView = root.findViewById(R.id.sms_button);
        buttonGetStatus = root.findViewById(R.id.button_get_status);
        buttonGetLocation = root.findViewById(R.id.button_get_location);

        return root;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // Get Activity and Context
        act = Objects.requireNonNull(getActivity());
        ctx = act.getApplicationContext();

        final SmsSender smsSender = new SmsSender(ctx, act);
        smsViewModel = new ViewModelProvider(this).get(SmsViewModel.class);
        statusViewModel = new ViewModelProvider(this).get(StatusViewModel.class);
        statusViewModel.getStatusBattery().observe(getViewLifecycleOwner(), statuses -> {
            if (statuses.size() == 0)
                return;

            Status s = statuses.get(0);
            String batteryStatus = s.getValue() + " %";
            batteryStatusText.setText(batteryStatus);
            batteryLastChangedText.setText(Utils.convertToTime(s.getTimestamp()));
        });
        statusViewModel.getStatusLocationLat().observe(getViewLifecycleOwner(), statuses -> {
            if (statuses.size() == 0)
                return;
            latText.setText(String.valueOf(statuses.get(0).getValue()));
            locationLastChangedText.setText(Utils.convertToTime(statuses.get(0).getTimestamp()));
        });
        statusViewModel.getStatusLocationLng().observe(getViewLifecycleOwner(), statuses -> {
            if (statuses.size() == 0)
                return;
            lngText.setText(String.valueOf(statuses.get(0).getValue()));
            locationLastChangedText.setText(Utils.convertToTime(statuses.get(0).getTimestamp()));
        });
        statusViewModel.getStatusLocationAcc().observe(getViewLifecycleOwner(), statuses -> {
            if (statuses.size() == 0)
                return;
            accText.setText(String.valueOf(statuses.get(0).getValue()));
            locationLastChangedText.setText(Utils.convertToTime(statuses.get(0).getTimestamp()));
        });

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(ctx);
        numberBikeBean = sharedPreferences.getString("number", "");

        // Finalize UI Elements
        buttonCreateSmsView.setOnClickListener(v -> {
            Intent intent = new Intent(ctx, SmsActivity.class);
            act.startActivity(intent);
        });
        buttonGetLocation.setOnClickListener(v -> {
            Log.d(MainActivity.TAG, "Button gedrückt");
            smsSender.send(numberBikeBean, "Wapp", smsViewModel);
        });
        buttonGetStatus.setOnClickListener(v -> {
            Log.d(MainActivity.TAG, "Button gedrückt");
            smsSender.send(numberBikeBean, "Status", smsViewModel);
        });
        buttonAdditionalSettings.setOnClickListener(v -> {
            Intent intent = new Intent(ctx, SettingsActivity.class);
            act.startActivity(intent);
        });
    }

    @Override
    public void onResume() {
        super.onResume();

        SmsSender smsSender = new SmsSender(ctx, act);

        progressBar.setVisibility(ProgressBar.GONE);
        statusLastChangedText.setText(sharedPreferences.getString("statusLastChange", ""));

        // Get the last two SMS and update the local database
        new Utils(ctx, smsViewModel, statusViewModel).execute();

        // Check if latest coordinates were retrieved
        new UpdateChecker(statusViewModel, isLatLngUpdated -> {
            if (!isLatLngUpdated) {
                latText.setText("");
                lngText.setText("");
                accText.setText("");
                progressBar.setVisibility(ProgressBar.VISIBLE);
                new LocationUpdater(ctx, statusViewModel).execute();
            }
        }).execute();

        // Check if the warning number is set, otherwise send a SMS
        if (!Utils.isWarningNumberSet(ctx)) {
            Log.d(MainActivity.TAG, "Warningnumber is not set!");
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("askedForWarningNumber", true);
            editor.apply();

            smsSender.send(numberBikeBean, "Warningnumber", smsViewModel);
        } else {
            Log.d(MainActivity.TAG, "Warningnumber is orderly set.");
        }
    }
}
