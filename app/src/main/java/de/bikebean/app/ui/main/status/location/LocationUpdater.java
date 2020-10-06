package de.bikebean.app.ui.main.status.location;

import android.content.Context;
import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import de.bikebean.app.R;
import de.bikebean.app.db.settings.settings.WappState;
import de.bikebean.app.db.type.Type;
import de.bikebean.app.db.type.types.Location;
import de.bikebean.app.ui.main.status.menu.log.LogViewModel;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

class LocationUpdater extends AsyncTask<String, Void, Boolean> {

    public interface PostResponseHandler {
        void onPostResponse(@NonNull WappState wappCellTowers, Type type);
    }

    private static final @Nullable MediaType JSON =
            MediaType.parse("application/json; charset=utf-8");

    private final LocationStateViewModel mStateViewModel;
    private final @NonNull LogViewModel mLogViewModel;
    private final @NonNull PostResponseHandler mPostResponseHandler;
    private final @NonNull WappState mWappState;

    private final @NonNull String mUrl;

    LocationUpdater(@NonNull Context context, LocationStateViewModel st, @NonNull LogViewModel lv,
                    @NonNull PostResponseHandler postResponseHandler,
                    @NonNull WappState wappState) {
        mStateViewModel = st;
        mLogViewModel = lv;
        mPostResponseHandler = postResponseHandler;
        mWappState = wappState;

        mUrl = context.getString(R.string.geolocation_baseurl) +
                context.getString(R.string.google_maps_api_key);
    }

    @Override
    protected Boolean doInBackground(String... args) {
        OkHttpClient client = new OkHttpClient();
        String requestBodyString = new LocationApiBody(mWappState, mLogViewModel)
                .createJsonApiBody(mLogViewModel);
        if (requestBodyString.isEmpty())
            return false;

        RequestBody requestBody;
        if (JSON != null)
            requestBody = RequestBody.create(JSON, requestBodyString);
        else
            return false;

        Request request = new Request.Builder()
                .url(mUrl)
                .post(requestBody)
                .addHeader("Content-Type", "application/json")
                .build();
        Response response;

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
            mStateViewModel.insert(new de.bikebean.app.db.settings.settings.location_settings.Location(mWappState));

        try {
            if (response.body() == null) {
                mLogViewModel.e("No body from Geolocation API!");
                return false;
            }

            String responseBodyString = response.body().string();

            mLogViewModel.d("RESPONSE FROM SERVER: " + responseBodyString);

            JSONObject responseJson = new JSONObject(responseBodyString);
            JSONObject locationJson = responseJson.getJSONObject("location");

            Location location = new Location(locationJson, responseJson, mWappState);

            mPostResponseHandler.onPostResponse(mWappState, location);
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
