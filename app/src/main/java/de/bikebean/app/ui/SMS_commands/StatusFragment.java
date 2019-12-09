package de.bikebean.app.ui.SMS_commands;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import de.bikebean.app.BuildConfig;
import de.bikebean.app.MainActivity;
import de.bikebean.app.R;
import de.bikebean.app.SmsReceiver;

public class StatusFragment extends Fragment implements View.OnClickListener {

    private StatusViewModel statusViewModel;

    private static Integer Number_of_celltowers;
    private static Integer Number_of_wifiaccesspoints;
    private static JSONObject server_response_LatLngAccuracy;
    int battery_status;
    JSONArray cellTowers = new JSONArray();
    JSONArray wifiAccessPoints = new JSONArray();
    Map<String, String> cellTowers_string_map = new HashMap<String, String>();
    Map<String, String> wifiAccessPoints_string_map = new HashMap<String,String>();
    RequestQueue queue;

    String phoneNo;
    String message = "wapp";


    //Testgeräte:
    static String Telefonnummer1 = BuildConfig.TELEFONNUMMER1;
    static String Telefonnummer2 = BuildConfig.TELEFONNUMMER2;

    static String Number_bike = Telefonnummer1;
    static String Bike_name = "Radname";




    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState)


    //Veränderliche Textview: Status über die letzten abgefragten Standortdaten
    {
        statusViewModel = ViewModelProviders.of(this).get(StatusViewModel.class);
        View root = inflater.inflate(R.layout.fragment_status, container, false);


        //Andere Livedata Textviews
        final TextView text_oben = root.findViewById(R.id.text_status);
        statusViewModel.getText().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                text_oben.setText(s);
            }
        });



        //Button SMS senden (innerhalb onCreateView)
        final Button button_get_location = (Button) root.findViewById(R.id.button_get_location);
        button_get_location.setOnClickListener(this);



        // POST Request API #2
        queue = Volley.newRequestQueue(getActivity().getApplicationContext());


        check_for_new_coordinates();




        String GoogleMapsAPIKey = BuildConfig.GOOGLEMAPSAPIKEY;
        final String url_geolocationAPI = "https://www.googleapis.com/geolocation/v1/geolocate?key=" + GoogleMapsAPIKey;
        httpPOST(url_geolocationAPI);

        return root;
    }




    //Button SMS senden (außerhalb onCreateView)
    @Override
    public void onClick(View v) {
        Log.d(MainActivity.TAG, "Button gedrückt");
        sendSMSMessage();

    }



    //Dropdown (1,4,8,12,24) & Button zum Kommando senden (Intervall um Daten zu senden)

    //Button Warningnumber?

    //Button Wifi On / Wifi Off



    //SMS-Parsen (String to JSON)
    public void check_for_new_coordinates (){

        //  [rssi (signal strength) MINUS DAZUBASTELN (2 Ziffern), Mac-Adresse DOPPELPUNKTE DAZUBASTELN (12 Ziffern/Buchstaben)] Akkustand
//        String wapp1_bsp ="90788102493fd4\n" +
//                "66946ab01746ac\n" +
//                "801062e5b58896\n" +
//                "715e904350f67d\n" +
//                "\n" +
//                "81\n";

        String wapp1_bsp ="87788102493fd4\n" +
                "66946ab01746ac\n" +
                "801062e5b58896\n" +
                "7058904350f67d\n" +
                "\n" +
                "80\n";



        String[] stringarray_wapp1 = wapp1_bsp.split("\n");
        setNumber_of_wifiaccesspoints(stringarray_wapp1.length-2);

        for(int i = 0; i < stringarray_wapp1.length; i++){

            //LÄnge des Substrings ist Unterscheidungskriterium
            switch(stringarray_wapp1[i].length()){

                //Fall: Leerzeile (Substring = 0 Zeichen lang)
                case 0: break;


                //Fall: Akkustand (Substring = 2 Zeichen lang)
                case 2: battery_status = Integer.parseInt(stringarray_wapp1[i]); break;


                //Fall: WifiAccessPoint ( Substring = 14 Zeichen lang)
                case 14:

                    wifiAccessPoints_string_map.put("signalStrength", "-"+stringarray_wapp1[i].substring(0, 2));
                    wifiAccessPoints_string_map.put("macAddress", stringarray_wapp1[i].substring(2, stringarray_wapp1[i].length()));

                    JSONObject wifiAccessPoint_x = new JSONObject(wifiAccessPoints_string_map);

                    try {
                        wapp1_string_to_int(wifiAccessPoint_x);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    wifiAccessPoints.put(wifiAccessPoint_x);
                    break;
            }
//            Log.d(MainActivity.TAG, "WifiAccessPointsSMS-Zeile "+i+ ": "+stringarray_wapp1[i]);
        }

        Log.d(MainActivity.TAG, "battery_status: "+battery_status);
        Log.d(MainActivity.TAG, "WifiAccessPoints_JsonArray: "+wifiAccessPoints.toString());


        //  [mcc (3 Ziffern), mnc (2 Ziffern), lac (hexadezimal, string 1-4 (vielleicht5?!) Zeichen), cellid (hexadezimal, string 1-4 Zeichen), rxl (2 Ziffern)] ZEILENUMBRUCH ... Akkustand 2-3 Ziffern ... ZEILENUMBRUCH
//        String wapp2_bsp = "262,03,55f1,a473,36\n" +
//                "262,03,55f1,5653,20\n" +
//                "262,03,55f1,4400,20\n" +
//                "262,03,55f1,8b40,11\n" +
//                "262,03,55f1,6bb2,10\n" +
//                "262,03,55f1,0833,10\n" +
//                "262,03,55f1,efb4,09\n";

        String wapp2_bsp = "262,03,55f1,a473,36\n" +
                "262,03,55f1,5653,21\n" +
                "262,03,55f1,4400,20\n" +
                "262,03,55f1,8b40,12\n" +
                "262,03,55f1,6bb2,10\n" +
                "262,03,55f1,0833,09\n" +
                "262,03,55f1,6bcd,03\n";




        String[] stringarray_wapp2 = wapp2_bsp.split("\n");
        setNumber_of_celltowers(stringarray_wapp2.length);

        for(int i = 0; i < stringarray_wapp2.length; i++){

            String[] stringarray_gsm_towers = stringarray_wapp2[i].split(",");

            for(int j = 0; j < stringarray_gsm_towers.length; j++){

                switch(j){
                    case 0: cellTowers_string_map.put("mobileCountryCode", stringarray_gsm_towers[j]); break;
                    case 1: cellTowers_string_map.put("mobileNetworkCode", stringarray_gsm_towers[j]); break;
                    case 2: cellTowers_string_map.put("locationAreaCode", String.valueOf(Integer.parseInt(stringarray_gsm_towers[j],16))); break;
                    case 3: cellTowers_string_map.put("cellId", String.valueOf(Integer.parseInt(stringarray_gsm_towers[j],16))); break;
                    case 4: cellTowers_string_map.put("signalStrength", "-"+stringarray_gsm_towers[j]); break;

                    default: break;
                }
            }

            JSONObject cellTower_x = new JSONObject(cellTowers_string_map);

            try {
                wapp2_string_to_int(cellTower_x);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            cellTowers.put(cellTower_x);

//            Log.d(MainActivity.TAG, "GSMTower SMS-Zeile "+i+ ": "+stringarray_wapp2[i]);

        }

        Log.d(MainActivity.TAG, "cellTowers_JsonArray: "+cellTowers.toString());
    }
    private static void wapp1_string_to_int (JSONObject json) throws JSONException {
        String signalStrength = (String) json.remove("signalStrength");
        if (signalStrength != null) {
            json.put("signalStrength", Integer.parseInt(signalStrength));
        }
    }
    private static void wapp2_string_to_int (JSONObject json) throws JSONException {

        String cellId = (String) json.remove("cellId");
        if (cellId != null) {
            json.put("cellId", Integer.parseInt(cellId));
        }

        String locationAreaCode = (String) json.remove("locationAreaCode");
        if (locationAreaCode != null) {
            json.put("locationAreaCode", Integer.parseInt(locationAreaCode));
        }

        String mobileCountryCode = (String) json.remove("mobileCountryCode");
        if (mobileCountryCode != null) {
            json.put("mobileCountryCode", Integer.parseInt(mobileCountryCode));
        }

        String mobileNetworkCode = (String) json.remove("mobileNetworkCode");
        if (mobileNetworkCode != null) {
            json.put("mobileNetworkCode", Integer.parseInt(mobileNetworkCode));
        }

        String signalStrength = (String) json.remove("signalStrength");
        if (signalStrength != null) {
            json.put("signalStrength", Integer.parseInt(signalStrength));
        }
    }


    public void setNumber_of_celltowers(Integer number_of_celltowers){
        Number_of_celltowers = number_of_celltowers;
    }
    public static Integer getNumber_of_celltowers(){
        return Number_of_celltowers;
    }


    public void setNumber_of_wifiaccesspoints(Integer number_of_wifiaccesspoints){
        Number_of_wifiaccesspoints = number_of_wifiaccesspoints;
    }
    public static Integer getNumber_of_wifiaccesspoints(){
        return Number_of_wifiaccesspoints;
    }


    //POST Request API #3
    public void httpPOST(final String url) {

        //TODO: Eigene Nummern(n) hinzufügen
        //TODO: SMS von eingegebener Nummer lesen
        //TODO: SMS-Parser schreiben
        //TODO: SMS-Befehle senden


//        JSONObject wifi_access_point_1 = new JSONObject();
//        try {
//            wifi_access_point_1.put("macAddress", "00:25:9c:cf:1c:ac");
//            wifi_access_point_1.put("signalStrength", -43);
//            wifi_access_point_1.put("signalToNoiseRatio", 0);
//        } catch (JSONException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        }
//
//        JSONObject wifi_access_point_2 = new JSONObject();
//        try {
//            wifi_access_point_2.put("macAddress", "00:25:9c:cf:1c:ad");
//            wifi_access_point_2.put("signalStrength", -55);
//            wifi_access_point_2.put("signalToNoiseRatio", 0);
//        } catch (JSONException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        }
//
//        JSONArray wifi_access_points = new JSONArray();
//
//        wifi_access_points.put(wifi_access_point_1);
//        wifi_access_points.put(wifi_access_point_2);
//        final String wifi_access_points_string = wifi_access_points.toString();

//        final JSONObject requestBody_json = new JSONObject();
//        try {
//            requestBody_json.put("considerIp", "false");
////            requestBody_json.put("wifiAccessPoints", wifi_access_points);
//            requestBody_json.put("wifiAccessPoints", wifi_access_points);
//
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }

        final JSONObject requestBody_json = new JSONObject();
        try {
            requestBody_json.put("cellTowers", cellTowers);
            requestBody_json.put("wifiAccessPoints", wifiAccessPoints);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        final String requestBody = requestBody_json.toString();


//        JsonObjectRequest postRequest = new JsonObjectRequest(Request.Method.POST, url, requestBody_json,

                JsonObjectRequest postRequest = new JsonObjectRequest(Request.Method.POST, url, null,
                new Response.Listener<JSONObject>()
//        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
//                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(JSONObject response)
//                    public void onResponse(String response)
                    {
                        Log.d(MainActivity.TAG, "RESPONSE FROM SERVER: " + response.toString());
                        setCurrent_position_bike_coordinates(response);
                    }

                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        // error
                        Log.d(MainActivity.TAG, "Error.Response: " + error.getMessage());
                    }
                }

        ) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<String, String>();
//                headers.put("Accept", "application/json");
//                headers.put("Content-Type", "application/json; charset=utf-8");
                headers.put("Content-Type", "application/json");

                return headers;
            }

