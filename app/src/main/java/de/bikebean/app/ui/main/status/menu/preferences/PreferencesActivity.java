package de.bikebean.app.ui.main.status.menu.preferences;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.preference.EditTextPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;

import com.google.android.material.snackbar.Snackbar;

import de.bikebean.app.R;
import de.bikebean.app.db.BikeBeanRoomDatabase;
import de.bikebean.app.db.type.types.Initial;
import de.bikebean.app.ui.main.status.StateViewModel;
import de.bikebean.app.ui.main.status.menu.log.LogViewModel;
import de.bikebean.app.ui.main.status.menu.sms_history.SmsViewModel;
import de.bikebean.app.ui.utils.Release;
import de.bikebean.app.ui.utils.Utils;
import de.bikebean.app.ui.utils.VersionChecker;

import static android.content.Intent.*;

public class PreferencesActivity extends AppCompatActivity {

    private TextView versionNameNew;
    private Button downloadNewVersionButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.settings, new SettingsFragment())
                .commit();

        LogViewModel logViewModel =
                new ViewModelProvider(this).get(LogViewModel.class);
        PreferencesViewModel preferencesViewModel =
                new ViewModelProvider(this).get(PreferencesViewModel.class);

        Toolbar toolbar = findViewById(R.id.toolbar1);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        TextView versionName = findViewById(R.id.versionName);
        String versionNameString = "Aktuelle Version: " + Utils.getVersionName();
        versionName.setText(versionNameString);

        versionNameNew = findViewById(R.id.versionName2);
        downloadNewVersionButton = findViewById(R.id.downloadNewVersion);
        preferencesViewModel.getNewVersion().observe(this, setVersionNameNew);

        new VersionChecker(
                getApplicationContext(),
                logViewModel, preferencesViewModel,
                this::newerVersionHandler
        ).execute();
    }

    private final Observer<Release> setVersionNameNew = s -> {
        if (s.getUrl().equals(""))
            return;

        String versionNameString = "Neueste Version: " + s.getName();

        versionNameNew.setVisibility(View.VISIBLE);
        versionNameNew.setText(versionNameString);
        downloadNewVersionButton.setVisibility(View.VISIBLE);
        downloadNewVersionButton.setOnClickListener(v -> downloadNewVersion(s.getUrl()));
    };

    void newerVersionHandler(Release release) {
        new NewVersionDialog(this, release,
                this::downloadNewVersion,
                this::cancelNewVersionDownload)
                .show(
                        getSupportFragmentManager(),
                        "newVersionDialog"
                );
    }

    void downloadNewVersion(String url) {
        Uri webPage = Uri.parse(url);
        Intent intent = new Intent(ACTION_VIEW, webPage);
        if (intent.resolveActivity(getPackageManager()) != null)
            startActivity(intent);
    }

    void cancelNewVersionDownload() {
        assert true;
    }

    public static class SettingsFragment extends PreferenceFragmentCompat {

        StateViewModel stateViewModel;
        SmsViewModel smsViewModel;
        LogViewModel logViewModel;
        Context ctx;

        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey);

            stateViewModel = new ViewModelProvider(this).get(StateViewModel.class);
            smsViewModel = new ViewModelProvider(this).get(SmsViewModel.class);
            logViewModel = new ViewModelProvider(this).get(LogViewModel.class);

            // act and ctx
            final FragmentActivity act = requireActivity();
            ctx = act.getApplicationContext();

            String address = PreferenceManager.getDefaultSharedPreferences(ctx)
                    .getString("number", "");
            ResetDialog resetDialog =
                    new ResetDialog(act, address, this::resetAll, this::cancelReset);

            // Preferences
            final EditTextPreference numberPreference = findPreference("number");
            if (numberPreference != null) {
                numberPreference.setOnBindEditTextListener(numberEditTextListener);
                numberPreference.setDialogMessage(R.string.number_subtitle);
                numberPreference.setOnPreferenceChangeListener(numberChangeListener);
            }

            final Preference resetPreference = findPreference("reset");
            if (resetPreference != null) {
                resetPreference.setOnPreferenceClickListener(preference -> {
                    resetDialog.show(act.getSupportFragmentManager(), "resetDialog");
                    return true;
                });
            }
        }

        private final EditTextPreference.OnBindEditTextListener numberEditTextListener = text ->
                text.setInputType(InputType.TYPE_CLASS_PHONE);

        private final Preference.OnPreferenceChangeListener numberChangeListener =
                (preference, newValue) -> {
            if (newValue.toString().isEmpty()) {
                Snackbar.make(
                        requireView(),
                        R.string.number_error,
                        Snackbar.LENGTH_LONG
                ).show();
                return false;
            } else if (!newValue.toString().substring(0, 1).equals("+")) {
                Snackbar.make(
                        requireView(),
                        R.string.number_subtitle,
                        Snackbar.LENGTH_LONG
                ).show();
                return false;
            } else if (newValue.toString().contains(" ")) {
                Snackbar.make(
                        requireView(),
                        R.string.number_no_blanks,
                        Snackbar.LENGTH_LONG
                ).show();

                final EditTextPreference numberPreference = findPreference("number");
                if (numberPreference != null)
                    numberPreference.setText(newValue.toString().replace(" ", ""));

                return false;
            } else {
                resetAll(newValue.toString());
                return true;
            }
        };

        void resetAll(String address) {
            // reset DB and repopulate it
            BikeBeanRoomDatabase.resetAll();
            stateViewModel.insert(new Initial());
            smsViewModel.fetchSmsSync(ctx, stateViewModel, logViewModel, address);

            Snackbar.make(
                    requireView(), R.string.db_is_reset,
                    Snackbar.LENGTH_LONG
            ).show();
        }

        void cancelReset() {
            assert true;
        }
    }
}
