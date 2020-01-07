package de.bikebean.app.ui.status.location;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import java.lang.ref.WeakReference;

import de.bikebean.app.MainActivity;
import de.bikebean.app.db.status.Status;
import de.bikebean.app.ui.status.StatusViewModel;
import de.bikebean.app.ui.status.preferences.PreferenceUpdater;

public class LocationUpdater extends AsyncTask<String, Void, Boolean> {

    private final WeakReference<Context> contextReference;
    private StatusViewModel mStatusViewModel;
    private AsyncResponse mDelegate;

    private volatile static boolean isCellTowersSet = false, isWifiAccessPointsSet = false;
    private volatile static String cellTowers, wifiAccessPoints;

    public interface AsyncResponse {
        void onLocationUpdated(boolean isLocationUpdated);
    }

    public LocationUpdater(Context context, StatusViewModel statusViewModel, AsyncResponse delegate) {
        contextReference = new WeakReference<>(context);
        mStatusViewModel = statusViewModel;
        mDelegate = delegate;
    }

    @Override
    protected Boolean doInBackground(String... args) {
        Context context = contextReference.get();

        String key = args[1];

        if (key.equals(de.bikebean.app.db.status.Status.KEY_CELL_TOWERS)) {
            cellTowers = args[0];
            isCellTowersSet = true;
        } else if (key.equals(de.bikebean.app.db.status.Status.KEY_WIFI_ACCESS_POINTS)) {
            wifiAccessPoints = args[0];
            isWifiAccessPointsSet = true;
        }

        if (!(isCellTowersSet && isWifiAccessPointsSet))
            return false;

        GeolocationAPI geolocationAPI = new GeolocationAPI(context);
        ApiParser apiParser = new ApiParser(mStatusViewModel);

        Log.d(MainActivity.TAG, "Updating Lat/Lng...");

        String requestBody = apiParser.createJsonApiBody(cellTowers, wifiAccessPoints);
        geolocationAPI.httpPOST(requestBody, mStatusViewModel);

        return true;
    }

    @Override
    protected void onPostExecute(Boolean isLocationUpdated) {
        mDelegate.onLocationUpdated(isLocationUpdated);
    }
}
