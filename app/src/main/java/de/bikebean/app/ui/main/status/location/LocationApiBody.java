package de.bikebean.app.ui.main.status.location;

import com.google.gson.Gson;

import de.bikebean.app.db.settings.settings.Wapp;
import de.bikebean.app.db.settings.settings.number_settings.CellTowers;
import de.bikebean.app.db.settings.settings.number_settings.WifiAccessPoints;
import de.bikebean.app.db.sms.Sms;
import de.bikebean.app.ui.main.status.menu.log.LogViewModel;

class LocationApiBody {

    private final CellTowers.CellTowerList cellTowers;
    private final WifiAccessPoints.WifiAccessPointList wifiAccessPoints;
    private LogViewModel logViewModel;

    LocationApiBody(Wapp wapp, LogViewModel lv) {
        WifiAccessPoints w = wapp.getWifiAccessPointSetting(new Sms());
        this.wifiAccessPoints = (WifiAccessPoints.WifiAccessPointList) w.getList();
        logViewModel = lv;
        lv.d("numberWifiAccessPoints: " + w.getNumber());

        CellTowers c = wapp.getCellTowerSetting(new Sms());
        this.cellTowers = (CellTowers.CellTowerList) c.getList();
        lv.d("numberCellTowers: " + c.getNumber());
    }

    String createJsonApiBody() {
        final Gson gson = new Gson();

        logViewModel.d("WifiAccessPoints_Json: " + gson.toJson(wifiAccessPoints));
        logViewModel.d("cellTowers_Json: " + gson.toJson(cellTowers));

        return gson.toJson(this);
    }
}
