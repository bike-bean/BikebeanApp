package de.bikebean.app.ui.status.sms.send;

import android.app.Dialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import java.util.Objects;

import de.bikebean.app.R;

public class SmsSendWarningDialogFragment extends DialogFragment {

    private SmsSender smsSender;

    void setSmsSender(SmsSender smsSender) {
        this.smsSender = smsSender;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(Objects.requireNonNull(getActivity()));

        builder.setMessage(R.string.sms_send_warning)
                .setPositiveButton(R.string.continue_send_sms, (dialog, id) -> smsSender.reallySend())
                .setNegativeButton(R.string.abort_send_sms, ((dialog, id) -> smsSender.cancelSend()));

        return builder.create();
    }
}
