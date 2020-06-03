package de.bikebean.app.db.type.types;

import androidx.annotation.NonNull;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import de.bikebean.app.db.settings.Setting;
import de.bikebean.app.db.settings.settings.location_settings.Acc;
import de.bikebean.app.db.settings.settings.location_settings.Lat;
import de.bikebean.app.db.settings.settings.location_settings.Lng;
import de.bikebean.app.db.sms.Sms;
import de.bikebean.app.db.type.Type;

public class Location extends Type {

    private final List<Setting> settings;

    public Location(@NonNull JSONObject location, @NonNull JSONObject response, Sms sms)
            throws JSONException {
        super(SMSTYPE.LOCATION);

        settings = new ArrayList<>();

        settings.add(new Lat(location.getDouble("lat"), sms));
        settings.add(new Lng(location.getDouble("lng"), sms));
        settings.add(new Acc(response.getDouble("accuracy"), sms));
        settings.add(new de.bikebean.app.db.settings.settings.location_settings.Location(0.0, sms));
    }

    @Override
    public List<Setting> getSettings() {
        return settings;
    }
}
