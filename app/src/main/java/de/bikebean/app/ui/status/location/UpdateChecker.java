package de.bikebean.app.ui.status.location;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;

import androidx.preference.PreferenceManager;

import java.lang.ref.WeakReference;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import de.bikebean.app.MainActivity;
import de.bikebean.app.Utils;
import de.bikebean.app.db.status.Status;
import de.bikebean.app.ui.status.StatusViewModel;
import de.bikebean.app.ui.status.settings.UpdateSettings;
import de.bikebean.app.ui.status.sms.parser.SmsParser;

public class UpdateChecker extends AsyncTask<String, Void, Boolean> {

    private final WeakReference<StatusViewModel> statusViewModelReference;

    public interface AsyncResponse {
        void onProcessFinished(boolean isLatLngUpdated);
    }

    private AsyncResponse mDelegate;

    public UpdateChecker(StatusViewModel statusViewModel, AsyncResponse delegate) {
        statusViewModelReference = new WeakReference<>(statusViewModel);
        mDelegate = delegate;
    }

    @Override
    protected Boolean doInBackground(String... args) {
        StatusViewModel statusViewModel = statusViewModelReference.get();

        List<de.bikebean.app.db.status.Status> l1 = statusViewModel.getCellTowers();
        List<de.bikebean.app.db.status.Status> l2 = statusViewModel.getLng();

        Date d1, d2;

        if (l1.size() > 0 && l2.size() > 0) {
            d1 = new Date(l1.get(0).getTimestamp());
            d2 = new Date(l2.get(0).getTimestamp());
        } else return false;

        Log.d(MainActivity.TAG, "Location last update: " + d1.toString());
        Log.d(MainActivity.TAG, "Lat/Lng last update: " + d2.toString());

        return d2.compareTo(d1) > 0;
    }

    @Override
    protected void onPostExecute(Boolean result) {
        mDelegate.onProcessFinished(result);
    }
}
