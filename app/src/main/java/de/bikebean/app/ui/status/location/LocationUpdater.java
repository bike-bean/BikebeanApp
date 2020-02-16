package de.bikebean.app.ui.status.location;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import java.lang.ref.WeakReference;

import de.bikebean.app.MainActivity;
import de.bikebean.app.ui.status.sms.SmsViewModel;

class LocationUpdater extends AsyncTask<String, Void, Boolean> {

    private final WeakReference<Context> contextReference;
    private final LocationStateViewModel mStateViewModel;
    private final SmsViewModel mSmsViewModel;

    private final int mSmsId;

    LocationUpdater(Context context, LocationStateViewModel st, SmsViewModel sm, int smsId) {
        contextReference = new WeakReference<>(context);
        mStateViewModel = st;
        mSmsViewModel = sm;
        mSmsId = smsId;
    }

    @Override
    protected Boolean doInBackground(String... args) {
        String wifiAccessPoints = args[0];
        String cellTowers = args[1];

        if (checkAlreadyPresent())
            return false;

        GeolocationAPI geolocationAPI = new GeolocationAPI(contextReference.get(), mStateViewModel, mSmsViewModel);
        ApiParser apiParser = new ApiParser(mStateViewModel, mSmsViewModel);

        String requestBody = apiParser.createJsonApiBody(cellTowers, wifiAccessPoints, mSmsId);
        if (requestBody.isEmpty())
            return false;

        Log.d(MainActivity.TAG, "Updating Lat/Lng...");

        return geolocationAPI.httpPOST(requestBody, mSmsId);
    }

    private boolean checkAlreadyPresent() {
        return mStateViewModel.getLocationByIdSync(mSmsId);
    }

    @Override
    protected void onPostExecute(Boolean isLocationUpdated) {
        if (isLocationUpdated)
            mSmsViewModel.markParsed(mSmsId);
    }
}
