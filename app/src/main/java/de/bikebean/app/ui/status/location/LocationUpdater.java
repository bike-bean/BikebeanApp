package de.bikebean.app.ui.status.location;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import java.lang.ref.WeakReference;

import de.bikebean.app.MainActivity;
import de.bikebean.app.db.state.State;
import de.bikebean.app.ui.status.StateViewModel;
import de.bikebean.app.ui.status.sms.SmsViewModel;

public class LocationUpdater extends AsyncTask<String, Void, Boolean> {

    private final WeakReference<Context> contextReference;
    private StateViewModel mStateViewModel;
    private SmsViewModel mSmsViewModel;
    private AsyncResponse mDelegate;

    private volatile static boolean isCellTowersSet = false, isWifiAccessPointsSet = false;
    private volatile static String cellTowers, wifiAccessPoints;

    private int mSmsId;

    public interface AsyncResponse {
        void onLocationUpdated(boolean isLocationUpdated);
    }

    LocationUpdater(Context context, StateViewModel stateViewModel, SmsViewModel smsViewModel,
                    int smsId, AsyncResponse delegate) {
        contextReference = new WeakReference<>(context);
        mStateViewModel = stateViewModel;
        mSmsViewModel = smsViewModel;
        mDelegate = delegate;
        mSmsId = smsId;
    }

    @Override
    protected Boolean doInBackground(String... args) {
        String key = args[0];

        if (key.equals(State.KEY_CELL_TOWERS)) {
            cellTowers = args[1];
            isCellTowersSet = true;
        } else if (key.equals(State.KEY_WIFI_ACCESS_POINTS)) {
            wifiAccessPoints = args[1];
            isWifiAccessPointsSet = true;
        }

        if (!(isCellTowersSet && isWifiAccessPointsSet))
            return false;

        GeolocationAPI geolocationAPI = new GeolocationAPI(contextReference.get(), mStateViewModel, mSmsViewModel);
        ApiParser apiParser = new ApiParser(mStateViewModel, mSmsViewModel);

        Log.d(MainActivity.TAG, "Updating Lat/Lng...");

        String requestBody = apiParser.createJsonApiBody(cellTowers, wifiAccessPoints, mSmsId);
        if (requestBody.isEmpty())
            return false;

        return geolocationAPI.httpPOST(requestBody, mSmsId);
    }

    @Override
    protected void onPostExecute(Boolean isLocationUpdated) {
        mDelegate.onLocationUpdated(isLocationUpdated);
    }
}
