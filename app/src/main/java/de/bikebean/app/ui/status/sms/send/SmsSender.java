package de.bikebean.app.ui.status.sms.send;

import android.app.Dialog;
import android.content.SharedPreferences;
import android.telephony.SmsManager;

import androidx.fragment.app.FragmentActivity;
import androidx.preference.PreferenceManager;

import de.bikebean.app.Utils;
import de.bikebean.app.db.sms.Sms;
import de.bikebean.app.db.state.State;
import de.bikebean.app.ui.status.PermissionsRationaleDialog;
import de.bikebean.app.ui.status.StatusFragment;

public class SmsSender {

    public interface PostSmsSendHandler {
        void onPostSend(boolean sent, String phoneNumber, String message, State[] updates);
    }

    private final FragmentActivity act;
    private final SharedPreferences sharedPreferences;
    private PostSmsSendHandler postSmsSendHandler;

    public SmsSender(FragmentActivity act, PostSmsSendHandler postSmsSendHandler) {
        this.act = act;
        this.postSmsSendHandler = postSmsSendHandler;

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(act);
    }

    private String address;
    private String message;
    private State[] updates;

    public void send(Sms.MESSAGE message, State[] updates) {
        this.address = sharedPreferences.getString("number","");
        this.message = message.getMsg();
        this.updates = updates;

        if (address.isEmpty()) {
            cancelSend();
            return;
        }

        SmsSendWarnDialog smsSendWarnDialog =
                new SmsSendWarnDialog(act, message, this::getPermissions, this::cancelSend);
        Dialog dialog = smsSendWarnDialog.getDialog();

        if (dialog == null)
            dialog = smsSendWarnDialog.onCreateDialog(null);

        if (dialog.isShowing()) {
            cancelSend();
            return;
        }

        smsSendWarnDialog.show(act.getSupportFragmentManager(), "smsWarning");
    }

    private void getPermissions() {
        StatusFragment.permissionGrantedHandler = this::reallySend;

        if (Utils.getPermissions(act, Utils.PERMISSION_KEY.SMS, () ->
                new PermissionsRationaleDialog(act, Utils.PERMISSION_KEY.SMS).show(
                        act.getSupportFragmentManager(),
                        "smsRationaleDialog"
                )
        )) {
            StatusFragment.permissionDeniedHandler.continueWithoutPermission(false);
            StatusFragment.permissionGrantedHandler.continueWithPermission();
        } else
            cancelSend();
    }

    private void reallySend() {
        /*
        The user decided to send the SMS, so actually send it!
        */
        SmsManager smsManager = SmsManager.getDefault();
        smsManager.sendTextMessage(address,null, message,null,null);

        postSmsSendHandler.onPostSend(true, address, message, updates);
    }

    private void cancelSend() {
        /*
        The user decided to cancel, don't send an SMS and signal it to the user.
        */
        postSmsSendHandler.onPostSend(false, address, message, updates);
    }
}
