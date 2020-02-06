package de.bikebean.app.ui.status.sms.send;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.provider.Telephony;
import android.telephony.SmsManager;
import android.widget.Toast;

import androidx.fragment.app.FragmentActivity;
import androidx.preference.PreferenceManager;

import de.bikebean.app.Utils;
import de.bikebean.app.db.sms.Sms;
import de.bikebean.app.db.state.State;
import de.bikebean.app.ui.status.StateViewModel;
import de.bikebean.app.ui.status.sms.SmsViewModel;

public class SmsSender {

    private final Context ctx;
    private final FragmentActivity act;
    private final SharedPreferences sharedPreferences;
    private final SmsViewModel smsViewModel;
    private final StateViewModel stateViewModel;

    public SmsSender(Context ctx, FragmentActivity act,
                     SmsViewModel smsViewModel, StateViewModel stateViewModel) {
        this.ctx = ctx;
        this.act = act;
        this.smsViewModel = smsViewModel;
        this.stateViewModel = stateViewModel;

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(act);
    }

    private String phoneNumber;
    private String message;
    private State update;

    public void send(String message, State update) {
        this.phoneNumber = sharedPreferences.getString("number","");
        this.message = message;
        this.update = update;

        if (phoneNumber.isEmpty())
            return;

        SmsSendWarnDialog smsSendWarnDialog = new SmsSendWarnDialog(this, act);
        Dialog dialog = smsSendWarnDialog.getDialog();

        if (dialog == null)
            dialog = smsSendWarnDialog.onCreateDialog(null);

        if (dialog.isShowing())
            return;

        smsSendWarnDialog.show(act.getSupportFragmentManager(), "smsWarning");
    }

    void cancelSend() {
        /*
        The user decided to cancel, don't send an SMS and signal it to the user.
        */
        Toast.makeText(ctx, "Vorgang abgebrochen.", Toast.LENGTH_LONG).show();

        stateViewModel.notifyIntervalAbort(true);
    }

    void reallySend() {
        /*
        The user decided to send the SMS, so actually send it!
        */
        SmsManager smsManager = SmsManager.getDefault();
        long timestamp = System.currentTimeMillis();

        synchronized (this) {
            new SmsSendIdGetter(smsViewModel, smsId -> smsViewModel.insert(new Sms(
                    smsId - 1, phoneNumber, message, Telephony.Sms.MESSAGE_TYPE_SENT,
                    Sms.STATUS_NEW, Utils.convertToTime(timestamp), timestamp))
            ).execute();
        }

        smsManager.sendTextMessage(
                phoneNumber,null,
                message,null,null
        );

        Toast.makeText(ctx,"SMS an " + phoneNumber + " gesendet", Toast.LENGTH_LONG).show();

        stateViewModel.insert(update);
    }
}
