package de.bikebean.app.ui.drawer.status.location;

import android.content.Context;
import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import de.bikebean.app.R;
import de.bikebean.app.db.settings.settings.WappState;
import de.bikebean.app.db.settings.settings.location_settings.Location;
import de.bikebean.app.db.type.SmsType;
import de.bikebean.app.db.type.types.LocationType;
import de.bikebean.app.ui.drawer.log.LogViewModel;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

class LocationUpdater extends AsyncTask<String, Void, Boolean> {

    public interface PostResponseHandler {
        void onPostResponse(@NonNull WappState wappCellTowers, SmsType smsType);
    }

    private static final @Nullable MediaType JSON =
            MediaType.parse("application/json; charset=utf-8");

    private final LocationStateViewModel mStateViewModel;
    private final LogViewModel mLogViewModel;
    private final @NonNull PostResponseHandler mPostResponseHandler;
    private final @NonNull WappState mWappState;

    private final @NonNull String mUrl;

    LocationUpdater(final @NonNull Context context, LocationStateViewModel st, LogViewModel lv,
                    final @NonNull PostResponseHandler postResponseHandler,
                    final @NonNull WappState wappState) {
        mStateViewModel = st;
        mLogViewModel = lv;
        mPostResponseHandler = postResponseHandler;
        mWappState = wappState;

        mUrl = context.getString(R.string.geolocation_baseurl) +
                context.getString(R.string.google_maps_api_key);
    }

    @Override
    protected @NonNull Boolean doInBackground(final @NonNull String... args) {
        final @NonNull OkHttpClient client = new OkHttpClient();
        final @NonNull String requestBodyString = new LocationApiBody(mWappState, mLogViewModel)
                .createJsonApiBody(mLogViewModel);
        if (requestBodyString.isEmpty())
            return false;

        final @Nullable RequestBody requestBody;
        if (JSON != null)
            requestBody = RequestBody.create(JSON, requestBodyString);
        else
            return false;

        final @NonNull Request request = new Request.Builder()
                .url(mUrl)
                .post(requestBody)
                .addHeader("Content-Type", "application/json")
                .build();
        final @Nullable Response response;

        if (mStateViewModel.getLocationByIdSync(mWappState))
            return false;

        mStateViewModel.insertNumberStates(mWappState);
        mLogViewModel.d("Updating Coordinates (Lat/Lng)...");

        try {
            response = client.newCall(request).execute();
            mLogViewModel.d("Successfully posted to geolocation API (" + response.code() + ")");
        } catch (IOException e) {
            mLogViewModel.w("Could not reach geolocation API: " + e.getMessage());
            return false;
        }

        /*
         Mark location is updating in the UI (if it is the newest)
         */
        if (mWappState.getIfNewest(mStateViewModel))
            mStateViewModel.insert(new Location(mWappState.getSms()));

        try {
            if (response.body() == null) {
                mLogViewModel.e("No body from Geolocation API!");
                return false;
            }

            final @NonNull String responseBodyString = response.body().string();

            mLogViewModel.d("RESPONSE FROM SERVER: " + responseBodyString);

            final @NonNull JSONObject responseJson = new JSONObject(responseBodyString);
            final @NonNull JSONObject locationJson = responseJson.getJSONObject("location");

            final @NonNull LocationType locationType = new LocationType(
                    locationJson, responseJson, mWappState.getSms()
            );

            mPostResponseHandler.onPostResponse(mWappState, locationType);
        } catch (JSONException e) {
            mLogViewModel.w("Could not parse Geolocation JSON: " + e.getMessage());
            return false;
        } catch (IOException e) {
            mLogViewModel.w("Could not parse Geolocation: " + e.getMessage());
            return false;
        }

        return true;
    }
}
