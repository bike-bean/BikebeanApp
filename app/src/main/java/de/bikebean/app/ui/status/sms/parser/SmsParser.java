package de.bikebean.app.ui.status.sms.parser;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;

import androidx.preference.PreferenceManager;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.bikebean.app.MainActivity;
import de.bikebean.app.db.sms.Sms;
import de.bikebean.app.ui.status.StatusViewModel;
import de.bikebean.app.ui.status.preferences.PreferenceUpdater;

public class SmsParser extends AsyncTask<String, Void, Boolean> {

    private final static int SMS_TYPE_POSITION = 0;
    private final static int SMS_TYPE_STATUS = 1;
    private final static int SMS_TYPE_WIFI_ON = 2;
    private final static int SMS_TYPE_WIFI_OFF = 3;
    private final static int SMS_TYPE_WARNING_NUMBER = 4;
    private final static int SMS_TYPE_CELL_TOWERS = 50;  // wapp part 1
    private final static int SMS_TYPE_WIFI_LIST = 51;  // wapp part 2
    private final static int SMS_TYPE_INT = 6;

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

    private final WeakReference<StatusViewModel> statusViewModelReference;
    private final WeakReference<AsyncResponse> asyncResponseReference;

    public interface AsyncResponse {
        void onDatabaseUpdated(boolean isDatabaseUpdated);
    }

    private int smsId;

    private ArrayList<de.bikebean.app.db.status.Status> newStatusEntries;
    private SharedPreferences settings;
    private PreferenceUpdater preferenceUpdater;

    public SmsParser(Sms sms,
                     Context context,
                     StatusViewModel statusViewModel,
                     AsyncResponse delegate) {
        statusViewModelReference = new WeakReference<>(statusViewModel);
        asyncResponseReference = new WeakReference<>(delegate);

        settings = PreferenceManager.getDefaultSharedPreferences(context);
        preferenceUpdater = new PreferenceUpdater();
        newStatusEntries = new ArrayList<>();

        initMatchers(sms.getBody());
        smsId = sms.getId();
    }

    @Override
    protected Boolean doInBackground(String... args) {
        // Parse Sms to get which type it is
        int type = getType();
        Log.d(MainActivity.TAG, String.format("Detected Type %d", type));

        // Update the status entries for the status db and the user preferences
        loadNewStatusEntries(type);
        updatePreferences(settings, type);

        // add each status entry to the status viewModel ( -> database )
        for (de.bikebean.app.db.status.Status status : newStatusEntries)
            statusViewModelReference.get().insert(status);

        return true;
    }

    @Override
    protected void onPostExecute(Boolean isDatabaseUpdated) {
        asyncResponseReference.get().onDatabaseUpdated(isDatabaseUpdated);
    }

    private void initMatchers(String smsText) {
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

    private void loadNewStatusEntries(int type) {
        // Add the battery entry as it will be present most of the time
        newStatusEntries.add(new de.bikebean.app.db.status.Status(
                System.currentTimeMillis(),
                de.bikebean.app.db.status.Status.KEY_BATTERY,
                getStatusBattery(),
                "",
                de.bikebean.app.db.status.Status.STATUS_CONFIRMED,
                smsId)
        );

        // add further entries based on sms type
        switch (type) {
            case SMS_TYPE_POSITION:
                // TODO: Update Position Setting
                break;
            case SMS_TYPE_CELL_TOWERS:
                newStatusEntries.remove(0); // remove the battery entry in this special case
                newStatusEntries.add(new de.bikebean.app.db.status.Status(
                        System.currentTimeMillis(),
                        de.bikebean.app.db.status.Status.KEY_CELL_TOWERS,
                        0.0,
                        getWappCellTowers(),
                        de.bikebean.app.db.status.Status.STATUS_PENDING,
                        smsId)
                );
                break;
            case SMS_TYPE_WIFI_LIST:
                double batteryStatus = getBattery();
                Log.d(MainActivity.TAG, "batteryStatus: " + batteryStatus);
                // battery value is encoded differently in this case
                newStatusEntries.get(0).setValue(batteryStatus);
                newStatusEntries.add(new de.bikebean.app.db.status.Status(
                        System.currentTimeMillis(),
                        de.bikebean.app.db.status.Status.KEY_WIFI_ACCESS_POINTS,
                        0.0,
                        getWappWifi(),
                        de.bikebean.app.db.status.Status.STATUS_PENDING,
                        smsId)
                );
                break;
            case SMS_TYPE_STATUS:
            case SMS_TYPE_INT:
            case SMS_TYPE_WIFI_ON:
            case SMS_TYPE_WIFI_OFF:
            case SMS_TYPE_WARNING_NUMBER:
                newStatusEntries.add(new de.bikebean.app.db.status.Status(
                        System.currentTimeMillis(),
                        de.bikebean.app.db.status.Status.KEY_STATUS,
                        0.0,
                        "",
                        de.bikebean.app.db.status.Status.STATUS_CONFIRMED,
                        smsId)
                );
        }
    }

    private void updatePreferences(SharedPreferences settings, int type) {
        // only update the preferences settings here (which the user can control directly)
        switch (type) {
            case SMS_TYPE_STATUS:
                preferenceUpdater.updateWarningNumber(settings, getStatusWarningNumber());
                preferenceUpdater.updateInterval(settings, getStatusInterval());
                preferenceUpdater.updateWifi(settings, getStatusWifi());
                break;
            case SMS_TYPE_WIFI_ON:
                preferenceUpdater.updateWifi(settings, true);
                break;
            case SMS_TYPE_WIFI_OFF:
                preferenceUpdater.updateWifi(settings, false);
                break;
            case SMS_TYPE_WARNING_NUMBER:
                preferenceUpdater.updateWarningNumber(settings, getWarningNumber());
                break;
            case SMS_TYPE_INT:
                preferenceUpdater.updateInterval(settings, getInterval());
                break;
        }
    }

    private int getType() {
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
}

