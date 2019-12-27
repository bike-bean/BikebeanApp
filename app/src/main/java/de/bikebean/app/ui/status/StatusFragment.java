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
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.preference.PreferenceManager;

import java.util.Objects;

import de.bikebean.app.MainActivity;
import de.bikebean.app.R;
import de.bikebean.app.ui.status.settings.SettingsActivity;
import de.bikebean.app.ui.status.sms.SmsActivity;
import de.bikebean.app.ui.status.sms.send.SmsSender;

public class StatusFragment extends Fragment {

    protected Context ctx;
    protected FragmentActivity act;
    private SharedPreferences sharedPreferences;

    // Ui Elements
    private Button buttonCreateSmsView;
    private Button buttonAdditionalSettings;
    private Button buttonGetLocation;
    private Button buttonGetStatus;
    private TextView statusLastChangedText;
    private TextView batteryLastChangedText;
    private TextView batteryStatusText;
    private TextView latText;
    private TextView lngText;
    private TextView accText;
    private TextView locationLastChangedText;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState)
    {
        View root = inflater.inflate(R.layout.fragment_status, container, false);

        // UI Elements
        latText = root.findViewById(R.id.lat);
        lngText = root.findViewById(R.id.lng);
        accText = root.findViewById(R.id.acc);
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

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(ctx);
        final String numberBike = sharedPreferences.getString("number", "");

        // Finalize UI Elements
        buttonCreateSmsView.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(ctx, SmsActivity.class);
                act.startActivity(intent);
            }
        });

        buttonGetLocation.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Log.d(MainActivity.TAG, "Button gedrückt");
                smsSender.send(numberBike, "wapp");
            }
        });

        buttonGetStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(MainActivity.TAG, "Button gedrückt");
                smsSender.send(numberBike, "status");
            }
        });

        buttonAdditionalSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ctx, SettingsActivity.class);
                act.startActivity(intent);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        String batteryStatus = sharedPreferences.getInt("batteryStatus", 0) + " %";

        latText.setText(String.valueOf(sharedPreferences.getFloat("lat", (float) 0.0)));
        lngText.setText(String.valueOf(sharedPreferences.getFloat("lng", (float) 0.0)));
        accText.setText(String.valueOf(sharedPreferences.getFloat("acc", (float) 0.0)));
        locationLastChangedText.setText(sharedPreferences.getString("locationLastChange", ""));
        statusLastChangedText.setText(sharedPreferences.getString("statusLastChange", ""));
        batteryLastChangedText.setText(sharedPreferences.getString("batteryLastChange", ""));
        batteryStatusText.setText(batteryStatus);
    }
}
