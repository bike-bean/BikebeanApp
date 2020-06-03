package de.bikebean.app.ui.utils.sms.parser;

import android.os.AsyncTask;
import android.util.Log;

import java.lang.ref.WeakReference;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.bikebean.app.MainActivity;
import de.bikebean.app.db.settings.settings.Battery;
import de.bikebean.app.db.settings.settings.number_settings.CellTowers;
import de.bikebean.app.db.settings.settings.Interval;
import de.bikebean.app.db.settings.settings.LowBattery;
import de.bikebean.app.ui.initialization.SettingsList;
import de.bikebean.app.db.settings.settings.Wapp;
import de.bikebean.app.db.settings.settings.WarningNumber;
import de.bikebean.app.db.settings.settings.Wifi;
import de.bikebean.app.db.settings.settings.number_settings.WifiAccessPoints;
import de.bikebean.app.db.sms.Sms;
import de.bikebean.app.db.state.State;
import de.bikebean.app.ui.main.status.menu.log.LogViewModel;
import de.bikebean.app.ui.main.status.StateViewModel;
import de.bikebean.app.ui.main.status.menu.sms_history.SmsViewModel;

public class SmsParser extends AsyncTask<String, Void, Boolean> {

    private enum SMS_TYPE {
        POSITION, _STATUS, WIFI_ON, WIFI_OFF, INT, LOW_BATTERY,
        WARNING_NUMBER, CELL_TOWERS, WIFI_LIST, NO_WIFI_LIST, UNDEFINED
    }

    private final WeakReference<StateViewModel> statusViewModelReference;
    private final WeakReference<SmsViewModel> smsViewModelReference;
    private final WeakReference<LogViewModel> logViewModelReference;

    private final Sms sms;
    private SMS_TYPE type;

    public SmsParser(Sms sms, StateViewModel stateViewModel,
                     SmsViewModel smsViewModel, LogViewModel logViewModel) {
        this.sms = sms;
        statusViewModelReference = new WeakReference<>(stateViewModel);
        smsViewModelReference = new WeakReference<>(smsViewModel);
        logViewModelReference = new WeakReference<>(logViewModel);

        initMatchers(sms.getBody());
    }

    public SmsParser(Sms sms, LogViewModel logViewModel) {
        this.sms = sms;
        statusViewModelReference = new WeakReference<>(null);
        smsViewModelReference = new WeakReference<>(null);
        logViewModelReference = new WeakReference<>(logViewModel);

        initMatchers(sms.getBody());

        this.type = getType();

        if (type.equals(SMS_TYPE.UNDEFINED))
            logViewModelReference.get().w("Could not parse SMS: " + sms.getBody());
    }

    @Override
    protected Boolean doInBackground(String... args) {
        // Parse Sms to get which type it is
        type = getType();
        Log.d(MainActivity.TAG, String.format("Detected Type %d", type.ordinal()));

        // Update the status entries for the status db and the user preferences
        SettingsList settings = getSettingList();

        if (settings.size() > 0)
            // add each status entry to the status viewModel ( -> database )
            statusViewModelReference.get().insert(settings);
        else
            logViewModelReference.get().e("Could not parse SMS: " + sms.getBody());

        return true;
    }