//            @Override
//            protected Map<String, String> getParams()
//            {
//
//                Map<String, String>  params = new HashMap<String, String>();
//                params.put("cellTowers", cellTowers.toString());
//                params.put("wifiAccessPoints", wifiAccessPoints.toString());
//
//                return params;
//            }

            @Override
            public byte[] getBody() {
                try {
                    // Wenn die BEDINGUNG requestBody == null wahr ist, wird null ausgeführt. Ansonsten wird requestBody.getBytes("utf-8") ausgeführt.
                    return requestBody == null ? null : requestBody.getBytes("utf-8");
                } catch (UnsupportedEncodingException uee) {
                    VolleyLog.wtf(MainActivity.TAG, "Unsupported Encoding while trying to get the bytes of %s using %s",
                            requestBody, "utf-8");
                    return null;
                }
            }

        };

        queue.add(postRequest);

    }


    public void setCurrent_position_bike_coordinates(JSONObject server_response) {
        server_response_LatLngAccuracy = server_response;
    }
    //TODO: JSONException abfangen?
    public static Double getCurrent_position_Lat() throws JSONException {

        JSONObject location = server_response_LatLngAccuracy.getJSONObject("location");
        Double Lat = location.getDouble("lat");
        return Lat;
    }
    public static Double getCurrent_position_Lng() throws JSONException {

        JSONObject location = server_response_LatLngAccuracy.getJSONObject("location");
        Double Lng = location.getDouble("lng");
        return Lng;
    }
    public static Double getCurrent_position_Accuracy() throws JSONException {

        Double Accuracy = server_response_LatLngAccuracy.getDouble("accuracy");
        return Accuracy;
    }






    //SMS SENDEN

    public void setNumber_bike(String number_bike){
        Number_bike = number_bike;
    }
    public static String getNumber_bike(){
        return Number_bike;
    }

    public void setBike_name(String bike_name){
        Bike_name = bike_name;
    }
    public static String getBike_name(){
        return Bike_name;
    }


    private static final int MY_PERMISSIONS_REQUEST_SEND_SMS =0 ;

        protected void sendSMSMessage() {

        //TODO: individuell einstellen können
        phoneNo = Number_bike;

        if (ContextCompat.checkSelfPermission(getActivity().getApplicationContext(), android.Manifest.permission.SEND_SMS)
                != PackageManager.PERMISSION_GRANTED) {

            //Send-SMS permission is NOT granted
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                    android.Manifest.permission.SEND_SMS)) {

                //gerade keine Erklärung, warum man die Permission braucht, vorhanden

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {
                // No explanation needed; request the permission
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.SEND_SMS}, MY_PERMISSIONS_REQUEST_SEND_SMS);
                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        } else{
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(phoneNo, null, message, null, null);
            Toast.makeText(getActivity().getApplicationContext(), "SMS an "+Number_bike+" gesendet",
                    Toast.LENGTH_LONG).show();

            // Permission has already been granted
        }
    }

    //Handle the permissions request response
    @Override
    public void onRequestPermissionsResult(int requestCode,String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_SEND_SMS: {
                // If request is cancelled, the result arrays are empty.
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



                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
//                    Toast.makeText(getApplicationContext(),
//                            "SMS failed, please try again.", Toast.LENGTH_LONG).show();

                    return;
                }
            }
            // other 'case' lines to check for other
            // permissions this app might request.
            //TODO: Welche Permissions werden noch gebraucht?
            //TODO: Standort für WLAN, SMS empfangen, SMS Listener (?!)
        }

    }


}