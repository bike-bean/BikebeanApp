package de.bikebean.app.ui.status.sms.send;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import java.lang.ref.WeakReference;

import de.bikebean.app.R;

public class SmsSendWarnDialog extends DialogFragment {

    private final Activity act;
    private final SmsSender smsSender;

    SmsSendWarnDialog(SmsSender smsSender, Activity act) {
        this.smsSender = smsSender;
        this.act = act;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(act);

        builder.setMessage(R.string.sms_send_warning)
                .setPositiveButton(R.string.continue_send_sms, (dialog, id) -> smsSender.reallySend())
                .setNegativeButton(R.string.abort_send_sms, ((dialog, id) -> smsSender.cancelSend()));

        return builder.create();
    }
}
