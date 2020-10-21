package de.bikebean.app.ui.utils.sms.send;

import android.app.Dialog;
import android.telephony.SmsManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import de.bikebean.app.MainActivity;
import de.bikebean.app.ui.drawer.log.LogViewModel;
import de.bikebean.app.db.sms.Sms;
import de.bikebean.app.db.state.State;
import de.bikebean.app.ui.utils.permissions.PermissionUtils;
import de.bikebean.app.ui.utils.preferences.PreferencesUtils;

public class SmsSender {

    private final @NonNull AppCompatActivity act;
    private final @NonNull PostSmsSendHandler postSmsSendHandler;

    private final @NonNull String address;
    private final @NonNull Sms.MESSAGE message;
    private final @NonNull State[] updates;

    public SmsSender(final @NonNull AppCompatActivity act,
                     LogViewModel lv,
                     final @NonNull PostSmsSendHandler postSmsSendHandler,
                     final @NonNull Sms.MESSAGE message,
                     final @NonNull State[] updates) {
        this.act = act;
        this.postSmsSendHandler = postSmsSendHandler;

        final @Nullable String number = PreferencesUtils.getBikeBeanNumber(act, lv);

        if (number != null)
            this.address = number;
        else {
            this.address = "";
            this.message = Sms.MESSAGE._STATUS;
            this.updates = new State[]{};
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
        MainActivity.permissionGrantedHandler = this::reallySend;

        if (PermissionUtils.hasSmsPermissions(act)) {
            MainActivity.permissionDeniedHandler.continueWithoutPermission(false);
            MainActivity.permissionGrantedHandler.continueWithPermission(0);
        } else
            cancelSend();
    }

    private void reallySend(final int dummy) {
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

    public interface PostSmsSendHandler {
        void onPostSend(boolean sent, final @NonNull SmsSender smsSender);
    }
}
