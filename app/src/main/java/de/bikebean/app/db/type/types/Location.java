package de.bikebean.app.db.type.types;

import androidx.annotation.NonNull;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import de.bikebean.app.db.settings.Setting;
import de.bikebean.app.db.settings.settings.WappState;
import de.bikebean.app.db.settings.settings.location_settings.Acc;
import de.bikebean.app.db.settings.settings.location_settings.Lat;
import de.bikebean.app.db.settings.settings.location_settings.Lng;
import de.bikebean.app.db.type.Type;

public class Location extends Type {

    private final @NonNull List<Setting> settings;

    public Location(final @NonNull JSONObject location, final @NonNull JSONObject response,
                    final @NonNull WappState wappState) throws JSONException {
        super(SMSTYPE.LOCATION);

        settings = new ArrayList<>();

        settings.add(new Lat(location.getDouble("lat"), wappState));
        settings.add(new Lng(location.getDouble("lng"), wappState));
        settings.add(new Acc(response.getDouble("accuracy"), wappState));
        settings.add(new de.bikebean.app.db.settings.settings.location_settings.Location(0.0, wappState));
    }

    @Override
    public @NonNull List<Setting> getSettings() {
        return settings;
    }
}
