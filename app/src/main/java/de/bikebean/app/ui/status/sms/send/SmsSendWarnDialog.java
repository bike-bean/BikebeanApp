package de.bikebean.app.ui.status.sms.send;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import de.bikebean.app.R;

public class SmsSendWarnDialog extends DialogFragment {

    private final Activity act;
    private final iSmsSender smsSender;
    private final iSmsCanceler smsCanceler;

    private final String message;

    public interface iSmsSender {
        void send();
    }

    public interface iSmsCanceler {
        void cancel();
    }

    SmsSendWarnDialog(Activity act, String message, iSmsSender smsSender, iSmsCanceler smsCanceler) {
        this.smsSender = smsSender;
        this.smsCanceler = smsCanceler;
        this.act = act;

        this.message = message;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(act);

        LayoutInflater inflater = act.getLayoutInflater();

        @SuppressLint("InflateParams")
        View v = inflater.inflate(R.layout.dialog_warn_sms, null);

        TextView smsMessage = v.findViewById(R.id.txtMsgYou2);
        TextView smsMessageDots = v.findViewById(R.id.txtMsgFrom2);
        smsMessage.setText(message);
        smsMessageDots.setText("...");

        builder.setView(v)
                .setMessage(R.string.sms_send_warning)
                .setPositiveButton(R.string.continue_send_sms, (dialog, id) -> smsSender.send())
                .setNegativeButton(R.string.abort_send_sms, (dialog, id) -> smsCanceler.cancel());

        return builder.create();
    }
}
