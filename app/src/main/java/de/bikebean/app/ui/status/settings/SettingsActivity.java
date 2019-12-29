package de.bikebean.app.ui.status.settings;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
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

import java.util.Objects;

import de.bikebean.app.MainActivity;
import de.bikebean.app.R;
import de.bikebean.app.ui.status.sms.send.SmsSender;

public class SettingsActivity extends AppCompatActivity {

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

            EditTextPreference number = findPreference("number");

            final FragmentActivity act = Objects.requireNonNull(getActivity());
            fragmentManager = act.getSupportFragmentManager();
            final Context ctx = act.getApplicationContext();

            final String numberBike = Objects.requireNonNull(number).getText();
            final SmsSender smsSender = new SmsSender(ctx, act);

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
                                String timeStamp = UpdateSettings.getTimestamp();
                                Log.d(MainActivity.TAG, "Date: " + timeStamp);
                                if (intervalLastChange != null) {
                                    intervalLastChange.setText(timeStamp);
                                }

                                smsSender.send(numberBike, "Int " + newValue);
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
            }

            if (numberPreference != null) {
                numberPreference.setOnBindEditTextListener(
                        new EditTextPreference.OnBindEditTextListener() {
                            @Override
                            public void onBindEditText(@NonNull EditText editText) {
                                editText.setInputType(InputType.TYPE_CLASS_PHONE);
                            }
                        });
                numberPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                    @Override
                    public boolean onPreferenceChange(Preference preference, Object newValue) {
                        UpdateSettings updateSettings = new UpdateSettings();
                        updateSettings.resetAll(ctx);
                        return true;
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
                                String timeStamp = UpdateSettings.getTimestamp();
                                Log.d(MainActivity.TAG, "Date: " + timeStamp);
                                if (wifiLastChange != null) {
                                    wifiLastChange.setText(timeStamp);
                                }

                                smsSender.send(numberBike, "Wifi " + ((boolean) newValue ? "on" : "off" ));
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
}
