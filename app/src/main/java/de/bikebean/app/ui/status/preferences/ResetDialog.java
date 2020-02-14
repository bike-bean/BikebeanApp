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

    private final ResetHandler resetHandler;
    private final ResetCanceller resetCanceller;

    private final String address;

    public interface ResetHandler {
        void reset(String address);
    }

    public interface ResetCanceller {
        void cancel();
    }

    ResetDialog(Activity act, String address, ResetHandler r1, ResetCanceller r2) {
        this.act = act;

        this.resetHandler = r1;
        this.resetCanceller = r2;

        this.address = address;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(act);

        builder.setTitle(R.string.db_reset)
                .setMessage(R.string.db_reset_warning)
                .setPositiveButton(R.string.continue_reset, (dialog, id) ->
                        resetHandler.reset(address))
                .setNegativeButton(R.string.abort_send_sms, (dialog, id) ->
                        resetCanceller.cancel());

        return builder.create();
    }
}
