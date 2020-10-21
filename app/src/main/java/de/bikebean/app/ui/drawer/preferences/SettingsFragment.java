package de.bikebean.app.ui.drawer.preferences;

import android.os.Bundle;
import android.text.InputType;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.lifecycle.ViewModelProvider;
import androidx.preference.EditTextPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import com.google.android.material.snackbar.Snackbar;

import de.bikebean.app.R;
import de.bikebean.app.db.BikeBeanRoomDatabase;
import de.bikebean.app.db.type.types.Initial;
import de.bikebean.app.ui.drawer.log.LogViewModel;
import de.bikebean.app.ui.drawer.sms_history.SmsViewModel;
import de.bikebean.app.ui.drawer.status.StateViewModel;
import de.bikebean.app.ui.utils.Utils;
import de.bikebean.app.ui.utils.preferences.PreferencesUtils;

public class SettingsFragment extends PreferenceFragmentCompat {

    public static final @NonNull String RESET_PREFERENCE = "reset";
    public static final @NonNull String NAME_PREFERENCE = "name";
    public static final @NonNull String NUMBER_PREFERENCE = "number";
    public static final @NonNull String MAP_TYPE_PREFERENCE = "mapType";
    public static final @NonNull String INIT_STATE_PREFERENCE = "initState";
    public static final @NonNull String PREF_UNIQUE_ID = "DEVICE_UUID";

    private StateViewModel stateViewModel;
    private SmsViewModel smsViewModel;
    private LogViewModel logViewModel;

    @Override
    public void onCreatePreferences(final @NonNull Bundle savedInstanceState,
                                    final @NonNull String rootKey) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey);

        stateViewModel = new ViewModelProvider(this).get(StateViewModel.class);
        smsViewModel = new ViewModelProvider(this).get(SmsViewModel.class);
        logViewModel = new ViewModelProvider(this).get(LogViewModel.class);

        final @Nullable String address =
                PreferencesUtils.getBikeBeanNumber(requireContext(), logViewModel);
        final @Nullable ResetDialog resetDialog;

        if (address != null)
            resetDialog = new ResetDialog(
                    requireActivity(), address,
                    this::resetAll, this::cancelReset
            );
        else
            resetDialog = null;

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
        final @Nullable @StringRes Integer errorString = Utils.getErrorString(newValue.toString());

        if (errorString != null) {
            Snackbar.make(requireView(), errorString, Snackbar.LENGTH_LONG).show();

            if (errorString != R.string.number_no_blanks)
                return false;

            final @Nullable EditTextPreference numberPreference =
                    findPreference(NUMBER_PREFERENCE);
            if (numberPreference != null)
                numberPreference.setText(Utils.eliminateSpaces(newValue.toString()));
            else
                logViewModel.e("Failed to load BB-number! Maybe it's not set?");

            return false;
        } else {
            resetAll(newValue.toString());
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

    public enum INIT_STATE {
        NEW, ADDRESS, DONE
    }
}
