package de.bikebean.app.ui.status.settings;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.preference.EditTextPreference;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;
import androidx.preference.SwitchPreference;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
// import java.util.Map;
import java.util.Objects;

import de.bikebean.app.MainActivity;
import de.bikebean.app.R;

public class SettingsActivity extends AppCompatActivity {

    private SharedPreferences settings;
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_settings);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.settings, new SettingsFragment())
                .commit();
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    public static class SettingsFragment extends PreferenceFragmentCompat {

        final SMSSendWarningDialogFragment smsSendWarningDialogFragment = new SMSSendWarningDialogFragment();
        FragmentManager fragmentManager;

        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey);

            EditTextPreference numberPreference = findPreference("number");

            EditTextPreference warningNumberPreference = findPreference("warningNumber");
            final EditTextPreference warningNumberLastChange = findPreference("warningNumberLastChange");

            SwitchPreference wifiSwitch = findPreference("wlan");
            final EditTextPreference wifiLastChange = findPreference("wifiLastChange");

            ListPreference intervalPreference = findPreference("interval");
            final EditTextPreference intervalLastChange = findPreference("intervalLastChange");

            final FragmentActivity act = Objects.requireNonNull(getActivity());
            fragmentManager = act.getSupportFragmentManager();

            if (intervalPreference != null) {
                intervalPreference.setSummaryProvider(new Preference.SummaryProvider<ListPreference>() {
                    @Override
                    public CharSequence provideSummary(ListPreference preference) {
                        String value = preference.getValue();
                        if (TextUtils.isEmpty(value)) {
                            return "Not set";
                        }
                        return "Bike Bean wird alle " + value + "h neue Nachrichten prüfen.";
                    }
                });

                intervalPreference.setOnPreferenceChangeListener(
                        new Preference.OnPreferenceChangeListener() {
                    @Override
                    public boolean onPreferenceChange(Preference preference, Object newValue) {
                        Log.d(MainActivity.TAG, "Setting " + preference.getKey() +
                                " about to be changed to " + newValue);
                        String timeStamp = SettingsActivity.getTimestamp();
                        Log.d(MainActivity.TAG, "Date: " + timeStamp);
                        if (intervalLastChange != null) {
                            intervalLastChange.setText(timeStamp);
                        }
                        return true;
                    }
                });
            }

            if (warningNumberPreference != null) {
                warningNumberPreference.setSummaryProvider(
                        new Preference.SummaryProvider<EditTextPreference>() {
                    @Override
                    public CharSequence provideSummary(EditTextPreference preference) {
                        String text = preference.getText();
                        if (TextUtils.isEmpty(text)) {
                            return "Automatisch";
                        }
                        return "Automatisch (" + text + ")";
                    }
                });

                warningNumberPreference.setOnPreferenceChangeListener(
                        new Preference.OnPreferenceChangeListener() {
                    @Override
                    public boolean onPreferenceChange(Preference preference, Object newValue) {
                        Log.d(MainActivity.TAG, "Setting " + preference.getKey() +
                                " about to be changed to " + newValue);
                        String timeStamp = SettingsActivity.getTimestamp();
                        Log.d(MainActivity.TAG, "Date: " + timeStamp);
                        if (warningNumberLastChange != null) {
                            warningNumberLastChange.setText(timeStamp);
                        }
                        return true;
                    }
                });
            }

            if (numberPreference != null) {
                numberPreference.setOnBindEditTextListener(
                        new EditTextPreference.OnBindEditTextListener() {
                    @Override
                    public void onBindEditText(@NonNull EditText editText) {
                                editText.setInputType(InputType.TYPE_CLASS_PHONE);
                    }
                });
            }

            if (wifiSwitch != null) {
                wifiSwitch.setOnPreferenceChangeListener(
                        new Preference.OnPreferenceChangeListener() {
                    @Override
                    public boolean onPreferenceChange(Preference preference, Object newValue) {
                        Log.d(MainActivity.TAG, "Setting " + preference.getKey() +
                                " about to be changed to " + newValue);
                        String timeStamp = SettingsActivity.getTimestamp();
                        Log.d(MainActivity.TAG, "Date: " + timeStamp);
                        if (wifiLastChange != null) {
                            wifiLastChange.setText(timeStamp);
                        }
                        return true;
                    }
                });
            }

            showDialog();
        }

        private void showDialog() {
            smsSendWarningDialogFragment.show(fragmentManager, "smswarning");
        }
    }

    public void updateBattery(Context ctx, int value) {
        if (settings == null) {
            settings = PreferenceManager.getDefaultSharedPreferences(ctx);
        }

        editor = settings.edit();

        editor.putInt("batteryStatus", value);
        editor.putString("batteryLastChange", getTimestamp());
        editor.putString("statusLastChange", getTimestamp());

        // Commit the edits!
        editor.apply();
    }

    public void updateInterval(Context ctx, String value) {
        if (settings == null) {
            settings = PreferenceManager.getDefaultSharedPreferences(ctx);
        }

        editor = settings.edit();

        Log.d(MainActivity.TAG, "Updating interval to " + value);

        editor.putString("interval", value);
        editor.putString("intervalLastChange", getTimestamp());
        editor.putString("statusLastChange", getTimestamp());

        // Commit the edits!
        editor.apply();
    }

    public void updateWarningNumber(Context ctx, String value) {
        if (settings == null) {
            settings = PreferenceManager.getDefaultSharedPreferences(ctx);
        }

        editor = settings.edit();

        editor.putString("warningNumber", value);
        editor.putString("warningNumberLastChange", getTimestamp());
        editor.putString("statusLastChange", getTimestamp());

        editor.apply();
    }

    public void updateWifi(Context ctx, boolean state) {
        if (settings == null) {
            settings = PreferenceManager.getDefaultSharedPreferences(ctx);
        }

        editor = settings.edit();

        editor.putBoolean("wlan", state);
        editor.putString("wifiLastChange", getTimestamp());
        editor.putString("statusLastChange", getTimestamp());

        editor.apply();
    }

    public void updatePosition(Context ctx, String value) {
        if (settings == null) {
            settings = PreferenceManager.getDefaultSharedPreferences(ctx);
        }

        editor = settings.edit();

        editor.putString("location", value);
        editor.putString("locationLastChange", getTimestamp());

        editor.apply();
    }

    public void updateWifiList(Context ctx, String value) {
        if (settings == null) {
            settings = PreferenceManager.getDefaultSharedPreferences(ctx);
        }

        editor = settings.edit();

        editor.putString("wifiList", value);
        editor.putString("wifiListLastChange", getTimestamp());

        editor.apply();
    }

    public void updateNoCellTowers(Context ctx, int number) {
        if (settings == null) {
            settings = PreferenceManager.getDefaultSharedPreferences(ctx);
        }

        editor = settings.edit();

        editor.putInt("numberCellTowers", number);
        editor.putString("numberCellTowersLastChange", getTimestamp());

        editor.apply();
    }

    public void updateNoWifiAccessPoints(Context ctx, int number) {
        if (settings == null) {
            settings = PreferenceManager.getDefaultSharedPreferences(ctx);
        }

        editor = settings.edit();

        editor.putInt("numberWifiAccessPoints", number);
        editor.putString("numberWifiAccessPointsLastChange", getTimestamp());

        editor.apply();
    }

    public void updateLngLat(Context ctx, Float Lat, Float Lng, Float Acc) {
        if (settings == null) {
            settings = PreferenceManager.getDefaultSharedPreferences(ctx);
        }

        editor = settings.edit();

        editor.putFloat("lat", Lat);
        editor.putFloat("lng", Lng);
        editor.putFloat("acc", Acc);
        editor.putString("latLngLastChange", getTimestamp());

        // Map<String,?> m = settings.getAll();

        editor.apply();
    }

    public static class SMSSendWarningDialogFragment extends DialogFragment {

        @NonNull
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the Builder class for convenient dialog construction
            AlertDialog.Builder builder = new AlertDialog.Builder(Objects.requireNonNull(getActivity()));

            builder.setMessage(R.string.sms_send_warning)
                    .setPositiveButton(R.string.continue_send_sms, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {

                        }
                    });

            return builder.create();
        }
    }

    private static String getTimestamp() {
        return new SimpleDateFormat("dd.MM.yyyy HH:mm:ss", Locale.GERMANY).format(Calendar.getInstance().getTime());
    }
}
