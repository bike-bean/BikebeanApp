package de.bikebean.app.db.settings.settings.number_settings;

import java.util.ArrayList;
import java.util.List;

import de.bikebean.app.db.settings.settings.NumberSetting;
import de.bikebean.app.db.settings.settings.Wapp;
import de.bikebean.app.db.sms.Sms;
import de.bikebean.app.db.state.State;

public class WifiAccessPoints extends NumberSetting {

    private static final State.KEY key = State.KEY.WIFI_ACCESS_POINTS;
    private static final State.KEY numberKey = State.KEY.NO_WIFI_ACCESS_POINTS;

    private WifiAccessPointList wifiAccessPointList;

    public class WifiAccessPointList extends ArrayList<WifiAccessPoint> {}

    public class WifiAccessPoint extends RawNumberSettings {
        public String macAddress;
        public Integer signalStrength;

        void toMacAddress() {
            String str = macAddress;
            StringBuilder tmp = new StringBuilder();
            final int divisor = 2;

            while (str.length() > 0) {
                String nextChunk = str.substring(0, divisor);
                tmp.append(nextChunk);
                if (str.length() > 2)
                    tmp.append(":");

                str = str.substring(divisor);
            }

            macAddress = tmp.toString();
        }
    }

    private final String wifiAccessPoints;

    public WifiAccessPoints(String wifiAccessPoints, Sms sms) {
        super(sms, key, numberKey, wifiAccessPoints);
        this.wifiAccessPoints = wifiAccessPoints;
    }

    public WifiAccessPoints(Wapp wapp, Sms sms) {
        super(sms, key, numberKey, wapp.getWifiAccessPoints().getLongValue());
        this.wifiAccessPoints = wapp.getWifiAccessPoints().getLongValue();
    }

    public WifiAccessPoints() {
        super(key);
        this.wifiAccessPoints = "";
    }

    @Override
    protected void initList() {
        wifiAccessPointList = new WifiAccessPointList();
    }

    @Override
    protected void parseSplitString(String wifiAccessPoints) {
        stringArrayWapp = wifiAccessPoints.split("\n");
    }

    @Override
    protected void parseNumber() {
        number = stringArrayWapp.length;
    }

    @Override
    protected void parse(String wifiAccessPoints) {
        if (wifiAccessPoints.isEmpty())
            return;

        for (String s : stringArrayWapp)
            if (!s.equals("    ")) {
                // Länge des Substrings ist Unterscheidungskriterium
                WifiAccessPoint wap = new WifiAccessPoint();
                wap.macAddress = s.substring(2);
                wap.signalStrength = Integer.parseInt("-" + s.substring(0, 2));
                wap.toMacAddress();

                wifiAccessPointList.add(wap);
            }
    }

    @Override
    public List<? extends RawNumberSettings> getList() {
        return wifiAccessPointList;
    }

    public WifiAccessPointList getWifiAccessPoints() {
        return (WifiAccessPointList) getList();
    }

    @Override
    public String get() {
        return wifiAccessPoints;
    }
}
