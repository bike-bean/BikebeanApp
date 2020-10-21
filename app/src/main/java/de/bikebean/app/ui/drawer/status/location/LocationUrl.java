package de.bikebean.app.ui.drawer.status.location;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;
import java.util.Locale;

import de.bikebean.app.db.state.State;

public class LocationUrl {

    private final MutableLiveData<String> url = new MutableLiveData<>();
    private static final @NonNull String baseUrl = "https://maps.app.goo.gl/?link=";
    private static final @NonNull String mapBaseUrl = "https://www.google.com/maps/search/?api=1&query=";

    private double lat, lng;
    private boolean latSet = false, lngSet = false;

    public void setLat(final @NonNull List<State> lat) {
        if (lat.size() == 0) {
            setUrl();
            return;
        }

        latSet = true;
        this.lat = lat.get(0).getValue();

        setUrl();
    }

    public void setLng(final @NonNull List<State> lng) {
        if (lng.size() == 0) {
            setUrl();
            return;
        }

        lngSet = true;
        this.lng = lng.get(0).getValue();

        setUrl();
    }

    private void setUrl() {
        if (latSet && lngSet)
            url.setValue(encodeUrl());
        else
            url.setValue("");
    }

    public MutableLiveData<String> getUrl() {
        return url;
    }

    private @NonNull String encodeUrl() {
        try {
            return baseUrl + URLEncoder.encode(
                    mapBaseUrl + encodeLocation(this.lat) + "," + encodeLocation(this.lng),
                    "UTF-8"
            );
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return mapBaseUrl + encodeLocation(this.lat) + "," + encodeLocation(this.lng);
        }
    }

    private @NonNull String encodeLocation(double loc) {
        return String.format(Locale.GERMANY, "%.7f", loc)
                .replace(",", ".");
    }
}
