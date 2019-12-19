package de.bikebean.app.ui.status.sms.parser;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.gson.Gson;

import java.util.ArrayList;

import de.bikebean.app.MainActivity;
import de.bikebean.app.ui.status.StatusFragment;

public class SmsParser {

    private final Gson gson = new Gson();

    public String parseSMS(@NonNull String smsText1, String smsText2){
        LocationAPIBody locationAPIBody = new LocationAPIBody();

        String[] stringArrayWapp1 = smsText1.split("\n");
        StatusFragment.setNumberOfWifiaccesspoints(stringArrayWapp1.length-2);

        for (String s : stringArrayWapp1) {
            // Länge des Substrings ist Unterscheidungskriterium
            switch (s.length()) {
                case 0: // Fall: Leerzeile (Substring = 0 Zeichen lang)
                    break;
                case 2: // Fall: Akkustand (Substring = 2 Zeichen lang)
                    StatusFragment.setBatteryStatus(Integer.parseInt(s));
                    break;
                case 14: //Fall: WifiAccessPoint ( Substring = 14 Zeichen lang)
                    WifiAccessPoint wap = new WifiAccessPoint();
                    wap.macAddress = s.substring(2);
                    wap.signalStrength = Integer.parseInt("-" + s.substring(0, 2));

                    locationAPIBody.wifiAccessPoints.add(wap);
                    break;
            }
        }

        String[] stringArrayWapp2 = smsText2.split("\n");

        StatusFragment.setNumberOfCelltowers(stringArrayWapp2.length);

        for (String s : stringArrayWapp2) {
            String[] stringArray_gsm_towers = s.split(",");
            CellTower c = new CellTower();

            c.mobileCountryCode = Integer.parseInt(stringArray_gsm_towers[0]);
            c.mobileNetworkCode = Integer.parseInt(stringArray_gsm_towers[1]);
            c.locationAreaCode = Integer.parseInt(stringArray_gsm_towers[2], 16);
            c.cellId = Integer.parseInt(stringArray_gsm_towers[3], 16);
            c.signalStrength = Integer.parseInt("-" + stringArray_gsm_towers[4]);

            locationAPIBody.cellTowers.add(c);
        }

        Log.d(MainActivity.TAG, "batteryStatus: " + StatusFragment.getBatteryStatus());
        Log.d(MainActivity.TAG, "WifiAccessPoints_JsonArray: " + gson.toJson(locationAPIBody.wifiAccessPoints));
        Log.d(MainActivity.TAG, "cellTowers_JsonArray: " + gson.toJson(locationAPIBody.cellTowers));

        // Create final json string
        return gson.toJson(locationAPIBody);
    }
}

class WifiAccessPoint {
    String macAddress;
    Integer signalStrength;
}


class CellTower {
    Integer mobileCountryCode;
    Integer mobileNetworkCode;
    Integer locationAreaCode;
    Integer cellId;
    Integer signalStrength;
}

class LocationAPIBody {
    ArrayList<CellTower> cellTowers;
    ArrayList<WifiAccessPoint> wifiAccessPoints;

    LocationAPIBody() {
        cellTowers = new ArrayList<>();
        wifiAccessPoints = new ArrayList<>();
    }
}
