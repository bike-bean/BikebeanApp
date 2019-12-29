package de.bikebean.app.ui.status.sms.parser;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.preference.PreferenceManager;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.bikebean.app.MainActivity;
import de.bikebean.app.ui.status.geolocationapi.GeolocationAPI;
import de.bikebean.app.ui.status.settings.UpdateSettings;

public class SmsParser {

    private final Gson gson = new Gson();

    private String parseSMS(String smsText, UpdateSettings s, Context ctx) {
        LocationAPIBody locationAPIBody = new LocationAPIBody();
        String smsText1 = smsText.split("([.]){4}")[0];
        String smsText2 = smsText.split("([.]){4}")[1];

        int numberWifiAccessPoints = parseSms_1(smsText1, locationAPIBody);
        int numberCellTowers = parseSms_2(smsText2, locationAPIBody);

        Log.d(MainActivity.TAG, "WifiAccessPoints_JsonArray: " + gson.toJson(locationAPIBody.wifiAccessPoints));
        Log.d(MainActivity.TAG, "numberWifiAccessPoints: " + numberWifiAccessPoints);
        Log.d(MainActivity.TAG, "cellTowers_JsonArray: " + gson.toJson(locationAPIBody.cellTowers));
        Log.d(MainActivity.TAG, "numberCellTowers: " + numberCellTowers);

        s.updateNoWifiAccessPoints(ctx, numberWifiAccessPoints);
        s.updateNoCellTowers(ctx, numberCellTowers);

        // Create final json string
        return gson.toJson(locationAPIBody);
    }

