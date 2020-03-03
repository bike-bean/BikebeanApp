package de.bikebean.app.ui.main.status.location;

import android.util.Log;

import com.google.gson.Gson;

import de.bikebean.app.MainActivity;
import de.bikebean.app.db.settings.settings.Wapp;
import de.bikebean.app.db.settings.settings.number_settings.CellTowers;
import de.bikebean.app.db.settings.settings.number_settings.WifiAccessPoints;
import de.bikebean.app.db.sms.Sms;

class LocationApiBody {

    private final CellTowers.CellTowerList cellTowers;
    private final WifiAccessPoints.WifiAccessPointList wifiAccessPoints;

    LocationApiBody(Wapp wapp) {
        WifiAccessPoints w = wapp.getWifiAccessPointSetting(new Sms());
        this.wifiAccessPoints = (WifiAccessPoints.WifiAccessPointList) w.getList();
        Log.d(MainActivity.TAG, "numberWifiAccessPoints: " + w.getNumber());

        CellTowers c = wapp.getCellTowerSetting(new Sms());
        this.cellTowers = (CellTowers.CellTowerList) c.getList();
        Log.d(MainActivity.TAG, "numberCellTowers: " + c.getNumber());
    }

    String createJsonApiBody() {
        final Gson gson = new Gson();

        Log.d(MainActivity.TAG, "WifiAccessPoints_Json: " + gson.toJson(wifiAccessPoints));
        Log.d(MainActivity.TAG, "cellTowers_Json: " + gson.toJson(cellTowers));

        return gson.toJson(this);
    }
}
