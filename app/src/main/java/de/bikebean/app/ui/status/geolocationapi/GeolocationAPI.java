package de.bikebean.app.ui.status.geolocationapi;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import de.bikebean.app.BuildConfig;
import de.bikebean.app.MainActivity;
import de.bikebean.app.ui.status.StatusFragment;

public class GeolocationAPI {

    private static final String GoogleMapsAPIKey = BuildConfig.GOOGLEMAPSAPIKEY;
    private static final String baseUrl = "https://www.googleapis.com/geolocation/v1/geolocate?key=";
    private static final String url = baseUrl + GoogleMapsAPIKey;

    private RequestQueue queue;

    public GeolocationAPI(Context ctx) {
        queue = Volley.newRequestQueue(ctx);
    }

    //POST Request API #3
    public void httpPOST(final String requestBody) {
        JsonObjectRequest postRequest = new JsonObjectRequest(
                Request.Method.POST, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(MainActivity.TAG, "RESPONSE FROM SERVER: " + response.toString());
                        StatusFragment.setCurrentPositionBikeCoordinates(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error
                        Log.d(MainActivity.TAG, "Error.Response: " + error.getMessage());
                    }
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
//            protected Map<String, String> getParams()
//            {
//
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