    private int parseSms_1(String smsText, LocationAPIBody locationAPIBody) {
        String[] stringArrayWapp = smsText.split("\n");
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

    private int parseSms_2(String smsText, LocationAPIBody locationAPIBody) {
        String[] stringArrayWapp = smsText.split("\n");

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

    public void updateStatus(Context ctx, String smsText) {
        SMSTypes smsTypes = new SMSTypes(smsText);
        UpdateSettings updateSettings = new UpdateSettings();
        GeolocationAPI geolocationAPI = new GeolocationAPI(ctx);
        int type = smsTypes.getSmsType();
        Log.d(MainActivity.TAG, String.format("Detected Type %d", type));

        switch (type) {
            case SMSTypes.SMS_TYPE_POSITION:
                // TODO: Update Position Setting
                updateSettings.updateBattery(ctx, smsTypes.getStatusBattery());
                break;
            case SMSTypes.SMS_TYPE_STATUS:
                updateSettings.updateWarningNumber(ctx, smsTypes.getStatusWarningNumber());
                updateSettings.updateInterval(ctx, smsTypes.getStatusInterval());
                updateSettings.updateWifi(ctx, smsTypes.getStatusWifi());
                updateSettings.updateBattery(ctx, smsTypes.getStatusBattery());
                break;
            case SMSTypes.SMS_TYPE_WIFI_ON:
                updateSettings.updateWifi(ctx, true);
                updateSettings.updateBattery(ctx, smsTypes.getStatusBattery());
                break;
            case SMSTypes.SMS_TYPE_WIFI_OFF:
                updateSettings.updateWifi(ctx, false);
                updateSettings.updateBattery(ctx, smsTypes.getStatusBattery());
                break;
            case SMSTypes.SMS_TYPE_WARNING_NUMBER:
                updateSettings.updateWarningNumber(ctx, smsTypes.getWarningNumber());
                updateSettings.updateBattery(ctx, smsTypes.getStatusBattery());
                break;
            case SMSTypes.SMS_TYPE_WAPP:
                String wappCellTowers = smsTypes.getWappCellTowers();
                int batteryStatus = smsTypes.getBattery();
                String wappWifi = smsTypes.getWappWifi();
                updateSettings.updateBattery(ctx, batteryStatus);
                Log.d(MainActivity.TAG, "batteryStatus: " + batteryStatus);

                String requestBody = parseSMS(wappWifi + "...." + wappCellTowers, updateSettings, ctx);
                updateSettings.updatePosition(ctx, wappCellTowers);
                updateSettings.updateWifiList(ctx, wappWifi);
                geolocationAPI.httpPOST(requestBody, updateSettings);
                break;
            case SMSTypes.SMS_TYPE_INT:
                updateSettings.updateInterval(ctx, smsTypes.getInterval());
                updateSettings.updateBattery(ctx, smsTypes.getStatusBattery());
                break;
        }
    }

    public void updateLatLng(Context ctx) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(ctx);
        UpdateSettings updateSettings = new UpdateSettings();
        GeolocationAPI geolocationAPI = new GeolocationAPI(ctx);

        String wappCellTowers = sharedPreferences.getString("location", "");
        String wappWifi = sharedPreferences.getString("wifiList", "");

        Log.d(MainActivity.TAG, "Updating Lat/Lng...");
        Log.d(MainActivity.TAG, wappCellTowers);
        Log.d(MainActivity.TAG, wappWifi);

        String requestBody = parseSMS(wappWifi + "...." + wappCellTowers, updateSettings, ctx);
        geolocationAPI.httpPOST(requestBody, updateSettings);
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
    ArrayList<CellTower> cellTowers;
    ArrayList<WifiAccessPoint> wifiAccessPoints;

    LocationAPIBody() {
        cellTowers = new ArrayList<>();
        wifiAccessPoints = new ArrayList<>();
    }
}


class SMSTypes {
    final static int SMS_TYPE_POSITION = 0;
    final static int SMS_TYPE_STATUS = 1;
    final static int SMS_TYPE_WIFI_ON = 2;
    final static int SMS_TYPE_WIFI_OFF = 3;
    final static int SMS_TYPE_WARNING_NUMBER = 4;
    final static int SMS_TYPE_WAPP = 5;
    final static int SMS_TYPE_INT = 6;

    // position
    private Matcher positionMatcher;

    // status
    private Matcher statusWarningNumberMatcher;
    private Matcher statusIntervalMatcher;
    private Matcher statusWifiStatusMatcher;
    private Matcher statusBatteryStatusMatcher;

    // wifi
    private Matcher wifiStatusOnMatcher;
    private Matcher wifiStatusOffMatcher;

    // warning number
    private Matcher warningNumberMatcher;

    // wapp
    private Matcher wifiMatcher;
    private Matcher batteryMatcher;

    // interval
    private Matcher intervalChangedMatcher;

    SMSTypes(String smsText) {
        Pattern statusWarningNumberPattern = Pattern.compile("(Warningnumber: )([0-9]{8,})");
        Pattern statusIntervalPattern = Pattern.compile("(Interval: )(1|2|4|8|12|24)( h)");
        Pattern statusWifiStatusPattern = Pattern.compile("(Wifi Status: )(on|off)");
        Pattern statusBatteryStatusPattern = Pattern.compile("(Battery Status: )([0-9]{1,3})( %)");
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

    int getSmsType() {
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
        else if (wifiMatcher.find() && batteryMatcher.find() &&
                positionMatcher.find())
            type = SMS_TYPE_WAPP;
        else if (intervalChangedMatcher.find() && statusBatteryStatusMatcher.find())
            type = SMS_TYPE_INT;

        return type;
    }

    // TODO: implement
    // String getPosition() {}

    String getStatusWarningNumber() {
        return getMatcherResult(statusWarningNumberMatcher);
    }

    String getStatusInterval() {
        return getMatcherResult(statusIntervalMatcher);
    }

    boolean getStatusWifi() {
        String result = getMatcherResult(statusWifiStatusMatcher);
        return result.equals("on");
    }

    int getStatusBattery() {
        return Integer.parseInt(getMatcherResult(statusBatteryStatusMatcher));
    }

    String getWarningNumber() {
        return getMatcherResult(warningNumberMatcher);
    }

    String getWappCellTowers() {
        StringBuilder result = new StringBuilder();
        positionMatcher.reset();

        while (positionMatcher.find()) {
            result.append(positionMatcher.group());
            result.append("\n");
        }

        return result.toString();
    }

    String getWappWifi() {
        StringBuilder result = new StringBuilder();
        wifiMatcher.reset();

        while (wifiMatcher.find()) {
            result.append(wifiMatcher.group());
            result.append("\n");
        }

        return result.toString();
    }

    int getBattery() {
        int count = 0;
        StringBuilder result = new StringBuilder();
        batteryMatcher.reset();

        while (batteryMatcher.find()) {
            count++;
            result.append(batteryMatcher.group());
            if (count > 1) {
                throw new RuntimeException("There should only be one battery status per message.");
            }
        }

        return Integer.parseInt(result.toString());
    }

    String getInterval() {
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
}
