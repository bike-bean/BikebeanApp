package de.bikebean.app.ui.main.status.location;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import java.lang.ref.WeakReference;

import de.bikebean.app.MainActivity;
import de.bikebean.app.db.state.State;

class LocationUpdater extends AsyncTask<String, Void, Boolean> {

    private final WeakReference<Context> contextReference;
    private final LocationStateViewModel mStateViewModel;

    private final State finalWappCellTowers;
    private final State finalWappWifiAccessPoints;

    private final int mSmsId;

    private final GeolocationAPI.PostResponseHandler mPostResponseHandler;
    private final ApiParser.PostJsonCreateHandler mPostJsonCreateHandler;

    LocationUpdater(Context context, LocationStateViewModel st, int smsId,
                    GeolocationAPI.PostResponseHandler postResponseHandler,
                    ApiParser.PostJsonCreateHandler postJsonCreateHandler,
                    State wappCellTowers, State wappWifiAccessPoints) {
        contextReference = new WeakReference<>(context);
        mStateViewModel = st;
        mSmsId = smsId;

        finalWappCellTowers = wappCellTowers;
        finalWappWifiAccessPoints = wappWifiAccessPoints;

        mPostResponseHandler = postResponseHandler;
        mPostJsonCreateHandler = postJsonCreateHandler;
    }

    @Override
    protected Boolean doInBackground(String... args) {
        String wifiAccessPoints = args[0];
        String cellTowers = args[1];

        if (checkAlreadyPresent())
            return false;

        GeolocationAPI geolocationAPI = new GeolocationAPI(
                contextReference.get(), mPostResponseHandler,
                finalWappCellTowers, finalWappWifiAccessPoints
        );
        ApiParser apiParser = new ApiParser(mPostJsonCreateHandler);

        String requestBody = apiParser.createJsonApiBody(cellTowers, wifiAccessPoints, mSmsId);
        if (requestBody.isEmpty())
            return false;

        Log.d(MainActivity.TAG, "Updating Lat/Lng...");

        geolocationAPI.httpPOST(requestBody, mSmsId);
        return true;
    }

    private boolean checkAlreadyPresent() {
        return mStateViewModel.getLocationByIdSync(mSmsId);
    }

    @Override
    protected void onPostExecute(Boolean arg) {
    }
}
