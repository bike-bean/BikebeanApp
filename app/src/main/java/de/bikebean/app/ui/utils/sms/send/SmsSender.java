package de.bikebean.app.ui.utils.sms.send;

import android.app.Dialog;
import android.content.SharedPreferences;
import android.telephony.SmsManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;
import androidx.preference.PreferenceManager;

import de.bikebean.app.ui.main.status.menu.log.LogViewModel;
import de.bikebean.app.ui.utils.Utils;
import de.bikebean.app.db.sms.Sms;
import de.bikebean.app.db.state.State;
import de.bikebean.app.ui.utils.PermissionsRationaleDialog;
import de.bikebean.app.ui.main.status.StatusFragment;

public class SmsSender {

    public interface PostSmsSendHandler {
        void onPostSend(boolean sent, final @NonNull SmsSender smsSender);
    }

    private final @NonNull FragmentActivity act;
    private final @NonNull PostSmsSendHandler postSmsSendHandler;

    private final @NonNull String address;
    private final @NonNull Sms.MESSAGE message;
    private final @NonNull State[] updates;

    public SmsSender(final @NonNull FragmentActivity act,
                     LogViewModel lv,
                     final @NonNull PostSmsSendHandler postSmsSendHandler,
                     final @NonNull Sms.MESSAGE message,
                     final @NonNull State[] updates) {
        this.act = act;
        this.postSmsSendHandler = postSmsSendHandler;

        final @Nullable SharedPreferences sharedPreferences =
                PreferenceManager.getDefaultSharedPreferences(act);
        final @Nullable String number;

        if (sharedPreferences != null)
            number = sharedPreferences.getString("number", null);
        else {
            lv.e("Failed to get sharedPreferences!");
            this.address = "";
            this.message = Sms.MESSAGE._STATUS;
            this.updates = new State[]{};
            return;
        }

        if (number != null)
            this.address = number;
        else {
            this.address = "";
            this.message = Sms.MESSAGE._STATUS;
            this.updates = new State[]{};
            lv.e("Failed to load BB-number! Maybe it's not set?");
            return;
        }

        this.message = message;
        this.updates = updates;
    }

    public @NonNull String getAddress() {
        return address;
    }

    public @NonNull Sms.MESSAGE getMessage() {
        return message;
    }

    public @NonNull State[] getUpdates() {
        return updates;
    }

    public void send() {
        if (address.isEmpty()) {
            cancelSend();
            return;
        }

        final @NonNull SmsSendWarnDialog smsSendWarnDialog =
                new SmsSendWarnDialog(act, this);

        final @Nullable Dialog dialog;
        final @Nullable Dialog tmpDialog;

        tmpDialog = smsSendWarnDialog.getDialog();
        if (tmpDialog == null)
            dialog = smsSendWarnDialog.onCreateDialog(null);
        else dialog = tmpDialog;

        if (dialog.isShowing()) {
            cancelSend();
            return;
        }

        smsSendWarnDialog.show(act.getSupportFragmentManager(), "smsWarning");
    }

    public void getPermissions() {
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
        final @Nullable SmsManager smsManager = SmsManager.getDefault();

        if (smsManager != null)
            smsManager.sendTextMessage(
                    address,null, message.getMsg(),
                    null,null
            );

        postSmsSendHandler.onPostSend(true, this);
    }

    public void cancelSend() {
        /*
        The user decided to cancel, don't send an SMS and signal it to the user.
        */
        postSmsSendHandler.onPostSend(false, this);
    }
}
