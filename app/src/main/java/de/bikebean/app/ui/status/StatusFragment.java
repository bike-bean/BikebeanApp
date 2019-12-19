package de.bikebean.app.ui.status;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.preference.PreferenceManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Objects;

import de.bikebean.app.MainActivity;
import de.bikebean.app.R;
import de.bikebean.app.ui.status.geolocationapi.GeolocationAPI;
import de.bikebean.app.ui.status.settings.SettingsActivity;
import de.bikebean.app.ui.status.sms.SmsActivity;
import de.bikebean.app.ui.status.sms.parser.SmsParser;
import de.bikebean.app.ui.status.sms.send.SmsSender;

public class StatusFragment extends Fragment {

    private static JSONObject serverResponseLatLngAccuracy;

    // Status
    private static Integer numberOfCelltowers;
    private static Integer numberOfWifiaccesspoints;
    private static Integer batteryStatus;

    // private static String Bike_name = "Radname";

    protected Context ctx;
    protected FragmentActivity act;

    // Ui Elements
    private Button buttonCreateSmsView;
    private Button buttonAdditionalSettings;
    private Button buttonGetLocation;
    private TextView textOben;
    //Todo: Button für Statusabfrage
    // + Statusinformationen parsen und darstellen

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState)
    {
        // Veränderliche Textview: Status über die letzten abgefragten Standortdaten
        StatusViewModel statusViewModel = ViewModelProviders.of(this).get(StatusViewModel.class);
        View root = inflater.inflate(R.layout.fragment_status, container, false);

        // UI Elements
        textOben = root.findViewById(R.id.text_status);
        buttonAdditionalSettings = root.findViewById(R.id.button_additional_settings);
        buttonGetLocation = root.findViewById(R.id.button_get_location);
        buttonCreateSmsView = root.findViewById(R.id.sms_button);

        statusViewModel.getText().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textOben.setText(s);
            }
        });

        return root;
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // Get Activity and Context
        ctx = Objects.requireNonNull(getActivity()).getApplicationContext();
        act = Objects.requireNonNull(getActivity());

        final SmsSender smsSender = new SmsSender(ctx, act);
        final SmsParser smsParser = new SmsParser();
        GeolocationAPI geolocationAPI = new GeolocationAPI(ctx);

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(ctx);
        final String numberBike = sharedPreferences.getString("number", "");

        // Finalize UI Elements
        buttonCreateSmsView.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(ctx, SmsActivity.class);
                act.startActivity(intent);
            }
        });

        buttonGetLocation.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Log.d(MainActivity.TAG, "Button gedrückt");
                smsSender.send(numberBike, "wapp");
            }
        });

        buttonAdditionalSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ctx, SettingsActivity.class);
                act.startActivity(intent);
            }
        });

        //  [rssi (signal strength) MINUS DAZUBASTELN (2 Ziffern),
        //  Mac-Adresse DOPPELPUNKTE DAZUBASTELN (12 Ziffern/Buchstaben)]
        //  Akkustand
//        String wappBsp1 ="90788102493fd4\n" +
//                "66946ab01746ac\n" +
//                "801062e5b58896\n" +
//                "715e904350f67d\n" +
//                "\n" +
//                "81\n";

        String wappBsp1 =
                "87788102493fd4\n" +
                "66946ab01746ac\n" +
                "801062e5b58896\n" +
                "7058904350f67d\n" +
                "\n" +
                "80\n";

        //  [mcc (3 Ziffern), mnc (2 Ziffern),
        //  lac (hexadezimal, string 1-4 (vielleicht5?!) Zeichen),
        //  cellid (hexadezimal, string 1-4 Zeichen),
        //  rxl (2 Ziffern)]
        //  ZEILENUMBRUCH
        //  ...
        //  Akkustand 2-3 Ziffern
        //  ...
        //  ZEILENUMBRUCH
//        String wappBsp2 = "262,03,55f1,a473,36\n" +
//                "262,03,55f1,5653,20\n" +
//                "262,03,55f1,4400,20\n" +
//                "262,03,55f1,8b40,11\n" +
//                "262,03,55f1,6bb2,10\n" +
//                "262,03,55f1,0833,10\n" +
//                "262,03,55f1,efb4,09\n";

        String wappBsp2 = "262,03,55f1,a473,36\n" +
                "262,03,55f1,5653,21\n" +
                "262,03,55f1,4400,20\n" +
                "262,03,55f1,8b40,12\n" +
                "262,03,55f1,6bb2,10\n" +
                "262,03,55f1,0833,09\n" +
                "262,03,55f1,6bcd,03\n";

        String requestBody = smsParser.parseSMS(wappBsp1, wappBsp2);

        // POST Request API #2
        geolocationAPI.httpPOST(requestBody);
    }

    // Getters and Setters
    public static Integer getBatteryStatus() {
        return batteryStatus;
    }

    public static void setBatteryStatus(Integer batteryStatus) {
        StatusFragment.batteryStatus = batteryStatus;
    }

    public static Integer getNumberOfWifiaccesspoints(){
        return numberOfWifiaccesspoints;
    }

    public static void setNumberOfWifiaccesspoints(Integer numberOfWifiaccesspoints) {
        StatusFragment.numberOfWifiaccesspoints = numberOfWifiaccesspoints;
    }

    public static Integer getNumberOfCelltowers(){
        return numberOfCelltowers;
    }

    public static void setNumberOfCelltowers(Integer numberOfCelltowers) {
        StatusFragment.numberOfCelltowers = numberOfCelltowers;
    }

    public static void setCurrentPositionBikeCoordinates(JSONObject server_response) {
        serverResponseLatLngAccuracy = server_response;
    }

    //TODO: JSONException abfangen?
    public static Double getCurrentPositionLat() throws JSONException {
        JSONObject location = serverResponseLatLngAccuracy.getJSONObject("location");
        return location.getDouble("lat");
    }

    public static Double getCurrentPositionLng() throws JSONException {
        JSONObject location = serverResponseLatLngAccuracy.getJSONObject("location");
        return location.getDouble("lng");
    }

    public static Double getCurrentPositionAccuracy() throws JSONException {
        return serverResponseLatLngAccuracy.getDouble("accuracy");
    }
}
