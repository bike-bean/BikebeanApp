package de.bikebean.app.ui.status.location;

import android.util.Log;

import com.google.gson.Gson;

import java.util.ArrayList;

import de.bikebean.app.MainActivity;

class ApiParser {

    public interface PostJsonCreateHandler {
        void onPostJsonCreate(int numberCellTowers, int numberWifiAccessPoints, int smsId);
    }

    private final PostJsonCreateHandler mPostJsonCreateHandler;
    private final LocationAPIBody locationAPIBody;

    private final Gson gson = new Gson();

    ApiParser(PostJsonCreateHandler postJsonCreateHandler) {
        mPostJsonCreateHandler = postJsonCreateHandler;
        locationAPIBody = new LocationAPIBody();
    }

    String createJsonApiBody(String cellTowers, String wifiAccessPoints, int smsId) {
        int numberWifiAccessPoints = parseWifiAccessPoints(wifiAccessPoints, locationAPIBody);
        int numberCellTowers = parseCellTowers(cellTowers, locationAPIBody);

        Log.d(MainActivity.TAG, "WifiAccessPoints_JsonArray: " + gson.toJson(locationAPIBody.wifiAccessPoints));
        Log.d(MainActivity.TAG, "numberWifiAccessPoints: " + numberWifiAccessPoints);
        Log.d(MainActivity.TAG, "cellTowers_JsonArray: " + gson.toJson(locationAPIBody.cellTowers));
        Log.d(MainActivity.TAG, "numberCellTowers: " + numberCellTowers);

        mPostJsonCreateHandler.onPostJsonCreate(numberCellTowers, numberWifiAccessPoints, smsId);

        // Create final json string
        return gson.toJson(locationAPIBody);
    }

    private int parseWifiAccessPoints(String wifiAccessPoints, LocationAPIBody locationAPIBody) {
        String[] stringArrayWapp = wifiAccessPoints.split("\n");
        int numberWifiAccessPoints = stringArrayWapp.length;

        for (String s : stringArrayWapp)
            if (!s.equals("    ")) {
                // Länge des Substrings ist Unterscheidungskriterium
                WifiAccessPoint wap = new WifiAccessPoint();
                wap.macAddress = s.substring(2);
                wap.signalStrength = Integer.parseInt("-" + s.substring(0, 2));
                wap.toMacAddress();

                locationAPIBody.wifiAccessPoints.add(wap);
            }

        return numberWifiAccessPoints;
    }

    private int parseCellTowers(String cellTowers, LocationAPIBody locationAPIBody) {
        String[] stringArrayWapp = cellTowers.split("\n");
        int numberCellTowers = stringArrayWapp.length;

        for (String s : stringArrayWapp)
            if (!s.equals("    ")) {
                String[] stringArray_gsm_towers = s.split(",");
                CellTower c = new CellTower();

                c.mobileCountryCode = Integer.parseInt(stringArray_gsm_towers[0]);
                c.mobileNetworkCode = Integer.parseInt(stringArray_gsm_towers[1]);
                c.locationAreaCode = Integer.parseInt(stringArray_gsm_towers[2], 16);
                c.cellId = Integer.parseInt(stringArray_gsm_towers[3], 16);
                c.signalStrength = Integer.parseInt("-" + stringArray_gsm_towers[4]);

                locationAPIBody.cellTowers.add(c);
            }

        return numberCellTowers;
    }

    class WifiAccessPoint {
        String macAddress;
        Integer signalStrength;

        void toMacAddress() {
            String str = macAddress;
            StringBuilder tmp = new StringBuilder();
            final int divisor = 2;

            while (str.length() > 0) {
                String nextChunk = str.substring(0, divisor);
                tmp.append(nextChunk);
                if (str.length() > 2)
                    tmp.append(":");

                str = str.substring(divisor);
            }

            macAddress = tmp.toString();
        }
    }

    class CellTower {
        Integer mobileCountryCode;
        Integer mobileNetworkCode;
        Integer locationAreaCode;
        Integer cellId;
        Integer signalStrength;
    }

    class LocationAPIBody {
        final ArrayList<CellTower> cellTowers;
        final ArrayList<WifiAccessPoint> wifiAccessPoints;

        LocationAPIBody() {
            cellTowers = new ArrayList<>();
            wifiAccessPoints = new ArrayList<>();
        }
    }
}
