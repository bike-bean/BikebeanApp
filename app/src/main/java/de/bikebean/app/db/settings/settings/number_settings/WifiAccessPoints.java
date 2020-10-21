package de.bikebean.app.db.settings.settings.number_settings;

import androidx.annotation.NonNull;

import java.util.ArrayList;

import de.bikebean.app.db.settings.settings.NumberSetting;
import de.bikebean.app.db.settings.settings.WappState;
import de.bikebean.app.db.sms.Sms;
import de.bikebean.app.db.state.State;
import de.bikebean.app.db.state.StateFactory;

public class WifiAccessPoints extends NumberSetting {

    private static final @NonNull State.KEY key = State.KEY.WIFI_ACCESS_POINTS;
    private static final @NonNull State.KEY numberKey = State.KEY.NO_WIFI_ACCESS_POINTS;

    public WifiAccessPoints(final @NonNull String wifiAccessPoints, final @NonNull Sms sms) {
        super(wifiAccessPoints, sms, key,
                StateFactory.createNumberState(
                        sms, numberKey,
                        getNumberFromString(wifiAccessPoints), State.STATUS.CONFIRMED
                ),
                new WifiAccessPointList(wifiAccessPoints.split("\n"))
        );
    }

    public WifiAccessPoints(final @NonNull Sms sms,
                            final @NonNull WifiAccessPointsGetter wifiAccessPointsGetter) {
        this(wifiAccessPointsGetter.getWifiAccessPoints(), sms);
    }

    public WifiAccessPoints(final @NonNull WappState wappState) {
        this(wappState.getWifiAccessPoints().getLongValue(), wappState.getSms());
    }

    public WifiAccessPoints() {
        super("", key, StateFactory.createNullState(),
                new WifiAccessPointList(new String[]{})
        );
    }

    public interface WifiAccessPointsGetter {
        @NonNull String getWifiAccessPoints();
    }

    private static int getNumberFromString(final @NonNull String string) {
        if (string.isEmpty())
            return 0;
        else return string.split("\n").length;
    }

    public static class WifiAccessPointList extends ArrayList<WifiAccessPoint> {
        WifiAccessPointList(final @NonNull String[] stringArrayWapp) {
            parse(stringArrayWapp);
        }

        private void parse(final @NonNull String[] stringArrayWapp) {
            for (final @NonNull String s : stringArrayWapp)
                if (!s.equals("    ") && !s.isEmpty()) {
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
}
