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

    private volatile static boolean isCellTowersSet, isWifiAccessPointsSet;
    private volatile static String cellTowers, wifiAccessPoints;
    private volatile static boolean isLocationUpdated;

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

        isCellTowersSet = isWifiAccessPointsSet = isLocationUpdated = false;

        String key = args[1];

        try {
            waitForOther(key);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        if (key.equals("cellTowers"))
            cellTowers = args[0];
        else if (key.equals("wifiAccessPoints"))
            wifiAccessPoints = args[0];

        synchronized (this) {
            if (isLocationUpdated)
                return true;

            GeolocationAPI geolocationAPI = new GeolocationAPI(context);
            ApiParser apiParser = new ApiParser(statusViewModel);

            Log.d(MainActivity.TAG, "Updating Lat/Lng...");
            Log.d(MainActivity.TAG, cellTowers);
            Log.d(MainActivity.TAG, wifiAccessPoints);

            String requestBody = apiParser.createJsonApiBody(cellTowers, wifiAccessPoints);
            geolocationAPI.httpPOST(requestBody, statusViewModel);

            isLocationUpdated = true;
        }

        return true;
    }

    @Override
    protected void onPostExecute(Boolean isLocationUpdated) {
        mDelegate.onLocationUpdated(isLocationUpdated);
    }

    private void waitForOther(String key) throws InterruptedException {
        if (key.equals("cellTowers"))
            waitForWifiAccessPoints();
        else if (key.equals("wifiAccessPoints"))
            waitForCellTowers();
    }

    private void waitForCellTowers() throws InterruptedException {
        while (!isCellTowersSet) {
            Thread.sleep(100);
        }
    }

    private void waitForWifiAccessPoints() throws InterruptedException {
        while (!isWifiAccessPointsSet) {
            Thread.sleep(100);
        }
    }
}
