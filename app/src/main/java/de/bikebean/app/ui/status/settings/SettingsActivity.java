package de.bikebean.app.ui.status.settings;

import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.EditTextPreference;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
// import androidx.preference.SwitchPreference;

import de.bikebean.app.R;

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
        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey);

            // SwitchPreference wlanSwitch = findPreference("wlan");
            EditTextPreference numberPreference = findPreference("number");
            EditTextPreference warningNumberPreference = findPreference("warningNumber");
            ListPreference intervalPreference = findPreference("interval");

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
            }

            if (warningNumberPreference != null) {
                warningNumberPreference.setSummaryProvider(new Preference.SummaryProvider<EditTextPreference>() {
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
            }
        }
    }
}