package de.bikebean.app.ui.main.wifi_localization;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
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
import android.widget.Toast;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import de.bikebean.app.MainActivity;
import de.bikebean.app.R;
import de.bikebean.app.db.settings.settings.number_settings.WifiAccessPoints;
import de.bikebean.app.db.sms.Sms;
import de.bikebean.app.db.state.State;
import de.bikebean.app.ui.main.status.StateViewModel;

public class Wifi_localizationFragment extends Fragment {

    //TODO:
    // Bikebean Wifi-Name anpassen
    private final String BIKEBEAN_WIFI_NAME = "LS_WLAN";

    private Context ctx;
    private FragmentActivity act;
    private WifiManager wifiManager;
    private ListView listView;
    private Button buttonScan;
    private List<ScanResult> results;
    private ArrayList<String> arrayList = new ArrayList<>();
    private ArrayAdapter adapter;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_wifi_localization, container, false);

        buttonScan = root.findViewById(R.id.scanBtn);
        listView = root.findViewById(R.id.wifiList);

        return root;
    }

    // LM: Methode onActivityCreated: Alternative in einem Fragment (Superklasse: Fragment) statt zu erben von Superklasse AppCompatActivty (Implementierung: this = getActivity())
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

        StateViewModel stateViewModel = new ViewModelProvider(this).get(StateViewModel.class);
        WifiAccessPoints wifiAccessPoints = new WifiAccessPoints(stateViewModel.getWifiAccessPointsSync(), new Sms());
        WifiAccessPoints.WifiAccessPointList wifiAccessPointList = wifiAccessPoints.getWifiAccessPoints();

        for (WifiAccessPoints.WifiAccessPoint w : wifiAccessPointList) {
            // use w.macAddress;
            // use w.signalStrength;
        }

        //TODO:
        // Überprüfen, ob GPS-Standort eingeschaltet ist -> Nur dann können mittlerweile WLAN-Netzwerke gelesen werden
        // https://stackoverflow.com/questions/10311834/how-to-check-if-location-services-are-enabled

        arrayList.clear();
        act.registerReceiver(wifiReceiver, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
        wifiManager.startScan();
        Toast.makeText(act, R.string.scanning_wifi, Toast.LENGTH_SHORT).show();
    }

    private BroadcastReceiver wifiReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            results = wifiManager.getScanResults();

            act.unregisterReceiver(this);

            for (ScanResult scanResult : results) {
                if (scanResult.SSID.equals(BIKEBEAN_WIFI_NAME)) {
                    arrayList.add("[" + scanResult.BSSID + "], " + scanResult.SSID + ", " + scanResult.level + "dBm");
                    Log.d(MainActivity.TAG, scanResult.toString());
                }
                adapter.notifyDataSetChanged();
            }
        }
    };
}