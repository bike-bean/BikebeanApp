package de.bikebean.app.ui.utils.sms.send;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.google.android.material.snackbar.Snackbar;

import de.bikebean.app.R;

public class SmsSendWarnDialog extends DialogFragment {

    private final @NonNull Activity act;
    private final @NonNull SmsSender smsSender;

    SmsSendWarnDialog(@NonNull Activity act, @NonNull SmsSender smsSender) {
        this.act = act;
        this.smsSender = smsSender;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        final @NonNull AlertDialog.Builder builder = new AlertDialog.Builder(act);

        final @NonNull LayoutInflater inflater = act.getLayoutInflater();

        @SuppressLint("InflateParams")
        final @NonNull View v = inflater.inflate(R.layout.dialog_warn_sms, null);

        final @Nullable TextView smsMessage = v.findViewById(R.id.txtMsgYou2);
        final @Nullable TextView smsMessageDots = v.findViewById(R.id.txtMsgFrom2);

        if (smsMessage != null && smsMessageDots != null) {
            smsMessage.setText(smsSender.getMessage().getMsg());
            smsMessageDots.setText("...");
        } else {
            Snackbar.make(
                    requireView(),
                    smsSender.getMessage().getMsg() + "senden?",
                    Snackbar.LENGTH_LONG
            );
        }

        builder.setView(v)
                .setMessage(R.string.sms_send_warning)
                .setPositiveButton(R.string.continue_send_sms, (dialog, id) -> smsSender.getPermissions())
                .setNegativeButton(R.string.abort_send_sms, (dialog, id) -> smsSender.cancelSend());

        return builder.create();
    }
}
