package de.bikebean.app.ui.drawer.status.location;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import java.util.List;
import java.util.Locale;

import de.bikebean.app.db.state.State;

public class LocationUri {

    private final MutableLiveData<String> uri = new MutableLiveData<>();
    private static final @NonNull String baseString = "geo:0,0?q=%s,%s(%s)";

    private final @NonNull String bikeName;
    private double lat, lng;
    private boolean latSet = false, lngSet = false;

    public LocationUri(final @NonNull String bikeName) {
        this.bikeName = bikeName;
    }

    public void setLat(final @NonNull List<State> lat) {
        if (lat.size() == 0) {
            setUri();
            return;
        }

        latSet = true;
        this.lat = lat.get(0).getValue();

        setUri();
    }

    public void setLng(final @NonNull List<State> lng) {
        if (lng.size() == 0) {
            setUri();
            return;
        }

        lngSet = true;
        this.lng = lng.get(0).getValue();

        setUri();
    }

    private void setUri() {
        if (latSet && lngSet)
            uri.setValue(encodeUrl());
        else
            uri.setValue("");
    }

    public MutableLiveData<String> getUri() {
        return uri;
    }

    private @NonNull String encodeUrl() {
        return String.format(Locale.GERMANY, baseString, lat, lng, bikeName);
    }
}
