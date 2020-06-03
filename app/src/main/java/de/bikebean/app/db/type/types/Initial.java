package de.bikebean.app.db.type.types;

import java.util.ArrayList;
import java.util.List;

import de.bikebean.app.db.settings.Setting;
import de.bikebean.app.db.settings.settings.Battery;
import de.bikebean.app.db.settings.settings.location_settings.Acc;
import de.bikebean.app.db.settings.settings.location_settings.Lat;
import de.bikebean.app.db.settings.settings.location_settings.Lng;
import de.bikebean.app.db.settings.settings.location_settings.Location;
import de.bikebean.app.db.settings.settings.number_settings.CellTowers;
import de.bikebean.app.db.settings.settings.number_settings.WifiAccessPoints;
import de.bikebean.app.db.type.Type;

public class Initial extends Type {

    private final List<Setting> settings;

    public Initial() {
        super(SMSTYPE.INITIAL);

        settings = new ArrayList<>();

        settings.add(new Battery());
        settings.add(new Location());
        settings.add(new Acc());
        settings.add(new Lat());
        settings.add(new Lng());
        settings.add(new CellTowers());
        settings.add(new WifiAccessPoints());
    }

    @Override
    public List<Setting> getSettings() {
        return settings;
    }
}
