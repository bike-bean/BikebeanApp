package de.bikebean.app;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = "BIKE";

    public static final String OTP_REGEX = "[0-9]{1,6}";
//    private SmsReceiver smsBroadcastReceiver;

    String text = "123";

//    // POST Request API
//    public static RequestQueue queue;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_status, R.id.navigation_map, R.id.navigation_wifi)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);


        //SMS-Listener 2. Versuch
        SmsReceiver.bindListener(new SmsListener() {
                                     @Override
                                     public void messageReceived(String messageText) {

                                         //From the received text string you may do string operations to get the required OTP
                                         //It depends on your SMS format
                                         Log.d(TAG, "Messages: "+ messageText);
                                         Toast.makeText(MainActivity.this, "Message: " + messageText, Toast.LENGTH_LONG).show();

                                         // If your OTP is six digits number, you may use the below code

//                                         Pattern pattern = Pattern.compile(OTP_REGEX);
//                                         Matcher matcher = pattern.matcher(messageText);
//                                         String otp;
//                                         while (matcher.find()) {
//                                             otp = matcher.group();
//                                         }
//
//                                         Toast.makeText(MainActivity.this, "OTP: " + otp, Toast.LENGTH_LONG).show();

                                     }
                                 });



//        //SMS-Listener 1. Versuch
//        String hallo;
//
//        smsBroadcastReceiver = new SmsReceiver(StatusFragment.getNumber_bike());
//        registerReceiver(smsBroadcastReceiver, new IntentFilter(Telephony.Sms.Intents.SMS_RECEIVED_ACTION));
//
//        smsBroadcastReceiver.setListener(new SmsReceiver.Listener({
//        @Override
//        public void onTextReceived (String text){
//            // Do stuff with received text!
//            hallo = text;
//
//        }
//        });
    }
}