    @Override
    protected void onPostExecute(Boolean isDatabaseUpdated) {
        if (isDatabaseUpdated)
            smsViewModelReference.get().markParsed(sms);
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
    private Matcher noWifiMatcher;
    private Matcher batteryMatcher;

    // interval
    private Matcher intervalChangedMatcher;

    private Matcher lowBatteryMatcher;

    private void initMatchers(String smsText) {
        Pattern statusWarningNumberPattern = Pattern.compile("(Warningnumber: )([+0-9]{8,})");
        Pattern statusIntervalPattern = Pattern.compile("(Interval: )(1|2|4|8|12|24)(h)");
        Pattern statusWifiStatusPattern = Pattern.compile("(Wifi Status: )(on|off)");
        Pattern statusBatteryStatusPattern = Pattern.compile("(Battery Status: )([0-9]{1,3})(%)");
        Pattern warningNumberPattern = Pattern.compile(
                "(Warningnumber has been changed to )([+0-9]{8,})"
        );
        Pattern wifiStatusOnPattern = Pattern.compile("(Wifi is on!)");
        Pattern wifiStatusOffPattern = Pattern.compile("(Wifi Off)");
        Pattern intervalChangedPattern = Pattern.compile(
                "(GSM will be switched on every )(1|2|4|8|12|24)( hour)(s)*([.])"
        );
        Pattern positionPattern = Pattern.compile(
                "([0-9]{3},[0-9]{2},[0-9a-fA-F]+,[0-9a-fA-F]+,[0-9]+)"
        );
        Pattern wifiPattern = Pattern.compile("([0-9a-fA-F]{14})");
        Pattern noWifiPattern = Pattern.compile("(no wifi available)([0-9]){1,3}");
        Pattern batteryPattern = Pattern.compile("^([0-9]){1,3}$", Pattern.MULTILINE);
        Pattern lowBatteryPattern = Pattern.compile("(BATTERY LOW!\\nBATTERY STATUS: )([0-9]{1,3})(%)");

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
        noWifiMatcher = noWifiPattern.matcher(smsText);
        batteryMatcher = batteryPattern.matcher(smsText);
        lowBatteryMatcher = lowBatteryPattern.matcher(smsText);
    }

    private SMS_TYPE getType() {
        SMS_TYPE type = SMS_TYPE.UNDEFINED;

        if (positionMatcher.find() && statusBatteryStatusMatcher.find())
            type = SMS_TYPE.POSITION;
        else if (statusWarningNumberMatcher.find() && statusIntervalMatcher.find() &&
                statusWifiStatusMatcher.find() && statusBatteryStatusMatcher.find())
            type = SMS_TYPE._STATUS;
        else if (statusIntervalMatcher.find() && statusWifiStatusMatcher.find() &&
                statusBatteryStatusMatcher.find()) {
            logViewModelReference.get().w("Warningnumber is not set!");
            type = SMS_TYPE._STATUS;
        } else if (wifiStatusOnMatcher.find() && statusBatteryStatusMatcher.find())
            type = SMS_TYPE.WIFI_ON;
        else if (wifiStatusOffMatcher.find() && statusBatteryStatusMatcher.find())
            type = SMS_TYPE.WIFI_OFF;
        else if (warningNumberMatcher.find() && statusBatteryStatusMatcher.find())
            type = SMS_TYPE.WARNING_NUMBER;
        else if (positionMatcher.find())
            type = SMS_TYPE.CELL_TOWERS;
        else if (wifiMatcher.find() && batteryMatcher.find())
            type = SMS_TYPE.WIFI_LIST;
        else if (noWifiMatcher.find())
            type = SMS_TYPE.NO_WIFI_LIST;
        else if (intervalChangedMatcher.find() && statusBatteryStatusMatcher.find())
            type = SMS_TYPE.INT;
        else if (lowBatteryMatcher.find())
            type = SMS_TYPE.LOW_BATTERY;

        return type;
    }

    public SettingsList getSettingList() {
        SettingsList settings = new SettingsList();

        // add entries based on sms type
        switch (type) {
            case POSITION:
                break;
            case _STATUS:
                settings._add(new WarningNumber(getStatusWarningNumber(), sms))
                        ._add(new Interval(getStatusInterval(), sms))
                        ._add(new Wifi(getStatusWifi(), sms))
                        ._add(new Battery(getStatusBattery(), sms))
                        ._add(new de.bikebean.app.db.settings.settings.Status(0.0, sms));
                break;
            case WIFI_ON:
                settings._add(new Battery(getStatusBattery(), sms))
                        ._add(new Wifi(true, sms))
                        ._add(new de.bikebean.app.db.settings.settings.Status(0.0, sms));
                break;
            case WIFI_OFF:
                settings._add(new Battery(getStatusBattery(), sms))
                        ._add(new Wifi(false, sms))
                        ._add(new de.bikebean.app.db.settings.settings.Status(0.0, sms));
                break;
            case WARNING_NUMBER:
                settings._add(new Battery(getStatusBattery(), sms))
                        ._add(new WarningNumber(getWarningNumber(), sms))
                        ._add(new de.bikebean.app.db.settings.settings.Status(0.0, sms));
                break;
            case INT:
                settings._add(new Battery(getStatusBattery(), sms))
                        ._add(new Interval(getInterval(), sms))
                        ._add(new de.bikebean.app.db.settings.settings.Status(0.0, sms));
                break;
            case CELL_TOWERS:
                // no battery entry in this special case
                settings._add(new CellTowers(getWappCellTowers(), sms))
                        ._add(new Wapp(State.WAPP_CELL_TOWERS, sms));
                break;
            case WIFI_LIST:
                // battery value is encoded differently in this case
                settings._add(new Battery(getBattery(), sms))
                        ._add(new WifiAccessPoints(getWappWifi(), sms))
                        ._add(new Wapp(State.WAPP_WIFI_ACCESS_POINTS, sms));
                break;
            case NO_WIFI_LIST:
                // battery value is encoded differently in this case
                settings._add(new Battery(getBatteryNoWifi(), sms))
                        ._add(new WifiAccessPoints("", sms))
                        ._add(new Wapp(State.WAPP_WIFI_ACCESS_POINTS, sms));
                break;
            case LOW_BATTERY:
                settings._add(new LowBattery(getLowBattery(), sms));
                break;
        }

        return settings;
    }

    /*
    Get results of Sms Parser
    */
    private String getStatusWarningNumber() {
        return getMatcherResult(statusWarningNumberMatcher);
    }

    private int getStatusInterval() {
        return Integer.parseInt(getMatcherResult(statusIntervalMatcher));
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

    private double getBatteryNoWifi() {
        String result = getMatcherResult(noWifiMatcher);

        if (!result.isEmpty())
            return Double.parseDouble(result);
        else return 0.0;
    }

    private double getLowBattery() {
        String result = getMatcherResult(lowBatteryMatcher);

        if (!result.isEmpty())
            return Double.parseDouble(result);
        else return 0.0;
    }

    private int getInterval() {
        return Integer.parseInt(getMatcherResult(intervalChangedMatcher));
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

