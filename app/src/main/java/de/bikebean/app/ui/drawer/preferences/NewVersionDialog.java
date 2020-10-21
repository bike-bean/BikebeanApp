package de.bikebean.app.ui.drawer.preferences;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import de.bikebean.app.R;

public class NewVersionDialog extends DialogFragment {

    private final @NonNull Activity act;

    private final @NonNull NewVersionDownloadHandler newVersionDownloadHandler;
    private final @NonNull NewVersionDownloadCanceller newVersionDownloadCanceller;

    private final @NonNull Release release;

    public interface NewVersionDownloadHandler {
        void downloadNewVersion(final @NonNull String address);
    }

    public interface NewVersionDownloadCanceller {
        void cancel();
    }

    NewVersionDialog(final @NonNull Activity act, final @NonNull Release release,
                     final @NonNull NewVersionDownloadHandler r1,
                     final @NonNull NewVersionDownloadCanceller r2) {
        this.act = act;

        this.newVersionDownloadHandler = r1;
        this.newVersionDownloadCanceller = r2;

        this.release = release;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(final @Nullable Bundle savedInstanceState) {
        final @NonNull AlertDialog.Builder builder = new AlertDialog.Builder(act);

        final @NonNull String message = getString(
                R.string.update_text_1) +
                release.getName() +
                getString(R.string.update_text_2
                );

        builder.setTitle(R.string.update_title)
                .setMessage(message)
                .setPositiveButton(R.string.continue_update, (dialog, id) ->
                        newVersionDownloadHandler.downloadNewVersion(release.getUrl())
                )
                .setNegativeButton(R.string.cancel_update, (dialog, id) ->
                        newVersionDownloadCanceller.cancel());

        return builder.create();
    }
}
