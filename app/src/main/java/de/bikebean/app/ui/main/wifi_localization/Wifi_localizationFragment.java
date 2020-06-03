package de.bikebean.app.ui.main.wifi_localization;

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
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
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
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import de.bikebean.app.R;
import de.bikebean.app.db.settings.settings.number_settings.WifiAccessPoints;
import de.bikebean.app.db.sms.Sms;
import de.bikebean.app.ui.main.status.StateViewModel;
import de.bikebean.app.ui.main.status.menu.log.LogViewModel;

public class Wifi_localizationFragment extends Fragment {

    private Context ctx;
    private FragmentActivity act;
    private WifiManager wifiManager;
    private ListView listView;
    private TextView textView;
    private Button buttonScan;
    private final ArrayList<String> arrayList = new ArrayList<>();
    private final HashMap<String, Integer> hashmap = new HashMap<>();
    private ArrayAdapter adapter;

    private LogViewModel logViewModel;
    private StateViewModel stateViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_wifi_localization, container, false);

        buttonScan = root.findViewById(R.id.scanBtn);
        listView = root.findViewById(R.id.wifiList);

        textView = root.findViewById(R.id.bikebean_wifi);

        return root;
    }

    // LM: Methode onActivityCreated: Alternative in einem Fragment (Superklasse: Fragment) Zum Erben von Superklasse AppCompatActivty (Implementierung: this = getActivity())
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // Get Activity and Context
        act = requireActivity();
        ctx = act.getApplicationContext();

        // hide the actionbar for this fragment
        ActionBar actionbar = ((AppCompatActivity) act).getSupportActionBar();
        Objects.requireNonNull(actionbar).hide();

        buttonScan.setOnClickListener(view -> scanWifi());
        adapter = new ArrayAdapter<>(requireActivity(), android.R.layout.simple_list_item_1, arrayList);
        listView.setAdapter(adapter);

        stateViewModel = new ViewModelProvider(requireActivity()).get(StateViewModel.class);
        logViewModel = new ViewModelProvider(requireActivity()).get(LogViewModel.class);

        scanWifi();
    }

    private void scanWifi() {
        // Nachträglich hinzugefügt (ohne diese Überprüfung wurden keine WLANs gefunden):
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                act.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 0);
        }
        wifiManager = (WifiManager) act.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        if (!Objects.requireNonNull(wifiManager).isWifiEnabled()) {
            // Aktiviere WLAN, falls deaktiviert
            wifiManager.setWifiEnabled(true);
        }

//        TODO: Überprüfen, ob Standort eingeschaltet ist
//        // IMPLEMTIERUNG ÜBER HILFSKLASSE ÜBERPRÜFEN -> https://stackoverflow.com/questions/12320857/how-to-get-my-activity-context
//        // Überprüfen, ob GPS-Standort eingeschaltet ist -> Nur dann können mittlerweile WLAN-Netzwerke gelesen werden
//        // https://stackoverflow.com/questions/10311834/how-to-check-if-location-services-are-enabled
//        LocationManager lm = (LocationManager)ctx.getSystemService(Context.LOCATION_SERVICE);
//        boolean gps_enabled = false;
//        boolean network_enabled = false;
//
//        try {
//            gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
//        } catch(Exception ex) {}
//
//        try {
//            network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
//        } catch(Exception ex) {}
//
//        if(!gps_enabled && !network_enabled) {
//        if (!gps_enabled) {
//            // notify user
//            new AlertDialog.Builder(ctx)
//                    .setMessage(R.string.gps_network_not_enabled)
//                    .setPositiveButton(R.string.open_location_settings, new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface paramDialogInterface, int paramInt) {
//                            ctx.startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
//                        }
//                    })
//                            .setNegativeButton(R.string.Cancel,null)
//                            .show();
//        }
//
//
//        // Ende Überprüfung GPS-Standort
//        // App stürzt ab, wenn man aufs Wifi-Fragment klickt und GPS deaktiviert ist
//        // Fehlermeldung: "java.lang.IllegalStateException: You need to use a Theme.AppCompat theme (or descendant) with this activity."


//mirkos testecke start
        LocationManager lm = (LocationManager)ctx.getSystemService(Context.LOCATION_SERVICE);
        boolean gps_enabled = false;
        boolean network_enabled = false;

        try {
            gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch(Exception ex) {}

        if (!gps_enabled) {
            // notify user
            AlertDialog.Builder alert = new AlertDialog.Builder(requireActivity()); //ctx
                alert.setMessage(R.string.gps_network_not_enabled);
                alert.create().show();
        }

//mirkos testecke ende

        arrayList.clear();
        hashmap.clear();

        act.registerReceiver(wifiReceiver, new IntentFilter(
                WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)
        );
        wifiManager.startScan();
        Toast.makeText(act, R.string.scanning_wifi, Toast.LENGTH_SHORT).show();
    }

    private final BroadcastReceiver wifiReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            List<ScanResult> results = wifiManager.getScanResults();
            act.unregisterReceiver(this);

            WifiAccessPoints wifiAccessPoints = new WifiAccessPoints(stateViewModel.getWifiAccessPointsSync(), new Sms());
            WifiAccessPoints.WifiAccessPointList wifiAccessPointsList_bikebean =
                    (WifiAccessPoints.WifiAccessPointList) wifiAccessPoints.getList();

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
                    if (scanResult.BSSID.equals(w.macAddress)) {
                        Integer difference_signal_strength = java.lang.Math.abs(scanResult.level - w.signalStrength);
                        hashmap.put("\u0394 " + difference_signal_strength.toString() + " dBm bei WLAN [" + scanResult.SSID + "]\n(Bikebean: " + w.signalStrength + " dBm | Handy: " + scanResult.level + " dBm)", difference_signal_strength);
                        counter_wifis++;
                    }
                }
            }
            if (counter_wifis == 0) {
                arrayList.add("Keine relevanten WiFi-Access-Points in der Nähe gefunden");
                adapter.notifyDataSetChanged();
            }
            else {
                hashmap.put("VERGLEICH SIGNALSTÄRKEN:",-1);
                //hashmap sortieren
                Object[] a = hashmap.entrySet().toArray();
                Arrays.sort(a, (Comparator) (o1, o2) -> ((Map.Entry<String, Integer>) o1).getValue()
                        .compareTo(((Map.Entry<String, Integer>) o2).getValue()));
                //Aufsteigend nach den Integern sortierte Strings der hashmap an arrayList übergeben
                for (Object e : a) {
                    arrayList.add(((Map.Entry<String, Integer>) e).getKey());
                }
                adapter.notifyDataSetChanged();
            }

            if (counter_bikebean_wifi == 0) {
                textView.setText("BIKEBEAN-WIFI WURDE NICHT GEFUNDEN");
            }
        }
    };

}