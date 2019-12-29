package de.bikebean.app.ui.status.sms.send;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.telephony.SmsManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import de.bikebean.app.ui.status.StatusFragment;

public class SmsSender extends StatusFragment {

    private static final int MY_PERMISSIONS_REQUEST_SEND_SMS = 0;

    public SmsSender(Context ctx, FragmentActivity act) {
        this.ctx = ctx;
        this.act = act;
    }

    public void send(String Number_bike, String message) {
        if (Number_bike.isEmpty()) {
            Toast.makeText(ctx, "Keine Nummer gespeichert!", Toast.LENGTH_LONG).show();
            return;
        }

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
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(Number_bike, null, message, null, null);
            Toast.makeText(ctx, "SMS an " + Number_bike + " gesendet",
                    Toast.LENGTH_LONG).show();
        }
    }

    //Handle the permissions request response
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
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
//                    Toast.makeText(getApplicationContext(), "SMS sent.",
//                            Toast.LENGTH_LONG).show();

                assert true;
            } else {
                // permission denied, boo! Disable the
                // functionality that depends on this permission.
//                    Toast.makeText(getApplicationContext(),
//                            "SMS failed, please try again.", Toast.LENGTH_LONG).show();
                return;
            }
            // other 'case' lines to check for other
            // permissions this app might request.
            //TODO: Welche Permissions werden noch gebraucht?
            //TODO: Standort für WLAN, SMS empfangen, SMS Listener (?!)
            assert true;
        }
    }
}
