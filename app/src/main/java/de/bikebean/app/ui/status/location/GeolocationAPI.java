package de.bikebean.app.ui.status.location;

import android.content.Context;
import android.util.Log;

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

import de.bikebean.app.MainActivity;
import de.bikebean.app.R;
import de.bikebean.app.db.state.State;

class GeolocationAPI {

    public interface PostResponseHandler {
        void onPostResponse(
                double lat, double lng, double acc, int smsId,
                State wappCellTowers, State wappWifiAccessPoints
        );
    }

    private final RequestQueue queue;
    private final PostResponseHandler mPostResponseHandler;

    private State finalWappCellTowers;
    private State finalWappWifiAccessPoints;

    private final String url;

    private int mSmsId;

    GeolocationAPI(Context ctx, PostResponseHandler postResponseHandler,
                   State wappCellTowers, State wappWifiAccessPoints) {
        queue = Volley.newRequestQueue(ctx);
        mPostResponseHandler = postResponseHandler;

        finalWappCellTowers = wappCellTowers;
        finalWappWifiAccessPoints = wappWifiAccessPoints;

        String googleMapsAPIKey = ctx.getResources().getString(R.string.google_maps_api_key);
        String baseUrl = "https://www.googleapis.com/geolocation/v1/geolocate?key=";
        url = baseUrl + googleMapsAPIKey;
    }

    //POST Request Geolocation API
    boolean httpPOST(final String requestBody, int smsId) {
        mSmsId = smsId;

        queue.add(new JsonObjectRequest(Request.Method.POST, url, null,
                this::responseListener, this::errorListener) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");

                return headers;
            }

            @Override
            public byte[] getBody() {
                return requestBody.getBytes(StandardCharsets.UTF_8);
            }
        });

        return true;
    }

    private void responseListener(JSONObject response) {
        Log.d(MainActivity.TAG, "RESPONSE FROM SERVER: " + response.toString());
        try {
            JSONObject location = response.getJSONObject("location");
            mPostResponseHandler.onPostResponse(
                    location.getDouble("lat"),
                    location.getDouble("lng"),
                    response.getDouble("accuracy"),
                    mSmsId, finalWappCellTowers, finalWappWifiAccessPoints

            );
        } catch (JSONException e) {
            assert true;
        }
    }

    private void errorListener(VolleyError error) {
        Log.d(MainActivity.TAG, "Error.Response: " + error.getMessage());
    }
}