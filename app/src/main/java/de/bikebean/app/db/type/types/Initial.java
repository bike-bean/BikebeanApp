package de.bikebean.app.db.type.types;

import de.bikebean.app.db.settings.settings.Battery;
import de.bikebean.app.db.settings.settings.location_settings.Acc;
import de.bikebean.app.db.settings.settings.location_settings.Lat;
import de.bikebean.app.db.settings.settings.location_settings.Lng;
import de.bikebean.app.db.settings.settings.location_settings.Location;
import de.bikebean.app.db.settings.settings.number_settings.CellTowers;
import de.bikebean.app.db.settings.settings.number_settings.WifiAccessPoints;
import de.bikebean.app.db.type.SmsType;

public class Initial extends SmsType {

    public Initial() {
        super(TYPE.INITIAL);

        getSettings().add(new Battery());
        getSettings().add(new Location());
        getSettings().add(new Acc());
        getSettings().add(new Lat());
        getSettings().add(new Lng());
        getSettings().add(new CellTowers());
        getSettings().add(new WifiAccessPoints());
    }
}
