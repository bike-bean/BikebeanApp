package de.bikebean.app.ui.status.preferences;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import de.bikebean.app.R;

public class ResetDialog extends DialogFragment {

    private final Activity act;
    private final PreferencesActivity.SettingsFragment settingsFragment;

    private final String address;

    ResetDialog(Activity act, String address,
                PreferencesActivity.SettingsFragment settingsFragment) {
        this.act = act;
        this.address = address;
        this.settingsFragment = settingsFragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(act);

        builder.setTitle(R.string.db_reset)
                .setMessage(R.string.db_reset_warning)
                .setPositiveButton(R.string.continue_reset, (dialog, id) ->
                        settingsFragment.resetAll(address))
                .setNegativeButton(R.string.abort_send_sms, (dialog, id) ->
                        settingsFragment.cancelReset() );

        return builder.create();
    }
}
