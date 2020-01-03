package de.bikebean.app.ui.status.location;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import java.lang.ref.WeakReference;
import java.util.List;

import de.bikebean.app.MainActivity;
import de.bikebean.app.ui.status.StatusViewModel;
import de.bikebean.app.ui.status.settings.UpdateSettings;
import de.bikebean.app.ui.status.sms.parser.SmsParser;

public class LocationUpdater extends AsyncTask<String, Void, String> {

    private final WeakReference<Context> contextReference;
    private final WeakReference<StatusViewModel> statusViewModelReference;

    public LocationUpdater(Context context, StatusViewModel statusViewModel) {
        contextReference = new WeakReference<>(context);
        statusViewModelReference = new WeakReference<>(statusViewModel);
    }

    @Override
    protected String doInBackground(String... args) {
        Context context = contextReference.get();
        StatusViewModel statusViewModel = statusViewModelReference.get();

        List<de.bikebean.app.db.status.Status> l1 = statusViewModel.getCellTowers();
        List<de.bikebean.app.db.status.Status> l2 = statusViewModel.getWifiAccessPoints();

        String cellTowers, wifiAccessPoints;

        if (l1.size() > 0 && l2.size() > 0) {
            cellTowers = l1.get(0).getLongValue();
            wifiAccessPoints = l2.get(0).getLongValue();
        } else return "";

        UpdateSettings updateSettings = new UpdateSettings();
        GeolocationAPI geolocationAPI = new GeolocationAPI(context);

        Log.d(MainActivity.TAG, "Updating Lat/Lng...");
        Log.d(MainActivity.TAG, cellTowers);
        Log.d(MainActivity.TAG, wifiAccessPoints);

        String requestBody = SmsParser.parseSMS(
                wifiAccessPoints + "...." + cellTowers, updateSettings, statusViewModel);
        geolocationAPI.httpPOST(requestBody, updateSettings, statusViewModel);

        return "";
    }
}
