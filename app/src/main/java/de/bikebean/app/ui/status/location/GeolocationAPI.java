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
import java.util.List;
import java.util.Map;

import de.bikebean.app.MainActivity;
import de.bikebean.app.R;
import de.bikebean.app.db.sms.Sms;
import de.bikebean.app.db.state.State;
import de.bikebean.app.ui.status.StateViewModel;
import de.bikebean.app.ui.status.sms.SmsViewModel;

class GeolocationAPI {

    private final String url;

    private final RequestQueue queue;

    private final StateViewModel vm;
    private final SmsViewModel sm;
    private Sms sms;

    GeolocationAPI(Context ctx, StateViewModel stateViewModel, SmsViewModel sm) {
        queue = Volley.newRequestQueue(ctx);
        vm = stateViewModel;
        this.sm = sm;

        String googleMapsAPIKey = ctx.getResources().getString(R.string.google_maps_api_key);
        String baseUrl = "https://www.googleapis.com/geolocation/v1/geolocate?key=";
        url = baseUrl + googleMapsAPIKey;
    }

    //POST Request Geolocation API
    boolean httpPOST(final String requestBody, int smsId) {
        List<Sms> l = sm.getSmsById(smsId);

        if (l.size() == 0)
            return false;

        this.sms = l.get(0);

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
            updateLngLatAcc(
                    location.getDouble("lat"),
                    location.getDouble("lng"),
                    response.getDouble("accuracy"),
                    vm, sms
            );
        } catch (JSONException e) {
            assert true;
        }
    }

    private void errorListener(VolleyError error) {
        Log.d(MainActivity.TAG, "Error.Response: " + error.getMessage());
    }

    private void updateLngLatAcc(double lat, double lng, double acc, StateViewModel vm, Sms sms) {
        vm.insert(new State(
                sms.getTimestamp(), State.KEY_LAT, lat,
                "", State.STATUS_CONFIRMED, sms.getId())
        );
        vm.insert(new State(
                sms.getTimestamp(), State.KEY_LNG, lng,
                "", State.STATUS_CONFIRMED, sms.getId())
        );
        vm.insert(new State(
                sms.getTimestamp(), State.KEY_ACC, acc,
                "", State.STATUS_CONFIRMED, sms.getId())
        );

        vm.confirmLocationKeys();
    }
}