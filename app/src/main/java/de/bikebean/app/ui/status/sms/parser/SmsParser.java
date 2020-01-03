package de.bikebean.app.ui.status.sms.parser;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.bikebean.app.MainActivity;
import de.bikebean.app.ui.status.StatusViewModel;
import de.bikebean.app.ui.status.settings.UpdateSettings;

public class SmsParser {

    private final static int SMS_TYPE_POSITION = 0;
    private final static int SMS_TYPE_STATUS = 1;
    private final static int SMS_TYPE_WIFI_ON = 2;
    private final static int SMS_TYPE_WIFI_OFF = 3;
    private final static int SMS_TYPE_WARNING_NUMBER = 4;
    public final static int SMS_TYPE_CELL_TOWERS = 50;  // wapp part 1
    public final static int SMS_TYPE_WIFI_LIST = 51;  // wapp part 2
    private final static int SMS_TYPE_INT = 6;

    // MATCHERS
    // position
    private final Matcher positionMatcher;

    // status
    private final Matcher statusWarningNumberMatcher;
    private final Matcher statusIntervalMatcher;
    private final Matcher statusWifiStatusMatcher;
    private final Matcher statusBatteryStatusMatcher;

    // wifi
    private final Matcher wifiStatusOnMatcher;
    private final Matcher wifiStatusOffMatcher;

    // warning number
    private final Matcher warningNumberMatcher;

    // wapp
    private final Matcher wifiMatcher;
    private final Matcher batteryMatcher;

    // interval
    private final Matcher intervalChangedMatcher;

    public SmsParser(String smsText) {
        Pattern statusWarningNumberPattern = Pattern.compile("(Warningnumber: )([+0-9]{8,})");
        Pattern statusIntervalPattern = Pattern.compile("(Interval: )(1|2|4|8|12|24)(h)");
        Pattern statusWifiStatusPattern = Pattern.compile("(Wifi Status: )(on|off)");
        Pattern statusBatteryStatusPattern = Pattern.compile("(Battery Status: )([0-9]{1,3})(%)");
        Pattern warningNumberPattern = Pattern.compile(
                "(Warningnumber has been changed to )([0-9]{8,})"
        );
        Pattern wifiStatusOnPattern = Pattern.compile(
                "(Wifi is on!)"
        );
        Pattern wifiStatusOffPattern = Pattern.compile(
                "(Wifi Off)"
        );
        Pattern intervalChangedPattern = Pattern.compile(
                "(GSM will be switched on every )(1|2|4|8|12|24)( hour)(s)*([.])"
        );
        Pattern positionPattern = Pattern.compile(
                "([0-9]{3},[0-9]{2},[0-9a-fA-F]+,[0-9a-fA-F]+,[0-9]+)"
        );
        Pattern wifiPattern = Pattern.compile(
                "([0-9a-fA-F]{14})"
        );
        Pattern batteryPattern = Pattern.compile("^([0-9]){1,3}$", Pattern.MULTILINE);

        statusWarningNumberMatcher = statusWarningNumberPattern.matcher(smsText);
        statusIntervalMatcher = statusIntervalPattern.matcher(smsText);
        statusWifiStatusMatcher = statusWifiStatusPattern.matcher(smsText);
        statusBatteryStatusMatcher = statusBatteryStatusPattern.matcher(smsText);
        warningNumberMatcher = warningNumberPattern.matcher(smsText);
        wifiStatusOnMatcher = wifiStatusOnPattern.matcher(smsText);
        wifiStatusOffMatcher = wifiStatusOffPattern.matcher(smsText);
        intervalChangedMatcher = intervalChangedPattern.matcher(smsText);
        positionMatcher = positionPattern.matcher(smsText);
        wifiMatcher = wifiPattern.matcher(smsText);
        batteryMatcher = batteryPattern.matcher(smsText);
    }

