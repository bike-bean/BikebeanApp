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

    // Singleton stuff
    private static SmsSendWarnDialog inst;

    static SmsSendWarnDialog getInstance(SmsSender smsSender, Activity act) {
        if (inst == null)
            synchronized (SmsSendWarnDialog.class) {
                if (inst == null)
                    inst = new SmsSendWarnDialog(smsSender, act);
            }

        return inst;
    }

    private WeakReference<Activity> activityWeakReference;
    private SmsSender smsSender;

    private SmsSendWarnDialog(SmsSender smsSender, Activity activity) {
        this.smsSender = smsSender;
        this.activityWeakReference = new WeakReference<>(activity);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activityWeakReference.get());

        builder.setMessage(R.string.sms_send_warning)
                .setPositiveButton(R.string.continue_send_sms, (dialog, id) -> smsSender.reallySend())
                .setNegativeButton(R.string.abort_send_sms, ((dialog, id) -> smsSender.cancelSend()));

        return builder.create();
    }
}
