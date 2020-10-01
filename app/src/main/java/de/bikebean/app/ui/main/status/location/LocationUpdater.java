package de.bikebean.app.ui.main.status.location;

import android.content.Context;
import android.os.AsyncTask;

import androidx.annotation.NonNull;

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
import de.bikebean.app.db.settings.settings.WappState;
import de.bikebean.app.db.sms.Sms;
import de.bikebean.app.db.type.Type;
import de.bikebean.app.db.type.types.Location;
import de.bikebean.app.ui.main.status.menu.log.LogViewModel;
import de.bikebean.app.ui.main.status.menu.sms_history.SmsViewModel;

class LocationUpdater extends AsyncTask<String, Void, Boolean> {

    public interface PostResponseHandler {
        void onPostResponse(WappState wappCellTowers, Type type, Sms sms);
    }

    private final LocationStateViewModel mStateViewModel;
    private final LogViewModel mLogViewModel;
    private final Sms mSms;
    private final PostResponseHandler mPostResponseHandler;
    private final WappState mWappState;

    private final RequestQueue mQueue;
    private final String mUrl;

    private static final Map<String, String> headers = new HashMap<String, String>() {{
        put("Content-Type", "application/json");
    }};

    LocationUpdater(Context context, LocationStateViewModel st, LogViewModel lv,
                    @NonNull SmsViewModel sm, PostResponseHandler postResponseHandler,
                    @NonNull WappState wappState) {
        mStateViewModel = st;
        mLogViewModel = lv;
        mSms = sm.getSmsByIdSync(wappState.getSmsId());
        mPostResponseHandler = postResponseHandler;
        mWappState = wappState;

        mQueue = Volley.newRequestQueue(context);

        String googleMapsAPIKey = context.getString(R.string.google_maps_api_key);
        String baseUrl = context.getString(R.string.geolocation_baseurl);
        mUrl = baseUrl + googleMapsAPIKey;
    }

    @Override
    protected Boolean doInBackground(String... args) {
        if (mStateViewModel.getLocationByIdSync(mSms))
            return false;

        String requestBody = new LocationApiBody(mWappState, mLogViewModel)
                .createJsonApiBody(mLogViewModel);
        if (requestBody.isEmpty())
            return false;

        mStateViewModel.insertNumberStates(mWappState, mSms);
        mLogViewModel.d("Updating Coordinates (Lat/Lng)...");
        httpPOST(requestBody);

        // mark location is updating in the UI (if it is the newest)
        if (mWappState.getIfNewest(mStateViewModel))
            mStateViewModel.insert(new de.bikebean.app.db.settings.settings.location_settings.Location(mWappState));

        return true;
    }

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

    private void responseListener(@NonNull JSONObject response) {
        mLogViewModel.d("RESPONSE FROM SERVER: " + response.toString());

        try {
            Location location = new Location(response.getJSONObject("location"), response, mSms);
            mPostResponseHandler.onPostResponse(mWappState, location, mSms);
        } catch (JSONException e) {
            assert true;
        }
    }

    private void errorListener(@NonNull VolleyError error) {
        mLogViewModel.d("Error.Response: " + error.getMessage());
    }
}