    public int getType() {
        int type = -1;

        if (positionMatcher.find() && statusBatteryStatusMatcher.find())
            type = SMS_TYPE_POSITION;
        else if (statusWarningNumberMatcher.find() && statusIntervalMatcher.find() &&
                statusWifiStatusMatcher.find() && statusBatteryStatusMatcher.find())
            type = SMS_TYPE_STATUS;
        else if (wifiStatusOnMatcher.find() && statusBatteryStatusMatcher.find())
            type = SMS_TYPE_WIFI_ON;
        else if (wifiStatusOffMatcher.find() && statusBatteryStatusMatcher.find())
            type = SMS_TYPE_WIFI_OFF;
        else if (warningNumberMatcher.find() && statusBatteryStatusMatcher.find())
            type = SMS_TYPE_WARNING_NUMBER;
        else if (positionMatcher.find())
            type = SMS_TYPE_CELL_TOWERS;
        else if (wifiMatcher.find() && batteryMatcher.find())
            type = SMS_TYPE_WIFI_LIST;
        else if (intervalChangedMatcher.find() && statusBatteryStatusMatcher.find())
            type = SMS_TYPE_INT;

        return type;
    }

    public void updateStatus(Context ctx, int type, StatusViewModel vm) {
        UpdateSettings updateSettings = new UpdateSettings();
        Log.d(MainActivity.TAG, String.format("Detected Type %d", type));

        switch (type) {
            case SMS_TYPE_POSITION:
                // TODO: Update Position Setting
                updateSettings.updateBattery(getStatusBattery(), vm);
                break;
            case SMS_TYPE_STATUS:
                updateSettings.updateWarningNumber(ctx, getStatusWarningNumber());
                updateSettings.updateInterval(ctx, getStatusInterval());
                updateSettings.updateWifi(ctx, getStatusWifi());
                updateSettings.updateBattery(getStatusBattery(), vm);
                break;
            case SMS_TYPE_WIFI_ON:
                updateSettings.updateWifi(ctx, true);
                updateSettings.updateBattery(getStatusBattery(), vm);
                break;
            case SMS_TYPE_WIFI_OFF:
                updateSettings.updateWifi(ctx, false);
                updateSettings.updateBattery(getStatusBattery(), vm);
                break;
            case SMS_TYPE_WARNING_NUMBER:
                updateSettings.updateWarningNumber(ctx, getWarningNumber());
                updateSettings.updateBattery(getStatusBattery(), vm);
                break;
            case SMS_TYPE_CELL_TOWERS:
                updateSettings.updatePosition(getWappCellTowers(), vm);
                break;
            case SMS_TYPE_WIFI_LIST:
                int batteryStatus = getBattery();
                Log.d(MainActivity.TAG, "batteryStatus: " + batteryStatus);

                updateSettings.updateBattery(batteryStatus, vm);
                updateSettings.updateWifiList(getWappWifi(), vm);
                break;
            case SMS_TYPE_INT:
                updateSettings.updateInterval(ctx, getInterval());
                updateSettings.updateBattery(getStatusBattery(), vm);
                break;
        }
    }

    // TODO: implement
    // String getPosition() {}

    private String getStatusWarningNumber() {
        return getMatcherResult(statusWarningNumberMatcher);
    }

    private String getStatusInterval() {
        return getMatcherResult(statusIntervalMatcher);
    }

    private boolean getStatusWifi() {
        String result = getMatcherResult(statusWifiStatusMatcher);
        return result.equals("on");
    }

    private int getStatusBattery() {
        return Integer.parseInt(getMatcherResult(statusBatteryStatusMatcher));
    }

    private String getWarningNumber() {
        return getMatcherResult(warningNumberMatcher);
    }

    private String getWappCellTowers() {
        StringBuilder result = new StringBuilder();
        positionMatcher.reset();

        while (positionMatcher.find()) {
            result.append(positionMatcher.group());
            result.append("\n");
        }

        return result.toString();
    }

    private String getWappWifi() {
        StringBuilder result = new StringBuilder();
        wifiMatcher.reset();

        while (wifiMatcher.find()) {
            result.append(wifiMatcher.group());
            result.append("\n");
        }

        return result.toString();
    }

    private int getBattery() {
        String result = "";
        batteryMatcher.reset();

        while (batteryMatcher.find()) {
            // use the last entry that matches battery specs
            result = batteryMatcher.group();
        }

        return Integer.parseInt(result);
    }

