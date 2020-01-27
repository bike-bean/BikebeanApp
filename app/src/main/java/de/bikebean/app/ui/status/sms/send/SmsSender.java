package de.bikebean.app.ui.status.sms.send;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.provider.Telephony;
import android.telephony.SmsManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import de.bikebean.app.Utils;
import de.bikebean.app.db.sms.Sms;
import de.bikebean.app.ui.status.StatusFragment;
import de.bikebean.app.ui.status.sms.SmsViewModel;

public class SmsSender extends StatusFragment {

    private static final int MY_PERMISSIONS_REQUEST_SEND_SMS = 0;

    private final Context ctx;
    private final FragmentActivity act;

    private SmsSendWarningDialogFragment smsSendWarningDialogFragment;

    public SmsSender(Context ctx, FragmentActivity act) {
        this.ctx = ctx;
        this.act = act;

        smsSendWarningDialogFragment = new SmsSendWarningDialogFragment();
        smsSendWarningDialogFragment.setSmsSender(this);
    }

    private SmsViewModel smsViewModel;
    private String message;
    private String phoneNumber;

    public void send(String phoneNumber, String message, SmsViewModel smsViewModel) {
        if (phoneNumber.isEmpty()) {
            Toast.makeText(ctx, "Keine Nummer gespeichert!", Toast.LENGTH_LONG).show();
            return;
        }

        this.phoneNumber = phoneNumber;
        this.message = message;
        this.smsViewModel = smsViewModel;

        prepareSend();
    }

    private void prepareSend() {
        // TODO: Add Utils function to handle that permission stuff (at least check if present)
        if (ContextCompat.checkSelfPermission(ctx, android.Manifest.permission.SEND_SMS)
                != PackageManager.PERMISSION_GRANTED) {

            //Send-SMS permission is NOT granted
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(act, android.Manifest.permission.SEND_SMS)) {
                assert true;
                //gerade keine Erklärung, warum man die Permission braucht, vorhanden

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {
                // No explanation needed; request the permission
                ActivityCompat.requestPermissions(act, new String[]{Manifest.permission.SEND_SMS}, MY_PERMISSIONS_REQUEST_SEND_SMS);
                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        } else {
            // Permission has already been granted

            smsSendWarningDialogFragment.show(act.getSupportFragmentManager(), "smsWarning");
        }
    }

    void cancelSend() {
        /*
        The user decided to cancel, don't send an SMS and signal it to the user.
        */
        Toast.makeText(ctx,"Vorgang abgebrochen.",
                Toast.LENGTH_LONG).show();
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

        smsManager.sendTextMessage(phoneNumber,null,
                message,null,null);

        Toast.makeText(ctx,"SMS an " + phoneNumber + " gesendet",
                Toast.LENGTH_LONG).show();
    }

    //Handle the permissions request response
    @Override
    public void onRequestPermissionsResult(
            int requestCode,
            @NonNull String[] permissions,
            @NonNull int[] grantResults) {
        if (requestCode == MY_PERMISSIONS_REQUEST_SEND_SMS) {// If request is cancelled, the result arrays are empty.
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // permission was granted, yay! Do the
                // contacts-related task you need to do.

                //Nur nach Akzeptieren des Requests wird eine Nachricht verschickt, SCHEISS CODE
                //Hier sollte nur das stehen, das erstmalig nach Permission-Erteilung notwendig ist

                //TODO: Man könnte das hier doch wieder auskommentieren (und sicherstellen, dass es wie der Code in sensSMSMessage() aussieht
//                    SmsManager smsManager = SmsManager.getDefault();
//                    smsManager.sendTextMessage(phoneNo, null, message, null, null);
//                    Toast.makeText(ctx, "SMS sent.",
//                            Toast.LENGTH_LONG).show();

                assert true;
            } else {
                // permission denied, boo! Disable the
                // functionality that depends on this permission.
//                    Toast.makeText(ctx,
//                            "SMS failed, please try again.", Toast.LENGTH_LONG).show();
                return;
            }
            // other 'case' lines to check for other
            // permissions this app might request.
            assert true;
        }
    }
}
