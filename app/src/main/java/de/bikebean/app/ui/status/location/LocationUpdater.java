package de.bikebean.app.ui.status.location;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import java.lang.ref.WeakReference;

import de.bikebean.app.MainActivity;
import de.bikebean.app.ui.status.StatusViewModel;
import de.bikebean.app.ui.status.preferences.PreferenceUpdater;

public class LocationUpdater extends AsyncTask<String, Void, Boolean> {

    private final WeakReference<Context> contextReference;
    private final WeakReference<StatusViewModel> statusViewModelReference;
    private AsyncResponse mDelegate;

    private volatile static boolean isCellTowersSet = false, isWifiAccessPointsSet = false;
    private volatile static String cellTowers, wifiAccessPoints;

    public interface AsyncResponse {
        void onLocationUpdated(boolean isLocationUpdated);
    }

    public LocationUpdater(Context context, StatusViewModel statusViewModel, AsyncResponse delegate) {
        contextReference = new WeakReference<>(context);
        statusViewModelReference = new WeakReference<>(statusViewModel);
        mDelegate = delegate;
    }

    @Override
    protected Boolean doInBackground(String... args) {
        Context context = contextReference.get();
        StatusViewModel statusViewModel = statusViewModelReference.get();

        String key = args[1];

        if (key.equals("cellTowers")) {
            cellTowers = args[0];
            isCellTowersSet = true;
        }
        else if (key.equals("wifiAccessPoints")) {
            wifiAccessPoints = args[0];
            isWifiAccessPointsSet = true;
        }

        if (!(isCellTowersSet && isWifiAccessPointsSet))
            return false;

        GeolocationAPI geolocationAPI = new GeolocationAPI(context);
        ApiParser apiParser = new ApiParser(statusViewModel);

        Log.d(MainActivity.TAG, "Updating Lat/Lng...");
        Log.d(MainActivity.TAG, cellTowers);
        Log.d(MainActivity.TAG, wifiAccessPoints);

        String requestBody = apiParser.createJsonApiBody(cellTowers, wifiAccessPoints);
        geolocationAPI.httpPOST(requestBody, statusViewModel);

        return true;
    }

    @Override
    protected void onPostExecute(Boolean isLocationUpdated) {
        mDelegate.onLocationUpdated(isLocationUpdated);
    }

}
