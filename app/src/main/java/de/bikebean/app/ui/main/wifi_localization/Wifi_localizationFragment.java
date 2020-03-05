package de.bikebean.app.ui.main.wifi_localization;

import android.Manifest;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.text.Html;
import android.util.Log;
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
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import de.bikebean.app.MainActivity;
import de.bikebean.app.R;
import de.bikebean.app.db.settings.settings.number_settings.WifiAccessPoints;
import de.bikebean.app.db.sms.Sms;
import de.bikebean.app.db.state.State;
import de.bikebean.app.ui.main.status.StateViewModel;
import java.lang.Math;

public class Wifi_localizationFragment extends Fragment {

    public String bikebean_wifi_name = "BikeBean.de";
    private Context ctx;
    private FragmentActivity act;
    private WifiManager wifiManager;
    private ListView listView;
    private TextView textView;
    private Button buttonScan;
    private List<ScanResult> results;
    private ArrayList<String> arrayList = new ArrayList<>();
    private ArrayAdapter adapter;
    private HashMap<String, Integer> hashmap;

    private StateViewModel stateViewModel;
//    private ApiParser apiParser;

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
        act = Objects.requireNonNull(getActivity());
        ctx = act.getApplicationContext();

        // hide the actionbar for this fragment
        ActionBar actionbar = ((AppCompatActivity) act).getSupportActionBar();
        Objects.requireNonNull(actionbar).hide();

        buttonScan.setOnClickListener(view -> scanWifi());
        adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, arrayList);
        listView.setAdapter(adapter);

        stateViewModel = new ViewModelProvider(getActivity()).get(StateViewModel.class);

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

//        //TODO: IMPLEMTIERUNG ÜBER HILFSKLASSE ÜBERPRÜFEN -> https://stackoverflow.com/questions/12320857/how-to-get-my-activity-context
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
//        //TODO: Ende Überprüfung GPS-Standort
//        // App stürzt ab, wenn man aufs Wifi-Fragment klickt und GPS deaktiviert ist
//        // Fehlermeldung: "java.lang.IllegalStateException: You need to use a Theme.AppCompat theme (or descendant) with this activity."


        arrayList.clear();
        act.registerReceiver(wifiReceiver, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
        wifiManager.startScan();
        Toast.makeText(act, R.string.scanning_wifi, Toast.LENGTH_SHORT).show();
    }



    private BroadcastReceiver wifiReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            results = wifiManager.getScanResults();
            WifiAccessPoints wifiAccessPoints = new WifiAccessPoints(stateViewModel.getWifiAccessPointsSync(), new Sms());
            WifiAccessPoints.WifiAccessPointList wifiAccessPointsList_bikebean = wifiAccessPoints.getWifiAccessPoints();

            act.unregisterReceiver(this);

            HashMap<String, Integer> hashmap = new HashMap<String, Integer>();
            Integer counter_bikebean_wifi = 0;
            Integer counter_wifis = 0;

            for (WifiAccessPoints.WifiAccessPoint w : wifiAccessPointsList_bikebean){
                for (ScanResult scanResult : results) {
                    if (scanResult.SSID.equals(bikebean_wifi_name)){
                        String html_bikebean_wifi_found = "<b>BIKEBEAN-WIFI GEFUNDEN</b><br>Signalstärke: " + scanResult.level + " dBm";
                        textView.setText(Html.fromHtml(html_bikebean_wifi_found));
                        counter_bikebean_wifi++;
                    }
                    if (scanResult.BSSID.equals(w.macAddress)) {
                        Integer difference_signal_strength = java.lang.Math.abs(scanResult.level - w.signalStrength);
                        hashmap.put("\u0394 " + difference_signal_strength.toString() + " dBm : [" + scanResult.SSID + "]\n(Bikebean: " + w.signalStrength + " dBm | Handy: " + scanResult.level + " dBm)", difference_signal_strength);
                        adapter.notifyDataSetChanged();
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
                Arrays.sort(a, new Comparator() {
                    public int compare(Object o1, Object o2) {
                        return ((Map.Entry<String, Integer>) o1).getValue()
                                .compareTo(((Map.Entry<String, Integer>) o2).getValue());
                    }
                });
                //sortierte Strings der hashmap an arraylist übergeben
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