    private String getInterval() {
        return getMatcherResult(intervalChangedMatcher);
    }

    private String getMatcherResult(Matcher m) {
        int count = 0;
        String result = "";
        m.reset();

        while (m.find()) {
            count++;
            result = m.group(2);
            if (count > 1) {
                throw new RuntimeException("There should only be one instance per message.");
            }
        }

        return result;
    }

    public static String parseSMS(String sms, UpdateSettings s, StatusViewModel vm) {
        final Gson gson = new Gson();

        LocationAPIBody locationAPIBody = new LocationAPIBody();
        String smsText1 = sms.split("([.]){4}")[0];
        String smsText2 = sms.split("([.]){4}")[1];

        int numberWifiAccessPoints = parseSms1(smsText1, locationAPIBody);
        int numberCellTowers = parseSms2(smsText2, locationAPIBody);

        Log.d(MainActivity.TAG, "WifiAccessPoints_JsonArray: " + gson.toJson(locationAPIBody.wifiAccessPoints));
        Log.d(MainActivity.TAG, "numberWifiAccessPoints: " + numberWifiAccessPoints);
        Log.d(MainActivity.TAG, "cellTowers_JsonArray: " + gson.toJson(locationAPIBody.cellTowers));
        Log.d(MainActivity.TAG, "numberCellTowers: " + numberCellTowers);

        s.updateNoWifiAccessPoints(numberWifiAccessPoints, vm);
        s.updateNoCellTowers(numberCellTowers, vm);

        // Create final json string
        return gson.toJson(locationAPIBody);
    }

    private static int parseSms1(String sms, LocationAPIBody locationAPIBody) {
        String[] stringArrayWapp = sms.split("\n");
        int numberWifiAccessPoints = stringArrayWapp.length;

        for (String s : stringArrayWapp) {
            if (!s.equals("    ")) {
                // Länge des Substrings ist Unterscheidungskriterium
                WifiAccessPoint wap = new WifiAccessPoint();
                wap.macAddress = s.substring(2);
                wap.signalStrength = Integer.parseInt("-" + s.substring(0, 2));
                wap.toMacAddress();

                locationAPIBody.wifiAccessPoints.add(wap);
            }
        }

        return numberWifiAccessPoints;
    }

    private static int parseSms2(String sms, LocationAPIBody locationAPIBody) {
        String[] stringArrayWapp = sms.split("\n");

        int numberCellTowers = stringArrayWapp.length;

        for (String s : stringArrayWapp) {
            if (!s.equals("    ")) {
                String[] stringArray_gsm_towers = s.split(",");
                CellTower c = new CellTower();

                c.mobileCountryCode = Integer.parseInt(stringArray_gsm_towers[0]);
                c.mobileNetworkCode = Integer.parseInt(stringArray_gsm_towers[1]);
                c.locationAreaCode = Integer.parseInt(stringArray_gsm_towers[2], 16);
                c.cellId = Integer.parseInt(stringArray_gsm_towers[3], 16);
                c.signalStrength = Integer.parseInt("-" + stringArray_gsm_towers[4]);

                locationAPIBody.cellTowers.add(c);
            }
        }

        return numberCellTowers;
    }
}


class WifiAccessPoint {
    String macAddress;
    Integer signalStrength;

    void toMacAddress() {
        String str = macAddress;
        StringBuilder tmp = new StringBuilder();
        final int divisor = 2;

        while(str.length() > 0) {
            String nextChunk = str.substring(0, divisor);
            tmp.append(nextChunk);
            if (str.length() > 2) {
                tmp.append(":");
            }

            str = str.substring(divisor);
        }

        macAddress = tmp.toString();
    }
}


class CellTower {
    Integer mobileCountryCode;
    Integer mobileNetworkCode;
    Integer locationAreaCode;
    Integer cellId;
    Integer signalStrength;
}


class LocationAPIBody {
    final ArrayList<CellTower> cellTowers;
    final ArrayList<WifiAccessPoint> wifiAccessPoints;

    LocationAPIBody() {
        cellTowers = new ArrayList<>();
        wifiAccessPoints = new ArrayList<>();
    }
}
