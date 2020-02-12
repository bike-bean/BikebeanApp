package de.bikebean.app.ui.status.sms.parser;

import android.os.AsyncTask;
import android.util.Log;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.bikebean.app.MainActivity;
import de.bikebean.app.db.settings.Battery;
import de.bikebean.app.db.settings.CellTowers;
import de.bikebean.app.db.settings.Interval;
import de.bikebean.app.db.settings.Setting;
import de.bikebean.app.db.settings.Wapp;
import de.bikebean.app.db.settings.WarningNumber;
import de.bikebean.app.db.settings.Wifi;
import de.bikebean.app.db.settings.WifiAccessPoints;
import de.bikebean.app.db.sms.Sms;
import de.bikebean.app.db.state.State;
import de.bikebean.app.ui.status.StateViewModel;
import de.bikebean.app.ui.status.sms.SmsViewModel;

public class SmsParser extends AsyncTask<String, Void, Boolean> {

    private final static int SMS_TYPE_POSITION = 0;
    private final static int SMS_TYPE_STATUS = 1;
    private final static int SMS_TYPE_WIFI_ON = 2;
    private final static int SMS_TYPE_WIFI_OFF = 3;
    private final static int SMS_TYPE_WARNING_NUMBER = 4;
    private final static int SMS_TYPE_CELL_TOWERS = 50;  // wapp part 1
    private final static int SMS_TYPE_WIFI_LIST = 51;  // wapp part 2
    private final static int SMS_TYPE_INT = 6;

    private final WeakReference<StateViewModel> statusViewModelReference;
    private final WeakReference<SmsViewModel> smsViewModelReference;

    private final Sms sms;
    private int type;

    public SmsParser(Sms sms, StateViewModel stateViewModel, SmsViewModel smsViewModel) {
        this.sms = sms;
        statusViewModelReference = new WeakReference<>(stateViewModel);
        smsViewModelReference = new WeakReference<>(smsViewModel);

        initMatchers(sms.getBody());
    }

    public SmsParser(Sms sms) {
        this.sms = sms;
        statusViewModelReference = new WeakReference<>(null);
        smsViewModelReference = new WeakReference<>(null);

        initMatchers(sms.getBody());

        this.type = getType();

        if (!(type > 0))
            Log.w(MainActivity.TAG, "Could not parse SMS: " + sms.getBody());
    }

    @Override
    protected Boolean doInBackground(String... args) {
        // Parse Sms to get which type it is
        type = getType();
        Log.d(MainActivity.TAG, String.format("Detected Type %d", type));

        // Update the status entries for the status db and the user preferences
        List<Setting> l = getSettingList();
        List<State> newStateEntries = l.get(0).updatePreferences(l);

        // add each status entry to the status viewModel ( -> database )
        for (State state : newStateEntries)
            statusViewModelReference.get().insert(state);

        return true;
    }

    @Override
    protected void onPostExecute(Boolean isDatabaseUpdated) {
        smsViewModelReference.get().markParsed(sms.getId());
    }

    // MATCHERS
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

    private void initMatchers(String smsText) {
        Pattern statusWarningNumberPattern = Pattern.compile("(Warningnumber: )([+0-9]{8,})");
        Pattern statusIntervalPattern = Pattern.compile("(Interval: )(1|2|4|8|12|24)(h)");
        Pattern statusWifiStatusPattern = Pattern.compile("(Wifi Status: )(on|off)");
        Pattern statusBatteryStatusPattern = Pattern.compile("(Battery Status: )([0-9]{1,3})(%)");
        Pattern warningNumberPattern = Pattern.compile(
                "(Warningnumber has been changed to )([+0-9]{8,})"
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

    private int getType() {
        int type = -1;

        if (positionMatcher.find() && statusBatteryStatusMatcher.find())
            type = SMS_TYPE_POSITION;
        else if (statusWarningNumberMatcher.find() && statusIntervalMatcher.find() &&
                statusWifiStatusMatcher.find() && statusBatteryStatusMatcher.find())
            type = SMS_TYPE_STATUS;
        else if (statusIntervalMatcher.find() && statusWifiStatusMatcher.find() &&
                statusBatteryStatusMatcher.find()) {
            Log.w(MainActivity.TAG, "Warningnumber is not set!");
            type = SMS_TYPE_STATUS;
        } else if (wifiStatusOnMatcher.find() && statusBatteryStatusMatcher.find())
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

    public List<Setting> getSettingList() {
        List<Setting> l = new ArrayList<>();

        // add entries based on sms type
        switch (type) {
            case SMS_TYPE_POSITION:
                break;
            case SMS_TYPE_STATUS:
                l.add(new WarningNumber(getStatusWarningNumber(), sms));
                l.add(new Interval(getStatusInterval(), sms));
                l.add(new Wifi(getStatusWifi(), sms));
                l.add(new Battery(getStatusBattery(), sms));
                l.add(new de.bikebean.app.db.settings.Status(0.0, sms));
                break;
            case SMS_TYPE_WIFI_ON:
                l.add(new Battery(getStatusBattery(), sms));
                l.add(new Wifi(true, sms));
                l.add(new de.bikebean.app.db.settings.Status(0.0, sms));
                break;
            case SMS_TYPE_WIFI_OFF:
                l.add(new Battery(getStatusBattery(), sms));
                l.add(new Wifi(false, sms));
                l.add(new de.bikebean.app.db.settings.Status(0.0, sms));
                break;
            case SMS_TYPE_WARNING_NUMBER:
                l.add(new Battery(getStatusBattery(), sms));
                l.add(new WarningNumber(getWarningNumber(), sms));
                l.add(new de.bikebean.app.db.settings.Status(0.0, sms));
                break;
            case SMS_TYPE_INT:
                l.add(new Battery(getStatusBattery(), sms));
                l.add(new Interval(getInterval(), sms));
                l.add(new de.bikebean.app.db.settings.Status(0.0, sms));
                break;
            case SMS_TYPE_CELL_TOWERS:
                // no battery entry in this special case
                l.add(new CellTowers(getWappCellTowers(), sms));
                l.add(new Wapp(State.WAPP_CELL_TOWERS, sms));
                break;
            case SMS_TYPE_WIFI_LIST:
                // battery value is encoded differently in this case
                l.add(new Battery(getBattery(), sms));
                l.add(new WifiAccessPoints(getWappWifi(), sms));
                l.add(new Wapp(State.WAPP_WIFI_ACCESS_POINTS, sms));
                break;
        }

        return l;
    }

    private String getStatusWarningNumber() {
        return getMatcherResult(statusWarningNumberMatcher);
    }

    private int getStatusInterval() {
        return Integer.valueOf(getMatcherResult(statusIntervalMatcher));
    }

    private boolean getStatusWifi() {
        String result = getMatcherResult(statusWifiStatusMatcher);
        return result.equals("on");
    }

    private double getStatusBattery() {
        String result = getMatcherResult(statusBatteryStatusMatcher);

        if (!result.isEmpty())
            return Double.parseDouble(result);
        else return 0.0;
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

    private double getBattery() {
        String result = "";
        batteryMatcher.reset();

        while (batteryMatcher.find()) {
            // use the last entry that matches battery specs
            result = batteryMatcher.group();
        }

        return Double.parseDouble(result);
    }

    private int getInterval() {
        return Integer.valueOf(getMatcherResult(intervalChangedMatcher));
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

