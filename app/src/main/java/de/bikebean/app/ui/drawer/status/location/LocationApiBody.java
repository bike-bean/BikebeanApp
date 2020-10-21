package de.bikebean.app.ui.drawer.status.location;

import androidx.annotation.NonNull;

import com.google.gson.Gson;

import de.bikebean.app.db.settings.settings.WappState;
import de.bikebean.app.db.settings.settings.number_settings.CellTowers;
import de.bikebean.app.db.settings.settings.number_settings.WifiAccessPoints;
import de.bikebean.app.ui.drawer.log.LogViewModel;

class LocationApiBody {

    private final @NonNull CellTowers.CellTowerList cellTowers;
    private final @NonNull WifiAccessPoints.WifiAccessPointList wifiAccessPoints;

    LocationApiBody(@NonNull WappState wappState, @NonNull LogViewModel lv) {
        final @NonNull WifiAccessPoints w = new WifiAccessPoints(wappState);
        wifiAccessPoints = (WifiAccessPoints.WifiAccessPointList) w.getList();
        lv.d("numberWifiAccessPoints: " + w.getNumber());

        final @NonNull CellTowers c = new CellTowers(wappState);
        cellTowers = (CellTowers.CellTowerList) c.getList();
        lv.d("numberCellTowers: " + c.getNumber());
    }

    @NonNull String createJsonApiBody(@NonNull LogViewModel lv) {
        final @NonNull Gson gson = new Gson();

        lv.d("WifiAccessPoints_Json: " + gson.toJson(wifiAccessPoints));
        lv.d("cellTowers_Json: " + gson.toJson(cellTowers));

        return gson.toJson(this);
    }
}
