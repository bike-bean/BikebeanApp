package de.bikebean.app.ui.main.status.menu.preferences;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import de.bikebean.app.R;

public class NewVersionDialog extends DialogFragment {

    private final Activity act;

    private final NewVersionDownloadHandler newVersionDownloadHandler;
    private final NewVersionDownloadCanceller newVersionDownloadCanceller;

    private final String address;
    private final String versionName;

    public interface NewVersionDownloadHandler {
        void downloadNewVersion(String address);
    }

    public interface NewVersionDownloadCanceller {
        void cancel();
    }

    NewVersionDialog(Activity act, String address, String versionName,
                     NewVersionDownloadHandler r1,
                     NewVersionDownloadCanceller r2) {
        this.act = act;

        this.newVersionDownloadHandler = r1;
        this.newVersionDownloadCanceller = r2;

        this.address = address;
        this.versionName = versionName;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(act);
        String message = getString(
                R.string.update_text_1) +
                versionName +
                getString(R.string.update_text_2
                );

        builder.setTitle(R.string.update_title)
                .setMessage(message)
                .setPositiveButton(R.string.continue_update, (dialog, id) ->
                        newVersionDownloadHandler.downloadNewVersion(address))
                .setNegativeButton(R.string.cancel_update, (dialog, id) ->
                        newVersionDownloadCanceller.cancel());

        return builder.create();
    }
}
