package de.bikebean.app.ui.status.preferences;

import android.content.Context;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.preference.EditTextPreference;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SwitchPreference;

import java.util.Objects;

import de.bikebean.app.MainActivity;
import de.bikebean.app.R;
import de.bikebean.app.Utils;
import de.bikebean.app.ui.status.sms.SmsViewModel;
import de.bikebean.app.ui.status.sms.send.SmsSender;

public class PreferencesActivity extends AppCompatActivity {

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
        private SmsViewModel smsViewModel;

        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey);

            smsViewModel = new ViewModelProvider(this).get(SmsViewModel.class);

            // act and ctx
            final FragmentActivity act = Objects.requireNonNull(getActivity());
            final Context ctx = act.getApplicationContext();

            final SmsSender smsSender = new SmsSender(ctx, act);

            // Preferences
            final EditTextPreference numberPreference = findPreference("number");
            final EditTextPreference warningNumberPreference = findPreference("warningNumber");
            final ListPreference intervalPreference = findPreference("interval");
            final SwitchPreference wifiSwitch = findPreference("wlan");

            // Last Change Strings
            final EditTextPreference intervalLastChange = findPreference("intervalLastChange");
            final EditTextPreference wifiLastChange = findPreference("wifiLastChange");

            final String numberBike = Objects.requireNonNull(numberPreference).getText();

            numberPreference.setOnBindEditTextListener(
                    editText -> editText.setInputType(InputType.TYPE_CLASS_PHONE));
            numberPreference.setDialogMessage(R.string.number_subtitle);
            numberPreference.setOnPreferenceChangeListener((preference, newValue) -> {
                if (newValue.toString().equals("")) {
                    Toast.makeText(
                            ctx, "Bitte Nummber eingeben!",
                            Toast.LENGTH_LONG).show();
                    return false;
                } else if (!newValue.toString().substring(0, 1).equals("+")) {
                    Toast.makeText(
                            ctx, "Bitte mit Ländercode (+49) eingeben!",
                            Toast.LENGTH_LONG).show();
                    return false;
                } else {
                    resetAll();
                    return true;
                }
            });

            if (intervalPreference != null) {
                intervalPreference.setSummaryProvider((Preference.SummaryProvider<ListPreference>) preference -> {
                    String value = preference.getValue();
                    if (TextUtils.isEmpty(value)) {
                        return "Not set";
                    }
                    return "Bike Bean wird alle " + value + "h neue Nachrichten prüfen.";
                });

                intervalPreference.setOnPreferenceChangeListener((preference, newValue) -> {
                    Log.d(MainActivity.TAG, "Setting " + preference.getKey() +
                            " about to be changed to " + newValue);
                    String timeStamp = Utils.getTimestamp();
                    Log.d(MainActivity.TAG, "Date: " + timeStamp);
                    if (intervalLastChange != null) {
                        intervalLastChange.setText(timeStamp);
                    }

                    smsSender.send(numberBike, "Int " + newValue, smsViewModel);
                    return true;
                });
            }

            if (warningNumberPreference != null) {
                warningNumberPreference.setSummaryProvider(
                        (Preference.SummaryProvider<EditTextPreference>) preference -> {
                            String text = preference.getText();
                            if (TextUtils.isEmpty(text)) {
                                return "Automatisch";
                            }
                            return "Automatisch (" + text + ")";
                        });
            }

            if (wifiSwitch != null) {
                wifiSwitch.setOnPreferenceChangeListener((preference, newValue) -> {
                    Log.d(MainActivity.TAG, "Setting " + preference.getKey() +
                            " about to be changed to " + newValue);
                    String timeStamp = Utils.getTimestamp();
                    Log.d(MainActivity.TAG, "Date: " + timeStamp);
                    if (wifiLastChange != null) {
                        wifiLastChange.setText(timeStamp);
                    }

                    smsSender.send(numberBike, "Wifi " + ((boolean) newValue ? "on" : "off" ), smsViewModel);
                    return true;
                });
            }
        }

        void resetAll() {
            // Preferences
            final EditTextPreference warningNumberPreference = findPreference("warningNumber");
            final ListPreference intervalPreference = findPreference("interval");
            final SwitchPreference wifiSwitch = findPreference("wlan");
            final EditTextPreference intervalLastChange = findPreference("intervalLastChange");
            final EditTextPreference warningNumberLastChange = findPreference("warningNumberLastChange");
            final EditTextPreference wifiLastChange = findPreference("wifiLastChange");
            final SwitchPreference askedForWarningNumber = findPreference("askedForWarningNumber");
            final SwitchPreference initialLoading = findPreference("initialLoading");

            Objects.requireNonNull(intervalPreference).setValue("1");
            Objects.requireNonNull(wifiSwitch).setChecked(false);
            Objects.requireNonNull(warningNumberPreference).setText("");
            Objects.requireNonNull(intervalLastChange).setText("");
            Objects.requireNonNull(warningNumberLastChange).setText("");
            Objects.requireNonNull(wifiLastChange).setText("");
            Objects.requireNonNull(askedForWarningNumber).setChecked(false);
            Objects.requireNonNull(initialLoading).setChecked(true);
        }
    }
}
