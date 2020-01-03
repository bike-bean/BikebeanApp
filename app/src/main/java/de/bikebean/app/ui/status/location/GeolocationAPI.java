package de.bikebean.app.ui.status.location;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import de.bikebean.app.BuildConfig;
import de.bikebean.app.MainActivity;
import de.bikebean.app.ui.status.StatusViewModel;
import de.bikebean.app.ui.status.settings.UpdateSettings;

class GeolocationAPI {

    private static final String GoogleMapsAPIKey = BuildConfig.GOOGLEMAPSAPIKEY;
    private static final String baseUrl = "https://www.googleapis.com/geolocation/v1/geolocate?key=";
    private static final String url = baseUrl + GoogleMapsAPIKey;

    private final RequestQueue queue;

    GeolocationAPI(Context ctx) {
        queue = Volley.newRequestQueue(ctx);
    }

    //POST Request API #3
    void httpPOST(final String requestBody, final UpdateSettings s, StatusViewModel vm) {
        JsonObjectRequest postRequest = new JsonObjectRequest(
                Request.Method.POST, url, null,
                response -> {
                    Log.d(MainActivity.TAG, "RESPONSE FROM SERVER: " + response.toString());
                    try {
                        JSONObject location = response.getJSONObject("location");
                        s.updateLngLat(
                                (float) location.getDouble("lat"),
                                (float) location.getDouble("lng"),
                                (float) response.getDouble("accuracy"),
                                vm
                        );
                    } catch (JSONException | InterruptedException e) {
                        assert true;
                    }
                },
                error -> { // error
                    Log.d(MainActivity.TAG, "Error.Response: " + error.getMessage());
                }
        ) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
//                headers.put("Accept", "application/json");
//                headers.put("Content-Type", "application/json; charset=utf-8");
                headers.put("Content-Type", "application/json");

                return headers;
            }

//            @Override
//            protected Map<String, String> getParams() {
//                Map<String, String>  params = new HashMap<String, String>();
//                params.put("cellTowers", cellTowers.toString());
//                params.put("wifiAccessPoints", wifiAccessPoints.toString());
//
//                return params;
//            }

            @Override
            public byte[] getBody() {
                return requestBody.getBytes(StandardCharsets.UTF_8);
            }
        };

        queue.add(postRequest);
    }
}
