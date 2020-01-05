package de.bikebean.app.ui.status.location;

import android.util.Log;

import com.google.gson.Gson;

import java.util.ArrayList;

import de.bikebean.app.MainActivity;
import de.bikebean.app.db.status.Status;
import de.bikebean.app.ui.status.StatusViewModel;
import de.bikebean.app.ui.status.preferences.PreferenceUpdater;

class ApiParser {

    private StatusViewModel mStatusViewModel;

    private LocationAPIBody locationAPIBody;

    private final Gson gson = new Gson();

    ApiParser(StatusViewModel statusViewModel) {
        mStatusViewModel = statusViewModel;

        locationAPIBody = new LocationAPIBody();
    }

    String createJsonApiBody(String cellTowers, String wifiAccessPoints) {
        int numberWifiAccessPoints = parseWifiAccessPoints(wifiAccessPoints, locationAPIBody);
        int numberCellTowers = parseCellTowers(cellTowers, locationAPIBody);

        Log.d(MainActivity.TAG, "WifiAccessPoints_JsonArray: " + gson.toJson(locationAPIBody.wifiAccessPoints));
        Log.d(MainActivity.TAG, "numberWifiAccessPoints: " + numberWifiAccessPoints);
        Log.d(MainActivity.TAG, "cellTowers_JsonArray: " + gson.toJson(locationAPIBody.cellTowers));
        Log.d(MainActivity.TAG, "numberCellTowers: " + numberCellTowers);

        mStatusViewModel.insert(new Status(
                System.currentTimeMillis(), Status.KEY_NO_WIFI_ACCESS_POINTS,
                (double) numberWifiAccessPoints, "", Status.STATUS_CONFIRMED, 0)
        );

        mStatusViewModel.insert(new Status(
                System.currentTimeMillis(), Status.KEY_NO_CELL_TOWERS,
                (double) numberCellTowers, "", Status.STATUS_CONFIRMED, 0)
        );

        // Create final json string
        return gson.toJson(locationAPIBody);
    }

    private int parseWifiAccessPoints(String wifiAccessPoints, LocationAPIBody locationAPIBody) {
        String[] stringArrayWapp = wifiAccessPoints.split("\n");
        int numberWifiAccessPoints = stringArrayWapp.length;

        for (String s : stringArrayWapp) {
            if (!s.equals("    ")) {
                // Länge des Substrings ist Unterscheidungskriterium
                WifiAccessPoint wap = new WifiAccessPoint();
                wap.macAddress = s.substring(2);
                wap.signalStrength = Integer.parseInt("-" + s.substring(0, 2));
                wap.toMacAddress();

                locationAPIBody.wifiAccessPoints.add(wap);
            }
        }

        return numberWifiAccessPoints;
    }

    private int parseCellTowers(String cellTowers, LocationAPIBody locationAPIBody) {
        String[] stringArrayWapp = cellTowers.split("\n");
        int numberCellTowers = stringArrayWapp.length;

        for (String s : stringArrayWapp) {
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

            while(str.length() > 0) {
                String nextChunk = str.substring(0, divisor);
                tmp.append(nextChunk);
                if (str.length() > 2) {
                    tmp.append(":");
                }

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
