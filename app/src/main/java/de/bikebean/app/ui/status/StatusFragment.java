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

import java.util.Locale;
import java.util.Objects;

import de.bikebean.app.MainActivity;
import de.bikebean.app.R;
import de.bikebean.app.Utils;
import de.bikebean.app.db.status.Status;
import de.bikebean.app.ui.status.location.LocationUpdater;
import de.bikebean.app.ui.status.preferences.PreferencesActivity;
import de.bikebean.app.ui.status.sms.SmsActivity;
import de.bikebean.app.ui.status.sms.SmsViewModel;
import de.bikebean.app.ui.status.sms.send.SmsSender;

public class StatusFragment extends Fragment {

    private static final int LOCATION_DEFAULT = 0;
    private static final int LOCATION_PENDING = 1;

    private Context ctx;
    private FragmentActivity act;
    private SharedPreferences sharedPreferences;
    private SmsSender smsSender;
    private StatusViewModel statusViewModel;
    private SmsViewModel smsViewModel;

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

        smsSender = new SmsSender(ctx, act);
        statusViewModel = new ViewModelProvider(this).get(StatusViewModel.class);
        smsViewModel = new  ViewModelProvider(this).get(SmsViewModel.class);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(ctx);
        numberBikeBean = sharedPreferences.getString("number", "");

        statusViewModel.getStatusBattery().observe(getViewLifecycleOwner(), statuses -> {
            if (statuses.size() == 0)
                return;

            Status s = statuses.get(0);
            String batteryStatus = s.getValue() + " %";
            batteryStatusText.setText(batteryStatus);
            // Todo: change the battery icon based on state of charge
            batteryLastChangedText.setText(Utils.convertToTime(s.getTimestamp()));
        });
        statusViewModel.getStatusLocationLat().observe(getViewLifecycleOwner(), statuses -> {
            if (statuses.size() == 0)
                return;
            latText.setText(String.format(Locale.GERMANY, "%.7f", statuses.get(0).getValue()));
            locationLastChangedText.setText(Utils.convertToTime(statuses.get(0).getTimestamp()));
        });
        statusViewModel.getStatusLocationLng().observe(getViewLifecycleOwner(), statuses -> {
            if (statuses.size() == 0)
                return;
            lngText.setText(String.format(Locale.GERMANY, "%.7f", statuses.get(0).getValue()));
            locationLastChangedText.setText(Utils.convertToTime(statuses.get(0).getTimestamp()));
        });
        statusViewModel.getStatusLocationAcc().observe(getViewLifecycleOwner(), statuses -> {
            if (statuses.size() == 0)
                return;
            Status s = statuses.get(0);
            String acc = s.getValue() + " m";

            accText.setText(acc);
            locationLastChangedText.setText(Utils.convertToTime(statuses.get(0).getTimestamp()));
        });
        statusViewModel.getPendingCellTowers().observe(getViewLifecycleOwner(), statuses -> {
            if (statuses.size() == 0)
                return;

            String cellTowers = statuses.get(0).getLongValue();
            int smsId = statuses.get(0).getSmsId();
            setLocationArea(LOCATION_PENDING);

            new LocationUpdater(ctx, statusViewModel, isLocationUpdated -> {
                if (isLocationUpdated) {
                    smsViewModel.markParsed(smsId);
                    setLocationArea(LOCATION_PENDING);
                }
            }).execute(cellTowers, "cellTowers");
        });
        statusViewModel.getPendingWifiAccessPoints().observe(getViewLifecycleOwner(), statuses -> {
            if (statuses.size() == 0)
                return;

            String wifiAccessPoints = statuses.get(0).getLongValue();
            int smsId = statuses.get(0).getSmsId();
            setLocationArea(LOCATION_PENDING);

            new LocationUpdater(ctx, statusViewModel, isLocationUpdated -> {
                if (isLocationUpdated) {
                    smsViewModel.markParsed(smsId);
                    setLocationArea(LOCATION_DEFAULT);
                }
            }).execute(wifiAccessPoints, "wifiAccessPoints");
        });
        statusViewModel.getStatus().observe(getViewLifecycleOwner(), statuses -> {
            for (Status status : statuses)
                statusLastChangedText.setText(Utils.convertToTime(status.getTimestamp()));
        });

        // Finalize UI Elements
        buttonCreateSmsView.setOnClickListener(v ->
                act.startActivity(new Intent(ctx, SmsActivity.class)));
        buttonGetLocation.setOnClickListener(v ->
                smsSender.send(numberBikeBean, "Wapp", smsViewModel));
        buttonGetStatus.setOnClickListener(v ->
                smsSender.send(numberBikeBean, "Status", smsViewModel));
        buttonAdditionalSettings.setOnClickListener(v ->
                act.startActivity(new Intent(ctx, PreferencesActivity.class)));
    }

    @Override
    public void onResume() {
        super.onResume();

        // TODO: Make this async and "smarter" (not in onResume...)
        // Check if the warning number is set, otherwise send a SMS
        if (!Utils.isWarningNumberSet(sharedPreferences)) {
            Log.d(MainActivity.TAG, "Warningnumber is not set!");
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("askedForWarningNumber", true);
            editor.apply();

            smsSender.send(numberBikeBean, "Warningnumber", smsViewModel);
        } else {
            Log.d(MainActivity.TAG, "Warningnumber is orderly set.");
        }
    }

    private void setLocationArea(int state) {
        if (state == LOCATION_PENDING) {
            latText.setText("");
            lngText.setText("");
            accText.setText("");
            progressBar.setVisibility(ProgressBar.VISIBLE);
        } else if (state == LOCATION_DEFAULT) {
            progressBar.setVisibility(ProgressBar.GONE);
        }
    }
}
