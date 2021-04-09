package de.bikebean.app.ui.drawer.wifi_localization;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProvider;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.bikebean.app.MainActivity;
import de.bikebean.app.R;
import de.bikebean.app.db.settings.settings.add_to_list_settings.number_settings.WifiAccessPoints;
import de.bikebean.app.db.sms.SmsFactory;
import de.bikebean.app.ui.drawer.status.StateViewModel;
import de.bikebean.app.ui.drawer.log.LogViewModel;

public class Wifi_localizationFragment extends Fragment {

    private Context ctx;
    private FragmentActivity act;
    private WifiManager wifiManager;
    private ListView listView;
    private TextView textView;
    private Button buttonScan;
    private final ArrayList<String> arrayList = new ArrayList<>();
    private final HashMap<String, Integer> hashMap = new HashMap<>();
    private ArrayAdapter<String> adapter;

    private LogViewModel logViewModel;
    private StateViewModel stateViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_wifi_localization, container, false);

        buttonScan = v.findViewById(R.id.scanBtn);
        listView = v.findViewById(R.id.wifiList);
        textView = v.findViewById(R.id.bikebean_wifi);

        return v;
    }

    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // Get Activity and Context
        act = requireActivity();
        ctx = act.getApplicationContext();

        buttonScan.setOnClickListener(view -> scanWifi());
        adapter = new ArrayAdapter<>(
                requireActivity(), android.R.layout.simple_list_item_1, arrayList
        );
        listView.setAdapter(adapter);

        stateViewModel = new ViewModelProvider(requireActivity()).get(StateViewModel.class);
        logViewModel = new ViewModelProvider(requireActivity()).get(LogViewModel.class);
    }

    @Override
    public void onResume() {
        super.onResume();

        final @NonNull MainActivity activity = (MainActivity) requireActivity();
        activity.setToolbarScrollEnabled(false);
        activity.resumeToolbarAndBottomSheet();
    }

    private void scanWifi() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                act.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 0);
        }
        wifiManager = (WifiManager) act.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        if (wifiManager.isWifiEnabled()) {
            // Aktiviere WLAN, falls deaktiviert
            wifiManager.setWifiEnabled(true);
        }

        // TODO: Überprüfen, ob Standort eingeschaltet ist
        // IMPLEMTIERUNG ÜBER HILFSKLASSE ÜBERPRÜFEN -> https://stackoverflow.com/questions/12320857/how-to-get-my-activity-context
        // Überprüfen, ob GPS-Standort eingeschaltet ist -> Nur dann können mittlerweile WLAN-Netzwerke gelesen werden
        // https://stackoverflow.com/questions/10311834/how-to-check-if-location-services-are-enabled

        // Ende Überprüfung GPS-Standort
        // App stürzt ab, wenn man aufs Wifi-Fragment klickt und GPS deaktiviert ist
        // Fehlermeldung: "java.lang.IllegalStateException: You need to use a Theme.AppCompat theme (or descendant) with this activity."

        LocationManager lm = (LocationManager)ctx.getSystemService(Context.LOCATION_SERVICE);
        boolean gps_enabled = false;
        // boolean network_enabled = false;

        try {
            gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch(Exception ex) {
            assert true;
        }

        if (!gps_enabled) {
            // notify user
            AlertDialog.Builder alert = new AlertDialog.Builder(requireActivity()); //ctx
                alert.setMessage(R.string.text_gps_network_not_enabled);
                alert.create().show();
        }

        arrayList.clear();
        hashMap.clear();

        act.registerReceiver(wifiReceiver, new IntentFilter(
                WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)
        );
        wifiManager.startScan();
        Toast.makeText(act, R.string.text_scanning_wifi, Toast.LENGTH_SHORT).show();
    }

    private final BroadcastReceiver wifiReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            List<ScanResult> results = wifiManager.getScanResults();
            act.unregisterReceiver(this);

            WifiAccessPoints wifiAccessPoints = new WifiAccessPoints(
                    SmsFactory.createNullSms(), stateViewModel.getWifiAccessPointsSync()
            );
            WifiAccessPoints.WifiAccessPointListBuilder wifiAccessPointsList_bikebean =
                    (WifiAccessPoints.WifiAccessPointListBuilder) wifiAccessPoints.getList();

            int counter_bikebean_wifi = 0;
            int counter_wifis = 0;

            int number_of_detected_wifis = 0;
            for (ScanResult scanResult : results) {
                //TODO:
                // Statt dem WLAN-Namen lieber nach der MacAdresse der BikeBean suchen
                String bikebean_wifi_name = "BikeBean.de";
                if (scanResult.SSID.equals(bikebean_wifi_name)){
                    String html_bikebean_wifi_found = "<b>BIKEBEAN-WIFI GEFUNDEN</b><br>Signalstärke: " + scanResult.level + " dBm";
                    textView.setText(Html.fromHtml(html_bikebean_wifi_found));
                    counter_bikebean_wifi++;
                }
                number_of_detected_wifis++;
            }

            logViewModel.d("Gefundene WLANs: " + number_of_detected_wifis);
            Toast.makeText(act, "Gefundene WLANs: " + number_of_detected_wifis,
                    Toast.LENGTH_SHORT).show();

            for (WifiAccessPoints.WifiAccessPoint w : wifiAccessPointsList_bikebean){
                for (ScanResult scanResult : results) {
                    if (scanResult.BSSID.equals(w.getMacAddress())) {
                        Integer difference_signal_strength = java.lang.Math.abs(scanResult.level - w.getSignalStrength());
                        hashMap.put("\u0394 " + difference_signal_strength.toString() + " dBm bei WLAN [" + scanResult.SSID + "]\n(Bikebean: " + w.getSignalStrength() + " dBm | Handy: " + scanResult.level + " dBm)", difference_signal_strength);
                        counter_wifis++;
                    }
                }
            }
            if (counter_wifis == 0) {
                arrayList.add("Keine relevanten WiFi-Access-Points in der Nähe gefunden");
            }
            else {
                hashMap.put("VERGLEICH SIGNALSTÄRKEN:",-1);
                //hashmap sortieren
                @SuppressWarnings("unchecked")
                Map.Entry<String, Integer>[] a =
                        (Map.Entry<String, Integer>[]) hashMap.entrySet().toArray();

                Arrays.sort(a, (o1, o2) -> o1.getValue().compareTo(o2.getValue()));
                //Aufsteigend nach den Integern sortierte Strings der hashmap an arrayList übergeben
                for (Map.Entry<String, Integer> e : a) {
                    arrayList.add(e.getKey());
                }
            }
            adapter.notifyDataSetChanged();

            if (counter_bikebean_wifi == 0) {
                textView.setText(R.string.text_wifi_not_found);
            }
        }
    };

}