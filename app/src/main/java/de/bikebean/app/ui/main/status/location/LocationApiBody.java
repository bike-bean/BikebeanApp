package de.bikebean.app.ui.main.status.location;

import com.google.gson.Gson;

import de.bikebean.app.db.settings.settings.WappState;
import de.bikebean.app.db.settings.settings.number_settings.CellTowers;
import de.bikebean.app.db.settings.settings.number_settings.WifiAccessPoints;
import de.bikebean.app.ui.main.status.menu.log.LogViewModel;

class LocationApiBody {

    private final CellTowers.CellTowerList cellTowers;
    private final WifiAccessPoints.WifiAccessPointList wifiAccessPoints;

    LocationApiBody(WappState wappState, LogViewModel lv) {
        WifiAccessPoints w = new WifiAccessPoints(wappState);
        this.wifiAccessPoints = (WifiAccessPoints.WifiAccessPointList) w.getList();
        lv.d("numberWifiAccessPoints: " + w.getNumber());

        CellTowers c = new CellTowers(wappState);
        this.cellTowers = (CellTowers.CellTowerList) c.getList();
        lv.d("numberCellTowers: " + c.getNumber());
    }

    String createJsonApiBody(LogViewModel lv) {
        final Gson gson = new Gson();

        lv.d("WifiAccessPoints_Json: " + gson.toJson(wifiAccessPoints));
        lv.d("cellTowers_Json: " + gson.toJson(cellTowers));

        return gson.toJson(this);
    }
}
