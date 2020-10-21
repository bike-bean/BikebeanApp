package de.bikebean.app.db.type.types;

import androidx.annotation.NonNull;

import org.json.JSONException;
import org.json.JSONObject;

import de.bikebean.app.db.settings.settings.location_settings.Acc;
import de.bikebean.app.db.settings.settings.location_settings.Lat;
import de.bikebean.app.db.settings.settings.location_settings.Lng;
import de.bikebean.app.db.settings.settings.location_settings.Location;
import de.bikebean.app.db.sms.Sms;
import de.bikebean.app.db.type.SmsType;

public class LocationType extends SmsType {

    public LocationType(final @NonNull JSONObject location, final @NonNull JSONObject response,
                        final @NonNull Sms sms) throws JSONException {
        super(TYPE.LOCATION);

        getSettings().add(new Lat(location.getDouble("lat"), sms));
        getSettings().add(new Lng(location.getDouble("lng"), sms));
        getSettings().add(new Acc(response.getDouble("accuracy"), sms));
        getSettings().add(new Location(0.0, sms));
    }
}
