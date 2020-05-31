package de.bikebean.app.ui.main.status.location;

import android.content.Context;
import android.os.AsyncTask;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import de.bikebean.app.R;
import de.bikebean.app.db.settings.settings.Acc;
import de.bikebean.app.db.settings.settings.Lat;
import de.bikebean.app.db.settings.settings.Lng;
import de.bikebean.app.db.settings.settings.Location;
import de.bikebean.app.db.settings.settings.Wapp;
import de.bikebean.app.db.sms.Sms;
import de.bikebean.app.ui.initialization.SettingsList;
import de.bikebean.app.ui.main.status.menu.log.LogViewModel;

class LocationUpdater extends AsyncTask<String, Void, Boolean> {

    public interface PostResponseHandler {
        void onPostResponse(Wapp wappCellTowers, SettingsList settings, Sms sms);
    }

    private final LogViewModel mLogViewModel;
    private final LocationStateViewModel mStateViewModel;
    private final Sms mSms;
    private final PostResponseHandler mPostResponseHandler;
    private final Wapp mWapp;

    private final RequestQueue mQueue;
    private final String mUrl;

    LocationUpdater(Context context, LocationStateViewModel st, LogViewModel lv,
                    Sms sms, PostResponseHandler postResponseHandler, Wapp wapp) {
        mStateViewModel = st;
        mLogViewModel = lv;
        mSms = sms;
        mPostResponseHandler = postResponseHandler;
        mWapp = wapp;

        mQueue = Volley.newRequestQueue(context);

        String googleMapsAPIKey = context.getResources().getString(R.string.google_maps_api_key);
        String baseUrl = "https://www.googleapis.com/geolocation/v1/geolocate?key=";
        mUrl = baseUrl + googleMapsAPIKey;
    }

    @Override
    protected Boolean doInBackground(String... args) {
        if (mStateViewModel.getLocationByIdSync(mSms))
            return false;

        String requestBody =
                new LocationApiBody(mWapp, mLogViewModel)
                        .createJsonApiBody(mLogViewModel);
        if (requestBody.isEmpty())
            return false;

        mStateViewModel.insertNumberStates(mWapp, mSms);
        mLogViewModel.d("Updating Lat/Lng...");

        httpPOST(requestBody);

        // mark location is updating in the UI (if it is the newest)
        if (mWapp.getIfNewest(mStateViewModel))
            mStateViewModel.insert(new Location(mWapp));

        return true;
    }

    private static final Map<String, String> headers = new HashMap<String, String>() {{
        put("Content-Type", "application/json");
    }};

    // POST Request Geolocation API
    private void httpPOST(final String requestBody) {
        mQueue.add(new JsonObjectRequest(Request.Method.POST, mUrl, null,
                this::responseListener, this::errorListener) {
            @Override
            public Map<String, String> getHeaders() {
                return headers;
            }

            @Override
            public byte[] getBody() {
                return requestBody.getBytes(StandardCharsets.UTF_8);
            }
        });
    }

    private void responseListener(JSONObject response) {
        mLogViewModel.d("RESPONSE FROM SERVER: " + response.toString());

        try {
            JSONObject location = response.getJSONObject("location");

            SettingsList settings = new SettingsList();
            settings._add(new Lat(location.getDouble("lat"), mSms))
                    ._add(new Lng(location.getDouble("lng"), mSms))
                    ._add(new Acc(response.getDouble("accuracy"), mSms))
                    ._add(new Location(0.0, mSms));

            mPostResponseHandler.onPostResponse(mWapp, settings, mSms);
        } catch (JSONException e) {
            assert true;
        }
    }

    private void errorListener(VolleyError error) {
        mLogViewModel.d("Error.Response: " + error.getMessage());
    }
}
