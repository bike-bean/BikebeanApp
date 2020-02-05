package de.bikebean.app.ui.status.preferences;

import android.content.Context;
import android.os.Bundle;
import android.text.InputType;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentActivity;
import androidx.preference.EditTextPreference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SwitchPreference;

import java.util.Objects;

import de.bikebean.app.R;
import de.bikebean.app.db.BikeBeanRoomDatabase;

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

        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey);

            // act and ctx
            final FragmentActivity act = getActivity();
            final Context ctx = Objects.requireNonNull(act).getApplicationContext();

            // Preferences
            final EditTextPreference numberPreference = findPreference("number");

            // Last Change Strings
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
                        resetAll();
                        return true;
                    }
                });
            }
        }

        void resetAll() {
            // Preferences
            final SwitchPreference initialLoading = findPreference("initialLoading");

            Objects.requireNonNull(initialLoading).setChecked(true);

            BikeBeanRoomDatabase.resetAll();
        }
    }
}
