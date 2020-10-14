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

    private static final @NonNull State.KEY key = State.KEY.WIFI_ACCESS_POINTS;
    private static final @NonNull State.KEY numberKey = State.KEY.NO_WIFI_ACCESS_POINTS;

    private final @NonNull WifiAccessPointList wifiAccessPointList;
    private final int number;
    private final @NonNull State numberState;

    public static class WifiAccessPointList extends ArrayList<WifiAccessPoint> {
        WifiAccessPointList(final @NonNull String[] stringArrayWapp) {
            parse(stringArrayWapp);
        }

        private void parse(final @NonNull String[] stringArrayWapp) {
            for (final @NonNull String s : stringArrayWapp)
                if (!s.equals("    ") && !s.equals("")) {
                    // Länge des Substrings ist Unterscheidungskriterium
                    final @NonNull WifiAccessPoint wap = new WifiAccessPoint();
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
            @NonNull String str = macAddress;
            final @NonNull StringBuilder tmp = new StringBuilder();
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

    public WifiAccessPoints(final @NonNull String wifiAccessPoints, final @NonNull Sms sms) {
        super(wifiAccessPoints, sms, key);

        final @NonNull String[] strings = mWappString.split("\n");
        number = strings.length;

        wifiAccessPointList = new WifiAccessPointList(strings);
        numberState = new State(
                getDate(), numberKey,
                (double) getNumber(), "",
                State.STATUS.CONFIRMED, getId()
        );
    }

    public WifiAccessPoints(final @NonNull SmsParser smsParser) {
        super(smsParser.getWappWifiAccessPoints(), smsParser.getSms(), key);

        final @NonNull String[] strings = mWappString.split("\n");
        number = strings.length;

        wifiAccessPointList = new WifiAccessPointList(strings);
        numberState = new State(
                getDate(), numberKey,
                (double) getNumber(), "",
                State.STATUS.CONFIRMED, getId()
        );
    }

    public WifiAccessPoints(final @NonNull WappState wappState) {
        super(wappState.getWifiAccessPoints().getLongValue(), wappState.getSms(), key);

        final @NonNull String[] strings = mWappString.split("\n");
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
    public @NonNull List<? extends RawNumberSettings> getList() {
        return wifiAccessPointList;
    }

    @Override
    public int getNumber() {
        return number;
    }

    @Override
    public @NonNull State getNumberState() {
        return numberState;
    }
}
