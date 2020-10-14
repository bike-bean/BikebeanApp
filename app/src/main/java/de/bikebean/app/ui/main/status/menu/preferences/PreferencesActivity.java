package de.bikebean.app.ui.main.status.menu.preferences;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
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

    public static final @NonNull String NUMBER_PREFERENCE = "number";
    public static final @NonNull String RESET_PREFERENCE = "reset";
    public static final @NonNull String NAME_PREFERENCE = "name";
    public static final @NonNull String MAP_TYPE_PREFERENCE = "mapType";

    private TextView versionNameNew;
    private Button downloadNewVersionButton;

    @Override
    protected void onCreate(final @Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.settings, new SettingsFragment())
                .commit();

        final @NonNull LogViewModel logViewModel =
                new ViewModelProvider(this).get(LogViewModel.class);
        final @NonNull PreferencesViewModel preferencesViewModel =
                new ViewModelProvider(this).get(PreferencesViewModel.class);

        final @Nullable Toolbar toolbar = findViewById(R.id.toolbar1);
        setSupportActionBar(toolbar);

        final @Nullable ActionBar actionBar = getSupportActionBar();
        if (actionBar != null)
            actionBar.setDisplayHomeAsUpEnabled(true);

        final @Nullable TextView versionName = findViewById(R.id.versionName);
        final @NonNull String versionNameString = "Aktuelle Version: " + Utils.getVersionName();
        if (versionName != null)
            versionName.setText(versionNameString);
        else
            logViewModel.e("Failed to load TextView versionName!");

        versionNameNew = findViewById(R.id.versionName2);
        downloadNewVersionButton = findViewById(R.id.downloadNewVersion);
        preferencesViewModel.getNewVersion().observe(this, setVersionNameNew);

        new VersionChecker(
                getApplicationContext(),
                logViewModel, preferencesViewModel,
                this::newerVersionHandler
        ).execute();
    }

    private final @NonNull Observer<Release> setVersionNameNew = s -> {
        if (s.getUrl().equals(""))
            return;

        final @NonNull String versionNameString = "Neueste Version: " + s.getName();

        versionNameNew.setVisibility(View.VISIBLE);
        versionNameNew.setText(versionNameString);
        downloadNewVersionButton.setVisibility(View.VISIBLE);
        downloadNewVersionButton.setOnClickListener(v -> downloadNewVersion(s.getUrl()));
    };

    void newerVersionHandler(final @NonNull Release release) {
        new NewVersionDialog(
                this, release,
                this::downloadNewVersion, this::cancelNewVersionDownload
        ).show(getSupportFragmentManager(),"newVersionDialog");
    }

    void downloadNewVersion(final @NonNull String url) {
        final @NonNull Intent intent = new Intent(ACTION_VIEW, Uri.parse(url));
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

        @Override
        public void onCreatePreferences(final @NonNull Bundle savedInstanceState,
                                        final @NonNull String rootKey) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey);

            stateViewModel = new ViewModelProvider(this).get(StateViewModel.class);
            smsViewModel = new ViewModelProvider(this).get(SmsViewModel.class);
            logViewModel = new ViewModelProvider(this).get(LogViewModel.class);

            final @Nullable String address =
                    PreferenceManager.getDefaultSharedPreferences(requireContext())
                            .getString(NUMBER_PREFERENCE, null);
            final @Nullable ResetDialog resetDialog;

            if (address != null)
                resetDialog = new ResetDialog(
                        requireActivity(), address,
                        this::resetAll, this::cancelReset
                );
            else {
                resetDialog = null;
                logViewModel.e("Failed to load BB-number! Maybe it's not set?");
            }

            /*
             Preferences
             */
            final @Nullable EditTextPreference numberPreference =
                    findPreference(NUMBER_PREFERENCE);
            if (numberPreference != null) {
                numberPreference.setOnBindEditTextListener(numberEditTextListener);
                numberPreference.setDialogMessage(R.string.number_subtitle);
                numberPreference.setOnPreferenceChangeListener(numberChangeListener);
            }

            final @Nullable Preference resetPreference = findPreference(RESET_PREFERENCE);
            if (resetPreference != null) {
                if (resetDialog != null)
                    resetPreference.setOnPreferenceClickListener(preference -> {
                        resetDialog.show(requireActivity().getSupportFragmentManager(), "resetDialog");
                        return true;
                    });
                else
                    resetPreference.setEnabled(false);
            }
        }

        private final EditTextPreference.OnBindEditTextListener numberEditTextListener = text ->
                text.setInputType(InputType.TYPE_CLASS_PHONE);

        private final Preference.OnPreferenceChangeListener numberChangeListener =
                (preference, newValue) -> {
            final @NonNull String newValueString = newValue.toString();

            if (newValueString.isEmpty()) {
                Snackbar.make(
                        requireView(),
                        R.string.number_error,
                        Snackbar.LENGTH_LONG
                ).show();

                return false;
            } else if (!newValueString.substring(0, 1).equals("+")) {
                Snackbar.make(
                        requireView(),
                        R.string.number_subtitle,
                        Snackbar.LENGTH_LONG
                ).show();

                return false;
            } else if (newValueString.contains(" ")) {
                Snackbar.make(
                        requireView(),
                        R.string.number_no_blanks,
                        Snackbar.LENGTH_LONG
                ).show();

                final @Nullable EditTextPreference numberPreference =
                        findPreference(NUMBER_PREFERENCE);
                if (numberPreference != null)
                    numberPreference.setText(newValueString.replace(" ", ""));
                else
                    logViewModel.e("Failed to load BB-number! Maybe it's not set?");

                return false;
            } else {
                resetAll(newValueString);
                return true;
            }
        };

        void resetAll(final @NonNull String address) {
            // reset DB and repopulate it
            BikeBeanRoomDatabase.resetAll();
            stateViewModel.insert(new Initial());

            smsViewModel.fetchSmsSync(requireContext(), stateViewModel, logViewModel, address);

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
