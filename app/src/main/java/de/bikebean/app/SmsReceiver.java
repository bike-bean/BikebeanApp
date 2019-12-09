package de.bikebean.app;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.Telephony;
import android.telephony.SmsMessage;
import android.util.Log;

import de.bikebean.app.ui.SMS_commands.StatusFragment;


public class SmsReceiver extends BroadcastReceiver {

//    public originated_adress(){
//
//
//    }
    //interface
    private static SmsListener mListener;

    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle data  = intent.getExtras();

        Object[] pdus = (Object[]) data.get("pdus");

        for(int i=0;i<pdus.length;i++){
            SmsMessage smsMessage = SmsMessage.createFromPdu((byte[]) pdus[i]);

            String sender = smsMessage.getOriginatingAddress();
            //Check the sender to filter messages which we require to read
            Log.d(MainActivity.TAG, sender);


            //TODO: Testnummer einfügen
            if (sender.equals("TESTNUMMER"))
            {

                String messageBody = smsMessage.getMessageBody();

                //Pass the message text to interface
                mListener.messageReceived(messageBody);

            }
        }

    }

    public static void bindListener(SmsListener listener) {
        mListener = listener;
    }
}
















//        //SMS-Listener 1. Versuch
///**
// * A broadcast receiver who listens for incoming SMS
// */
//
//public class SmsReceiver extends BroadcastReceiver {
//
//    private static final String TAG = "SmsReceiver";
//
//    private final String serviceProviderNumber;
////    private final String serviceProviderSmsCondition;
//
//    private Listener listener;
//
////    public SmsReceiver(String serviceProviderNumber, String serviceProviderSmsCondition) {
//    public SmsReceiver(String serviceProviderNumber) {
//        this.serviceProviderNumber = serviceProviderNumber;
////        this.serviceProviderSmsCondition = serviceProviderSmsCondition;
//    }
//
//    @Override
//    public void onReceive(Context context, Intent intent) {
//        if (intent.getAction().equals(Telephony.Sms.Intents.SMS_RECEIVED_ACTION)) {
//            String smsSender = "";
//            String smsBody = "";
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//                for (SmsMessage smsMessage : Telephony.Sms.Intents.getMessagesFromIntent(intent)) {
//                    smsSender = smsMessage.getDisplayOriginatingAddress();
//                    smsBody += smsMessage.getMessageBody();
//                }
//            } else {
//                Bundle smsBundle = intent.getExtras();
//                if (smsBundle != null) {
//                    Object[] pdus = (Object[]) smsBundle.get("pdus");
//                    if (pdus == null) {
//                        // Display some error to the user
//                        Log.d(TAG, "SmsBundle had no pdus key");
//                        return;
//                    }
//                    SmsMessage[] messages = new SmsMessage[pdus.length];
//                    for (int i = 0; i < messages.length; i++) {
//                        messages[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
//                        smsBody += messages[i].getMessageBody();
//                    }
//                    smsSender = messages[0].getOriginatingAddress();
//                }
//            }
//
////            if (smsSender.equals(serviceProviderNumber) && smsBody.startsWith(serviceProviderSmsCondition)) {
//            if (smsSender.equals(serviceProviderNumber)) {
//                if (listener != null) {
//                    listener.onTextReceived(smsBody);
//                }
//            }
//        }
//    }
//
//    void setListener(Listener listener) {
//        this.listener = listener;
//    }
//
//    interface Listener {
//        void onTextReceived(String text);
//    }
//}