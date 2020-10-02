package de.bikebean.app.db.settings.settings.number_settings;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

import de.bikebean.app.db.settings.settings.NumberSetting;
import de.bikebean.app.db.settings.settings.WappState;
import de.bikebean.app.db.sms.Sms;
import de.bikebean.app.db.state.State;
import de.bikebean.app.ui.utils.sms.parser.SmsParser;

public class WifiAccessPoints extends NumberSetting {

    private static final State.KEY key = State.KEY.WIFI_ACCESS_POINTS;
    private static final State.KEY numberKey = State.KEY.NO_WIFI_ACCESS_POINTS;

    private final WifiAccessPointList wifiAccessPointList;
    private final int number;
    private final State numberState;

    public static class WifiAccessPointList extends ArrayList<WifiAccessPoint> {
        WifiAccessPointList(String[] stringArrayWapp) {
            parse(stringArrayWapp);
        }

        private void parse(String[] stringArrayWapp) {
            for (String s : stringArrayWapp)
                if (!s.equals("    ") && !s.equals("")) {
                    // Länge des Substrings ist Unterscheidungskriterium
                    WifiAccessPoint wap = new WifiAccessPoint();
                    wap.macAddress = s.substring(2);
                    wap.signalStrength = Integer.parseInt("-" + s.substring(0, 2));
                    wap.toMacAddress();

                    this.add(wap);
                }
        }
    }
    public static class WifiAccessPoint extends RawNumberSettings {
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

    public WifiAccessPoints(String wifiAccessPoints, Sms sms) {
        super(wifiAccessPoints, sms, key);

        String[] strings = mWappString.split("\n");
        number = strings.length;

        wifiAccessPointList = new WifiAccessPointList(strings);
        numberState = new State(
                getDate(), numberKey,
                (double) getNumber(), "",
                State.STATUS.CONFIRMED, getId()
        );
    }

    public WifiAccessPoints(@NonNull SmsParser smsParser) {
        super(smsParser.getWappWifiAccessPoints(), smsParser.getSms(), key);

        String[] strings = mWappString.split("\n");
        number = strings.length;

        wifiAccessPointList = new WifiAccessPointList(strings);
        numberState = new State(
                getDate(), numberKey,
                (double) getNumber(), "",
                State.STATUS.CONFIRMED, getId()
        );
    }

    public WifiAccessPoints(@NonNull WappState wappState) {
        super(wappState.getWifiAccessPoints().getLongValue(), wappState.getSms(), key);

        String[] strings = mWappString.split("\n");
        number = strings.length;

        wifiAccessPointList = new WifiAccessPointList(strings);
        numberState = new State(
                getDate(), numberKey,
                (double) getNumber(), "",
                State.STATUS.CONFIRMED, getId()
        );
    }

    public WifiAccessPoints() {
        super("", key);

        wifiAccessPointList = new WifiAccessPointList(new String[]{});
        number = 0;
        numberState = new State();
    }

    @Override
    public List<? extends RawNumberSettings> getList() {
        return wifiAccessPointList;
    }

    @Override
    public int getNumber() {
        return number;
    }

    @Override
    public State getNumberState() {
        return numberState;
    }
}
