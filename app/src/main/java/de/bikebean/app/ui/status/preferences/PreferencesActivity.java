package de.bikebean.app.ui.status.preferences;

import android.content.Context;
import android.os.Bundle;
import android.text.InputType;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.preference.EditTextPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;

import java.util.Objects;

import de.bikebean.app.R;
import de.bikebean.app.db.BikeBeanRoomDatabase;
import de.bikebean.app.ui.status.StateViewModel;
import de.bikebean.app.ui.status.sms.SmsViewModel;

public class PreferencesActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.settings, new SettingsFragment())
                .commit();

        Toolbar toolbar = findViewById(R.id.toolbar1);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    public static class SettingsFragment extends PreferenceFragmentCompat {

        StateViewModel stateViewModel;
        SmsViewModel smsViewModel;
        Context ctx;

        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey);

            stateViewModel = new ViewModelProvider(this).get(StateViewModel.class);
            smsViewModel = new ViewModelProvider(this).get(SmsViewModel.class);

            // act and ctx
            final FragmentActivity act = getActivity();
            ctx = Objects.requireNonNull(act).getApplicationContext();

            String address = PreferenceManager.getDefaultSharedPreferences(ctx)
                    .getString("number", "");
            ResetDialog resetDialog =
                    new ResetDialog(act, address, this::resetAll, this::cancelReset);

            // Preferences
            final EditTextPreference numberPreference = findPreference("number");
            final Preference resetPreference = findPreference("reset");

            if (numberPreference != null) {
                numberPreference.setOnBindEditTextListener(
                        editText -> editText.setInputType(InputType.TYPE_CLASS_PHONE));
                numberPreference.setDialogMessage(R.string.number_subtitle);
                numberPreference.setOnPreferenceChangeListener((preference, newValue) -> {
                    if (newValue.toString().equals("")) {
                        Toast.makeText(ctx, R.string.number_error, Toast.LENGTH_LONG).show();
                        return false;
                    } else if (!newValue.toString().substring(0, 1).equals("+")) {
                        Toast.makeText(ctx, R.string.number_subtitle, Toast.LENGTH_LONG).show();
                        return false;
                    } else {
                        resetAll(newValue.toString());
                        return true;
                    }
                });
            }

            if (resetPreference != null) {
                resetPreference.setOnPreferenceClickListener(preference -> {
                    resetDialog.show(act.getSupportFragmentManager(), "resetDialog");
                    return true;
                });
            }
        }

        void resetAll(String address) {
            // reset DB and repopulate it
            BikeBeanRoomDatabase.resetAll();
            stateViewModel.insertInitialStates(ctx);
            smsViewModel.fetchSmsSync(ctx, stateViewModel, address);
        }

        void cancelReset() {
            assert true;
        }
    }
}
